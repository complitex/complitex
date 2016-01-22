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
import org.complitex.common.entity.FilterWrapper;
import org.complitex.common.service.BroadcastService;
import org.complitex.common.util.DateUtil;
import org.complitex.common.util.StringUtil;
import org.complitex.correction.exception.ResolveAddressException;
import org.complitex.correction.service.AddressCorrectionService;
import org.complitex.keconnection.heatmeter.entity.consumption.CentralHeatingConsumption;
import org.complitex.keconnection.heatmeter.entity.consumption.ConsumptionFile;
import org.complitex.keconnection.heatmeter.entity.consumption.ConsumptionFileStatus;
import org.complitex.keconnection.heatmeter.entity.consumption.ConsumptionStatus;
import org.complitex.keconnection.heatmeter.entity.cursor.ComMeterCursor;
import org.complitex.keconnection.heatmeter.service.ExternalHeatmeterService;
import org.complitex.keconnection.organization.strategy.KeOrganizationStrategy;
import org.complitex.organization.strategy.ServiceStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.ejb.*;
import javax.inject.Inject;
import javax.transaction.UserTransaction;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.complitex.keconnection.heatmeter.entity.consumption.ConsumptionStatus.*;

/**
 * @author inheaven on 20.03.2015 0:57.
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class CentralHeatingConsumptionService {
    private Logger log = LoggerFactory.getLogger(CentralHeatingConsumptionService.class);

    @EJB
    private ConsumptionFileBean consumptionFileBean;

    @EJB
    private CentralHeatingConsumptionBean centralHeatingConsumptionBean;
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

    @Resource
    private UserTransaction userTransaction;

    @Inject
    private BroadcastService broadcastService;

    @Asynchronous
    public void load(Date om, Long serviceProviderId, Long serviceId, Long userOrganizationId, String fileName,
                     String checkSum, InputStream inputStream){
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
            consumptionFile.setUserOrganizationId(userOrganizationId);
            consumptionFile.setName(fileName);
            consumptionFile.setCheckSum(checkSum);
            consumptionFile.setStatus(ConsumptionFileStatus.LOADING);
            consumptionFile.setLoaded(DateUtil.now());

            userTransaction.begin();
            consumptionFileBean.save(consumptionFile);
            userTransaction.commit();

            broadcastService.broadcast(this, consumptionFile);


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
                userTransaction.begin();
                consumptionFileBean.save(consumptionFile);
                userTransaction.commit();

                broadcastService.broadcast(this, consumptionFile);
            } catch (Exception e) {
                consumptionFile.setStatus(ConsumptionFileStatus.LOAD_ERROR);
                consumptionFileBean.save(consumptionFile);

                log.error("central heating consumption load error", e);
                broadcastService.broadcast(this, consumptionFile);
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
        try {
            consumptionFile.setStatus(ConsumptionFileStatus.BINDING);

            userTransaction.begin();
            consumptionFileBean.save(consumptionFile);
            userTransaction.commit();

            broadcastService.broadcast(this, consumptionFile);

            List<CentralHeatingConsumption> consumptions = centralHeatingConsumptionBean.getCentralHeatingConsumptions(
                    FilterWrapper.of(new CentralHeatingConsumption(consumptionFile.getId())));

            consumptions.parallelStream().forEach(c -> bind(consumptionFile, c));

            boolean notBound = consumptions.parallelStream()
                    .filter(c -> !c.getStatus().equals(ConsumptionStatus.BOUND))
                    .findAny()
                    .isPresent();

            consumptionFile.setStatus(notBound ? ConsumptionFileStatus.BIND_ERROR : ConsumptionFileStatus.BOUND);

            userTransaction.begin();
            consumptionFileBean.save(consumptionFile);
            userTransaction.commit();

            broadcastService.broadcast(this, consumptionFile);
        }catch (Exception e) {
            log.error("bind error", e);
        }
    }

    public void bind(ConsumptionFile consumptionFile, CentralHeatingConsumption c) {
        //validation
        if (Strings.isNullOrEmpty(c.getBuildingNumber())) {
            c.setStatus(VALIDATION_BUILDING_ERROR);
            centralHeatingConsumptionBean.save(c);
            return;
        }

        if (Strings.isNullOrEmpty(c.getStreet())) {
            c.setStatus(VALIDATION_STREET_ERROR);
            centralHeatingConsumptionBean.save(c);
            return;
        }

        ExternalAddress externalAddress = c.getExternalAddress();

        //street type
        if (Strings.isNullOrEmpty(externalAddress.getStreetType())){
            c.setStatus(VALIDATION_STREET_TYPE_ERROR);
            centralHeatingConsumptionBean.save(c);
            return;
        }

        if (Strings.isNullOrEmpty(externalAddress.getStreet())) {
            c.setStatus(VALIDATION_STREET_ERROR);
            centralHeatingConsumptionBean.save(c);
            return;
        }

        //apartment range
        if (!Strings.isNullOrEmpty(c.getApartmentRange())){
            c.setStatus(VALIDATION_APARTMENT_ERROR);
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
        ComMeterCursor cursor = getComMeterCursor(consumptionFile, c);

        if (cursor.getBuildingCode() == null){
            c.setStatus(LOCAL_BUILDING_CODE_UNRESOLVED);
            centralHeatingConsumptionBean.save(c);
            return;
        }

        try {
            externalHeatmeterService.callComMeterCursor(cursor);

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

            if (cursor.getData().isEmpty()){
                c.setStatus(METER_NOT_FOUND);
                c.setMessage("EMPTY_METER_LIST");
            }else if (cursor.getData().size() > 1){
                c.setStatus(METER_NOT_FOUND);
                c.setMessage("MORE_THEN_ONE_METER");
            }

            if (c.getStatus() != BINDING){
                centralHeatingConsumptionBean.save(c);
                return;
            }

            c.setMeterId(cursor.getData().get(0).getMId());
            c.setStatus(BOUND);
            centralHeatingConsumptionBean.save(c);
        } catch (Exception e) {
            c.setStatus(BIND_ERROR);
            c.setMessage(e.getMessage());
            centralHeatingConsumptionBean.save(c);

            log.error("consumption file bind error", e);
        }
    }

    public ComMeterCursor getComMeterCursor(ConsumptionFile consumptionFile, CentralHeatingConsumption c) {
        Long buildingCode = null;

        if (c.getLocalAddress().getBuildingId() != null) {
            buildingCode = buildingStrategy.getBuildingCode(c.getLocalAddress().getBuildingId(),
                    c.getOrganizationCode()).getBuildingCode();
        }

        String dataSource = null;
        try {
            dataSource = organizationStrategy.getDataSource(consumptionFile.getUserOrganizationId(),
                    consumptionFile.getServiceId());
        } catch (Exception e) {
            //datasource
        }

        String serviceCode = null;
        try {
            serviceCode = serviceStrategy.getDomainObject(consumptionFile.getServiceId(), true)
                    .getStringValue(ServiceStrategy.CODE);
        } catch (Exception e) {
            //serviceCode
        }

        return new ComMeterCursor(dataSource, c.getOrganizationCode(), buildingCode, consumptionFile.getOm(), serviceCode);
    }
}
