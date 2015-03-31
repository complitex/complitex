package org.complitex.keconnection.heatmeter.service.consumption;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.complitex.common.util.DateUtil;
import org.complitex.keconnection.heatmeter.entity.consumption.CentralHeatingConsumption;
import org.complitex.keconnection.heatmeter.entity.consumption.ConsumptionFile;
import org.complitex.keconnection.heatmeter.entity.consumption.ConsumptionFileStatus;
import org.complitex.keconnection.heatmeter.entity.consumption.ConsumptionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

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
        
        return cell.getStringCellValue();
    }

    private String toString(double value){
        return value == (long) value ? Long.toString((long) value) : Double.toString(value);
    }
}
