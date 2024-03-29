package ru.complitex.keconnection.heatmeter.service;

import au.com.bytecode.opencsv.CSVReader;
import ru.complitex.address.strategy.building.BuildingStrategy;
import ru.complitex.common.entity.IImportFile;
import ru.complitex.common.service.AbstractImportService;
import ru.complitex.common.service.ConfigBean;
import ru.complitex.common.service.IImportListener;
import ru.complitex.common.service.IProcessListener;
import ru.complitex.common.exception.ConcurrentModificationException;
import ru.complitex.common.exception.ImportFileNotFoundException;
import ru.complitex.common.exception.ImportFileReadException;
import ru.complitex.common.util.StringUtil;
import ru.complitex.keconnection.heatmeter.entity.*;
import ru.complitex.keconnection.heatmeter.service.exception.*;
import ru.complitex.keconnection.organization.strategy.KeOrganizationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static ru.complitex.keconnection.heatmeter.entity.HeatmeterPeriodSubType.OPERATING;
import static ru.complitex.keconnection.heatmeter.entity.HeatmeterType.HEATING_AND_WATER;
import static ru.complitex.keconnection.organization.strategy.KeOrganizationStrategy.KE_ORGANIZATION_OBJECT_ID;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 10.09.12 17:53
 */
@Stateless
public class HeatmeterImportService extends AbstractImportService {
    private final Logger log = LoggerFactory.getLogger(HeatmeterImportService.class);

    @EJB
    private ConfigBean configBean;

    @EJB
    private HeatmeterBean heatmeterBean;

    @EJB
    private KeOrganizationStrategy organizationStrategy;

    @EJB
    private BuildingStrategy buildingStrategy;

    @Asynchronous
    public void asyncUploadHeatmeters(String fileName, InputStream inputStream, Date beginOm, Date beginDate,
            IProcessListener<HeatmeterWrapper> listener) {
        uploadHeatmeters(fileName, inputStream, beginOm, beginDate, listener);
    }

    public void process(final IImportFile importFile, final IImportListener listener, final Date beginOm, Date beginDate)
            throws ImportFileNotFoundException, ImportFileReadException {
        int size = getRecordCount(importFile);

        listener.beginImport(importFile, size);

        uploadHeatmeters(importFile.getFileName(), getInputStream(importFile.getFileName()), beginOm, beginDate,
                new IProcessListener<HeatmeterWrapper>() {

                    int index = 0;
                    int processed = 0;

                    @Override
                    public void processed(HeatmeterWrapper object) {
                        index++;
                        processed++;

                        listener.recordProcessed(importFile, index);
                    }

                    @Override
                    public void skip(HeatmeterWrapper object) {
                        index++;
                    }

                    @Override
                    public void error(HeatmeterWrapper object, Exception e) {
                        listener.warn(importFile, e.getMessage());
                    }

                    @Override
                    public void done() {
                        listener.completeImport(importFile, processed);
                    }
                });
    }

    private void uploadHeatmeters(String fileName, InputStream inputStream, Date beginOm, Date beginDate,
            IProcessListener<HeatmeterWrapper> listener) {
        HeatmeterWrapper heatmeaterWrapper = null;

        int lineNum = 0;

        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            CSVReader reader = new CSVReader(bufferedReader, ';');

            String[] line;

            //skip first line
            reader.readNext();
            lineNum++;


            //"GEK";"DOM";"UL";"NDOM";"LOTOP0";"LOTOP1";"LOTOP2";"LOTOP3";"LOTOP4"
            while ((line = reader.readNext()) != null) {
                lineNum++;

                for (int i = 0; i < 5; ++i) {
                    String ls = line[4 + i];

                    //skip zero or empty lotop
                    if (ls.isEmpty() || ls.equals("0")) {
                        continue;
                    }

                    String orgCode = line[0];
                    //organization leading zero
                    if (orgCode.length() < 4 && StringUtil.isNumeric(orgCode)) {
                        orgCode = String.format("%04d", StringUtil.parseInt(orgCode));
                    }

                    heatmeaterWrapper = new HeatmeterWrapper(lineNum);

                    heatmeaterWrapper.setOrganizationCode(orgCode);
                    heatmeaterWrapper.setBuildingCode(line[1]);
                    heatmeaterWrapper.setLs(ls);
                    heatmeaterWrapper.setAddress(line[2] + " " + line[3]);
                    heatmeaterWrapper.setFileName(fileName);
                    heatmeaterWrapper.setBeginOm(beginOm);
                    heatmeaterWrapper.setEndOm(HeatmeterPeriod.DEFAULT_END_OM);
                    heatmeaterWrapper.setBeginDate(beginDate);
                    heatmeaterWrapper.setEndDate(HeatmeterPeriod.DEFAULT_END_DATE);

                    //create heatmeter
                    try {
                        createHeatmeter(heatmeaterWrapper);

                        listener.processed(heatmeaterWrapper);
                    } catch (BuildingNotFoundException | OrganizationNotFoundException | NumberLsException e) {
                        listener.error(heatmeaterWrapper, e);
                    } catch (DuplicateException e) {
                        listener.skip(heatmeaterWrapper);
                    }
                }
            }

            //manual close stream
            bufferedReader.close();

            //done
            listener.done();
        } catch (Exception e) {
            listener.error(heatmeaterWrapper, new CriticalHeatmeaterImportException(e, heatmeaterWrapper));
            listener.done();

            log.error("Ошибка импорта счетчика {} ", heatmeaterWrapper, e);
        }
    }

    public void createHeatmeter(HeatmeterWrapper heatmeaterWrapper) throws BuildingNotFoundException,
            OrganizationNotFoundException, DuplicateException, NumberLsException, ConcurrentModificationException {
        //find organization by code
        Long organizationId = organizationStrategy.getObjectIdByCode(heatmeaterWrapper.getOrganizationCode());

        if (organizationId == null) {
            throw new OrganizationNotFoundException(heatmeaterWrapper);
        }

        //find building code
        Long buildingCodeId = buildingStrategy.getBuildingCodeId(organizationId, heatmeaterWrapper.getBuildingCode());

        if (buildingCodeId == null) {
            throw new BuildingNotFoundException(heatmeaterWrapper);
        }

        //ls
        Integer ls;
        try {
            ls = Integer.parseInt(heatmeaterWrapper.getLs());
        } catch (NumberFormatException e) {
            throw new NumberLsException(heatmeaterWrapper);
        }

        //check duplicates
        if (heatmeterBean.isExist(ls, buildingCodeId)) {
            throw new DuplicateException();
        }

        //heatmeter
        Heatmeter heatmeter = heatmeterBean.getHeatmeterByLs(ls, KE_ORGANIZATION_OBJECT_ID);

        if (heatmeter == null) {
            heatmeter = new Heatmeter();
            heatmeter.setLs(ls);

            //default type
            heatmeter.setType(HEATING_AND_WATER);

            //default organization
            heatmeter.setOrganizationId(KE_ORGANIZATION_OBJECT_ID);

            //default calculating
            heatmeter.setCalculating(true);

            //create period
            HeatmeterOperation operation = new HeatmeterOperation(heatmeter.getId(), heatmeaterWrapper.getBeginOm(), OPERATING);

            operation.setEndOm(heatmeaterWrapper.getEndOm());
            operation.setBeginDate(heatmeaterWrapper.getBeginDate());
            operation.setEndDate(heatmeaterWrapper.getEndDate());

            heatmeter.getOperations().add(operation);
        }

        //create heatmeter connection
        HeatmeterConnection connection = new HeatmeterConnection();

        connection.setHeatmeterId(heatmeter.getId());
        connection.setObjectId(buildingCodeId);

        connection.setBeginOm(heatmeaterWrapper.getBeginOm());
        connection.setEndOm(heatmeaterWrapper.getEndOm());
        connection.setBeginDate(heatmeaterWrapper.getBeginDate());
        connection.setEndDate(heatmeaterWrapper.getEndDate());

        heatmeter.getConnections().add(connection);

        //save
        heatmeterBean.save(heatmeter);
    }

    public List<HeatmeterImportFile> getHeatmeterImportFiles() {
        List<HeatmeterImportFile> importFiles = new ArrayList<>();

        String[] names = getFileList(getDir(), "csv");

        if (names != null) {
            for (String name : names) {
                importFiles.add(new HeatmeterImportFile(name));
            }
        }

        return importFiles;
    }

    @Override
    protected String getDir() {
        return configBean.getString(HeatmeterConfig.IMPORT_HEATMETER_DIR, true);
    }
}
