package org.complitex.keconnection.heatmeter.service.consumption;

import com.google.common.base.Strings;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.complitex.address.entity.ExternalAddress;
import org.complitex.address.entity.LocalAddress;
import org.complitex.address.strategy.building.BuildingStrategy;
import org.complitex.address.strategy.building.entity.BuildingCode;
import org.complitex.common.entity.Cursor;
import org.complitex.common.entity.FilterWrapper;
import org.complitex.common.service.BroadcasterService;
import org.complitex.common.util.DateUtil;
import org.complitex.common.util.StringUtil;
import org.complitex.correction.exception.ResolveAddressException;
import org.complitex.correction.service.AddressCorrectionService;
import org.complitex.keconnection.heatmeter.entity.consumption.CentralHeatingConsumption;
import org.complitex.keconnection.heatmeter.entity.consumption.ConsumptionFile;
import org.complitex.keconnection.heatmeter.entity.consumption.ConsumptionFileStatus;
import org.complitex.keconnection.heatmeter.entity.consumption.ConsumptionStatus;
import org.complitex.keconnection.heatmeter.entity.cursor.ComMeter;
import org.complitex.keconnection.heatmeter.service.ExternalHeatmeterService;
import org.complitex.keconnection.organization.strategy.KeOrganizationStrategy;
import org.complitex.organization.strategy.ServiceStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.complitex.keconnection.heatmeter.entity.consumption.ConsumptionStatus.*;

/**
 * @author inheaven on 20.03.2015 0:57.
 */
@Stateless
public class CentralHeatingConsumptionService {
    private Logger log = LoggerFactory.getLogger(CentralHeatingConsumptionService.class);

    @EJB
    private ConsumptionFileBean consumptionFileBean;

    @EJB
    private CentralHeatingConsumptionBean centralHeatingConsumptionBean;

    @EJB
    private BroadcasterService broadcasterService;

    @EJB
    private AddressCorrectionService addressCorrectionService;

    @EJB
    private ExternalHeatmeterService externalHeatmeterService;

    @EJB
    private BuildingStrategy buildingStrategy;

    @EJB
    private ServiceStrategy serviceStrategy;

    @EJB
    private KeOrganizationStrategy organizationStrategy;

    @Asynchronous
    public void load(Date om, Long serviceProviderId, Long serviceId, String fileName, String checkSum, InputStream inputStream){
        try {
            if (fileName.length() > 255) {
                fileName = fileName.substring(0, 255);
            }

            HSSFWorkbook workbook = new HSSFWorkbook(inputStream);

            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

            HSSFSheet sheet = workbook.getSheetAt(0);

            ConsumptionFile consumptionFile = new ConsumptionFile();
            consumptionFile.setOm(om);
            consumptionFile.setServiceProviderId(serviceProviderId);
            consumptionFile.setServiceId(serviceId);
            consumptionFile.setName(fileName);
            consumptionFile.setCheckSum(checkSum);
            consumptionFile.setStatus(ConsumptionFileStatus.LOADING);
            consumptionFile.setLoaded(DateUtil.now());

            consumptionFileBean.save(consumptionFile);

            broadcasterService.broadcast(this, consumptionFile);

            try {
                sheet.forEach(r -> {
                    if (r.getRowNum() >= 9) {
                        CentralHeatingConsumption c = new CentralHeatingConsumption(consumptionFile.getId());

                        c.setNumber(getStringValue(evaluator, r.getCell(0)));
                        c.setDistrictCode(getStringValue(evaluator, r.getCell(1)));
                        c.setOrganizationCode(getStringValue(evaluator, r.getCell(2)));
                        c.setBuildingCode(getStringValue(evaluator, r.getCell(3)));
                        c.setAccountNumber(getStringValue(evaluator, r.getCell(4)));
                        c.setStreet(Optional.ofNullable(getStringValue(evaluator, r.getCell(5)))
                                .map(String::toUpperCase).orElse(null));
                        c.setBuildingNumber(Optional.ofNullable(getStringValue(evaluator, r.getCell(6)))
                                .map(String::toUpperCase).orElse(null));
                        c.setCommonVolume(getStringValue(evaluator, r.getCell(7)));
                        c.setApartmentRange(getStringValue(evaluator, r.getCell(8)));
                        c.setBeginDate(getStringValue(evaluator, r.getCell(9)));
                        c.setEndDate(getStringValue(evaluator, r.getCell(10)));

                        c.setStatus(ConsumptionStatus.LOADED);

                        if (!c.isEmpty()) {
                            centralHeatingConsumptionBean.save(c);
                        }
                    }
                });

                consumptionFile.setStatus(ConsumptionFileStatus.LOADED);
                consumptionFileBean.save(consumptionFile);

                broadcasterService.broadcast(this, consumptionFile);
            } catch (Exception e) {
                consumptionFile.setStatus(ConsumptionFileStatus.LOAD_ERROR);
                consumptionFileBean.save(consumptionFile);

                log.error("central heating consumption load error", e);
                broadcasterService.broadcast(this, consumptionFile);
            }
        } catch (Exception e) {
            log.error("error load central heating consumption xls file", e);
        }
    }

    private String getStringValue(FormulaEvaluator evaluator, Cell cell){
        if (cell == null){
            return null;
        }

        if (cell.getCellType() == Cell.CELL_TYPE_FORMULA){
            return toString(evaluator.evaluate(cell).getNumberValue());
        }

        return getStringValue(cell);
    }

    private String getStringValue(Cell cell){
        if (cell == null){
            return null;
        }

        if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
            if (HSSFDateUtil.isCellDateFormatted(cell)){
                return DateUtil.getDateFormat().format(cell.getDateCellValue());
            }

            return toString(cell.getNumericCellValue());
        }

        return Strings.emptyToNull(StringUtil.toCyrillic(cell.getStringCellValue().trim()));
    }


    private String toString(double value){
        return value == (long) value ? Long.toString((long) value) : Double.toString(value);
    }

    @Asynchronous
    public void bind(ConsumptionFile consumptionFile){
        consumptionFile.setStatus(ConsumptionFileStatus.BINDING);
        consumptionFileBean.save(consumptionFile);

        broadcasterService.broadcast(this, consumptionFile);

        List<CentralHeatingConsumption> consumptions = centralHeatingConsumptionBean.getCentralHeatingConsumptions(
                FilterWrapper.of(new CentralHeatingConsumption(consumptionFile.getId())));

        consumptions.parallelStream().forEach(c -> bind(consumptionFile, c));

        boolean notBound = consumptions.parallelStream()
                .filter(c -> !c.getStatus().equals(ConsumptionStatus.BOUND))
                .findAny()
                .isPresent();

        consumptionFile.setStatus(notBound ? ConsumptionFileStatus.BIND_ERROR : ConsumptionFileStatus.BOUND);
        consumptionFileBean.save(consumptionFile);

        broadcasterService.broadcast(this, consumptionFile);
    }

    public void bind(ConsumptionFile consumptionFile, CentralHeatingConsumption c) {
        //validation
        if (Strings.isNullOrEmpty(c.getBuildingNumber())) {
            c.setStatus(ConsumptionStatus.VALIDATION_BUILDING_ERROR);
            centralHeatingConsumptionBean.save(c);
            return;
        }

        if (Strings.isNullOrEmpty(c.getStreet())) {
            c.setStatus(ConsumptionStatus.VALIDATION_STREET_ERROR);
            centralHeatingConsumptionBean.save(c);
            return;
        }

        ExternalAddress externalAddress = c.getExternalAddress();

        //street type
        if (Strings.isNullOrEmpty(externalAddress.getStreetType())){
            c.setStatus(ConsumptionStatus.VALIDATION_STREET_TYPE_ERROR);
            centralHeatingConsumptionBean.save(c);
            return;
        }

        if (Strings.isNullOrEmpty(externalAddress.getStreet())) {
            c.setStatus(ConsumptionStatus.VALIDATION_STREET_ERROR);
            centralHeatingConsumptionBean.save(c);
            return;
        }

        //resolve address
        try {
            externalAddress.setOrganizationId(consumptionFile.getServiceProviderId());
            externalAddress.setUserOrganizationId(consumptionFile.getUserOrganizationId());

            LocalAddress localAddress = addressCorrectionService.resolveLocalAddress(externalAddress);

            c.setLocalAddress(localAddress);

            switch (localAddress.getFirstEmptyAddressEntity()){
                case STREET_TYPE:
                    c.setStatus(LOCAL_STREET_TYPE_UNRESOLVED);
                    break;
                case CITY:
                case STREET:
                    c.setStatus(LOCAL_STREET_UNRESOLVED);
                    break;
                case BUILDING:
                    c.setStatus(LOCAL_BUILDING_UNRESOLVED);
                    break;
                default:
                    c.setStatus(BINDING);
            }

            if (c.getStatus() != BINDING){
                centralHeatingConsumptionBean.save(c);
                return;
            }
        } catch (ResolveAddressException e) {
            c.setStatus(BIND_ERROR);
            c.setMessage(e.getMessage());
            centralHeatingConsumptionBean.save(c);

            log.error("consumption file bind error", e);

            return;
        }

        //meter cursor
        BuildingCode buildingCode = buildingStrategy.getBuildingCode(c.getLocalAddress().getBuildingId(),
                c.getOrganizationCode());

        if (buildingCode == null){
            c.setStatus(LOCAL_BUILDING_CODE_UNRESOLVED);
            centralHeatingConsumptionBean.save(c);
            return;
        }

        String dataSource = organizationStrategy.getDataSource(consumptionFile.getUserOrganizationId(),
                consumptionFile.getServiceId());
        String serviceCode = serviceStrategy.getDomainObject(consumptionFile.getServiceId(), true)
                .getStringValue(ServiceStrategy.CODE);

        Cursor<ComMeter> cursor;

        try {
            cursor = externalHeatmeterService.getComMeterCursor(dataSource, c.getOrganizationCode(),
                    buildingCode.getBuildingCode(), consumptionFile.getOm(), serviceCode);

            switch (cursor.getResultCode()){
                case 0:
                    c.setStatus(METER_NOT_FOUND);
                    c.setMessage("METER_NOT_FOUND");
                    break;
                case -1:
                    c.setStatus(METER_NOT_FOUND);
                    c.setMessage("ORGANIZATION_NOT_FOUND");
                    break;
                case -2:
                    c.setStatus(METER_NOT_FOUND);
                    c.setMessage("BUILDING_NOT_FOUND");
                    break;
                case -3:
                    c.setStatus(METER_NOT_FOUND);
                    c.setMessage("NOT_ACTUAL_METER");
                    break;
                case -4:
                    c.setStatus(METER_NOT_FOUND);
                    c.setMessage("SERVICE_NOT_FOUND");
                    break;
            }

            if (cursor.getList().isEmpty()){
                c.setStatus(METER_NOT_FOUND);
                c.setMessage("EMPTY_METER_LIST");
            }else if (cursor.getList().size() > 1){
                c.setStatus(METER_NOT_FOUND);
                c.setMessage("MORE_THEN_ONE_METER");
            }

            if (c.getStatus() != BINDING){
                centralHeatingConsumptionBean.save(c);
                return;
            }

            c.setMeterId(cursor.getList().get(0).getmId());
            c.setStatus(BOUND);
            centralHeatingConsumptionBean.save(c);
        } catch (Exception e) {
            c.setStatus(BIND_ERROR);
            c.setMessage(e.getMessage());
            centralHeatingConsumptionBean.save(c);

            log.error("consumption file bind error", e);
        }
    }
}
