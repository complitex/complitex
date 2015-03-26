package org.complitex.keconnection.heatmeter.service.consumption;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
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

                    c.setNumber(getStringValue(r.getCell(0)));
                    c.setDistrictCode(getStringValue(r.getCell(1)));
                    c.setOrganizationCode(getStringValue(r.getCell(2)));
                    c.setBuildingCode(getStringValue(r.getCell(3)));
                    c.setAccountNumber(getStringValue(r.getCell(4)));
                    c.setStreet(getStringValue(r.getCell(5)));
                    c.setBuildingNumber(getStringValue(r.getCell(6)));
                    c.setCommonVolume(getStringValue(r.getCell(7)));
                    c.setApartmentRange(getStringValue(r.getCell(8)));
                    c.setBeginDate(getStringValue(r.getCell(9)));
                    c.setEndDate(getStringValue(r.getCell(10)));
                    c.setCommonArea(getStringValue(r.getCell(11)));
                    c.setMeterVolume(getStringValue(r.getCell(12)));
                    c.setMeterArea(getStringValue(r.getCell(13)));
                    c.setCommonRentArea(getStringValue(r.getCell(14)));
                    c.setMeterRentVolume(getStringValue(r.getCell(15)));
                    c.setMeterRentArea(getStringValue(r.getCell(16)));
                    c.setNoMeterArea(getStringValue(r.getCell(17)));
                    c.setNoMeterRate(getStringValue(r.getCell(18)));
                    c.setRate(getStringValue(r.getCell(19)));
                    c.setNoMeterVolume(getStringValue(r.getCell(20)));

                    c.setStatus(ConsumptionStatus.LOADED);

                    centralHeatingConsumptionBean.save(c);
                }
            });
        } catch (IOException e) {
            log.error("error load central heating consumption xls file");
        }
    }

    private String getStringValue(Cell cell){
        if (cell == null){
            return null;
        }else  if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
            return String.valueOf(cell.getNumericCellValue());
        }
        
        return cell.getStringCellValue();
    }
}
