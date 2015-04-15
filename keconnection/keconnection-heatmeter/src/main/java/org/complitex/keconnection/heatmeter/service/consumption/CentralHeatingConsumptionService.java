package org.complitex.keconnection.heatmeter.service.consumption;

import com.google.common.base.Strings;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.complitex.address.entity.ExternalAddress;
import org.complitex.address.entity.LocalAddress;
import org.complitex.common.entity.FilterWrapper;
import org.complitex.common.service.BroadcasterService;
import org.complitex.common.util.DateUtil;
import org.complitex.correction.exception.ResolveAddressException;
import org.complitex.correction.service.AddressCorrectionService;
import org.complitex.keconnection.heatmeter.entity.consumption.CentralHeatingConsumption;
import org.complitex.keconnection.heatmeter.entity.consumption.ConsumptionFile;
import org.complitex.keconnection.heatmeter.entity.consumption.ConsumptionFileStatus;
import org.complitex.keconnection.heatmeter.entity.consumption.ConsumptionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            consumptionFile.setStatus(ConsumptionFileStatus.LOADED);
            consumptionFile.setLoaded(DateUtil.now());

            consumptionFileBean.save(consumptionFile);

            sheet.forEach(r ->{
                if (r.getRowNum() >= 9){
                    CentralHeatingConsumption c = new CentralHeatingConsumption(consumptionFile.getId());

                    c.setNumber(getStringValue(evaluator, r.getCell(0)));
                    c.setDistrictCode(getStringValue(evaluator, r.getCell(1)));
                    c.setOrganizationCode(getStringValue(evaluator, r.getCell(2)));
                    c.setBuildingCode(getStringValue(evaluator, r.getCell(3)));
                    c.setAccountNumber(getStringValue(evaluator, r.getCell(4)));
                    c.setStreet(getStringValue(evaluator, r.getCell(5)));
                    c.setBuildingNumber(getStringValue(evaluator, r.getCell(6)));
                    c.setCommonVolume(getStringValue(evaluator, r.getCell(7)));
                    c.setApartmentRange(getStringValue(evaluator, r.getCell(8)));
                    c.setBeginDate(getStringValue(evaluator, r.getCell(9)));
                    c.setEndDate(getStringValue(evaluator, r.getCell(10)));

                    c.setStatus(ConsumptionStatus.LOADED); //todo validate

                    if (!c.isEmpty()) {
                        centralHeatingConsumptionBean.save(c);
                    }
                }
            });
        } catch (IOException e) {
            log.error("error load central heating consumption xls file");
        }
    }

    private String getStringValue(FormulaEvaluator evaluator, Cell cell){
        if (cell == null){
            return null;
        }else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC){

            return toString(cell.getNumericCellValue());
        }else if (cell.getCellType() == Cell.CELL_TYPE_FORMULA){

            return toString(evaluator.evaluate(cell).getNumberValue());
        }
        
        return cell.getStringCellValue().trim();
    }

    private String toString(double value){
        return value == (long) value ? Long.toString((long) value) : Double.toString(value);
    }

    @Asynchronous
    public void bind(ConsumptionFile consumptionFile){
        consumptionFile.setStatus(ConsumptionFileStatus.BINDING);
        consumptionFileBean.save(consumptionFile);

        broadcasterService.broadcast(getClass().getName(), consumptionFile);

        List<CentralHeatingConsumption> consumptions = centralHeatingConsumptionBean.getCentralHeatingConsumptions(
                FilterWrapper.of(new CentralHeatingConsumption(consumptionFile.getId())));

        String city = "КИЇВ";
        Pattern pattern = Pattern.compile("(\\S*([\\.|\\s]))(.*)");

        consumptions.parallelStream().forEach(c -> {
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

            Matcher m = pattern.matcher(c.getStreet());

            if (!m.matches()){
                c.setStatus(ConsumptionStatus.VALIDATION_STREET_TYPE_ERROR);
                centralHeatingConsumptionBean.save(c);
                return;
            }

            String streetType = m.group(1).replace('.', ' ').trim();

            if (streetType.isEmpty()) {
                c.setStatus(ConsumptionStatus.VALIDATION_STREET_TYPE_ERROR);
                centralHeatingConsumptionBean.save(c);
                return;
            }

            String street = m.group(2).trim();

            if (street.isEmpty()) {
                c.setStatus(ConsumptionStatus.VALIDATION_STREET_ERROR);
                centralHeatingConsumptionBean.save(c);
                return;
            }

            //resolve address

            try {
                LocalAddress localAddress = addressCorrectionService.resolveLocalAddress(
                        ExternalAddress.of(city, streetType, street, c.getBuildingNumber(),
                                consumptionFile.getServiceProviderId(), consumptionFile.getUserOrganizationId())
                );

                c.setLocalAddress(localAddress);

                switch (localAddress.getFirstEmptyAddressEntity()){
                    case STREET_TYPE:
                        c.setStatus(ConsumptionStatus.LOCAL_STREET_TYPE_UNRESOLVED);
                        break;
                    case CITY:
                    case STREET:
                        c.setStatus(ConsumptionStatus.LOCAL_STREET_UNRESOLVED);
                        break;
                    case BUILDING:
                        c.setStatus(ConsumptionStatus.LOCAL_BUILDING_UNRESOLVED);
                        break;
                    default:
                        c.setStatus(ConsumptionStatus.BOUND);
                }

                centralHeatingConsumptionBean.save(c);
            } catch (ResolveAddressException e) {
                c.setStatus(ConsumptionStatus.BIND_ERROR);
                c.setMessage(e.getMessage());

                log.error("consumption file bind error", e);
            }
        });

        boolean notBound = consumptions.parallelStream()
                .filter(c -> !c.getStatus().equals(ConsumptionStatus.BOUND))
                .findAny()
                .isPresent();

        consumptionFile.setStatus(notBound ? ConsumptionFileStatus.BIND_ERROR : ConsumptionFileStatus.BOUND);
        consumptionFileBean.save(consumptionFile);

        broadcasterService.broadcast(getClass().getName(), consumptionFile);
    }
}
