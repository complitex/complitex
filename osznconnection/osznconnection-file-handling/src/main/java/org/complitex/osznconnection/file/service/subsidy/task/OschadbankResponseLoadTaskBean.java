package org.complitex.osznconnection.file.service.subsidy.task;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.complitex.common.exception.ExecuteException;
import org.complitex.osznconnection.file.entity.RequestFile;
import org.complitex.osznconnection.file.entity.RequestFileStatus;
import org.complitex.osznconnection.file.entity.subsidy.OschadbankResponse;
import org.complitex.osznconnection.file.entity.subsidy.OschadbankResponseField;
import org.complitex.osznconnection.file.entity.subsidy.OschadbankResponseFile;
import org.complitex.osznconnection.file.entity.subsidy.OschadbankResponseFileField;
import org.complitex.osznconnection.file.service.AbstractRequestTaskBean;
import org.complitex.osznconnection.file.service.RequestFileBean;
import org.complitex.osznconnection.file.service.exception.LoadException;
import org.complitex.osznconnection.file.service.subsidy.OschadbankResponseBean;
import org.complitex.osznconnection.file.service.subsidy.OschadbankResponseFileBean;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

/**
 * @author Anatoly A. Ivanov
 * 25.04.2019 18:35
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class OschadbankResponseLoadTaskBean extends AbstractRequestTaskBean<RequestFile> {
    @EJB
    private RequestFileBean requestFileBean;

    @EJB
    private OschadbankResponseFileBean oschadbankResponseFileBean;

    @EJB
    private OschadbankResponseBean oschadbankResponseBean;

    @SuppressWarnings("Duplicates")
    @Override
    public boolean execute(RequestFile requestFile, Map commandParameters) throws ExecuteException {
        Row row = null;

        try {
            XSSFWorkbook workbook = new XSSFWorkbook(requestFile.getAbsolutePath());

            XSSFSheet sheet = workbook.getSheetAt(0);

            Iterator<Row> it = sheet.iterator();

            OschadbankResponseFile oschadbankResponseFile = new OschadbankResponseFile();

            row = it.next();

            oschadbankResponseFile.putField(OschadbankResponseFileField.EDRPOU, getString(row, 1));
            oschadbankResponseFile.putField(OschadbankResponseFileField.REPORTING_PERIOD, getString(row, 3));
            oschadbankResponseFile.putField(OschadbankResponseFileField.PAYMENT_NUMBER, getString(row, 5));
            oschadbankResponseFile.putField(OschadbankResponseFileField.ANALYTICAL_ACCOUNT, getString(row, 7));

            row = it.next();

            oschadbankResponseFile.putField(OschadbankResponseFileField.PROVIDER_NAME, getString(row, 1));
            oschadbankResponseFile.putField(OschadbankResponseFileField.PROVIDER_CODE, getString(row, 3));
            oschadbankResponseFile.putField(OschadbankResponseFileField.REFERENCE_DOCUMENT, getString(row, 5));
            oschadbankResponseFile.putField(OschadbankResponseFileField.FEE, getString(row, 7));

            row = it.next();

            oschadbankResponseFile.putField(OschadbankResponseFileField.DOCUMENT_NUMBER, getString(row, 1));
            oschadbankResponseFile.putField(OschadbankResponseFileField.PROVIDER_ACCOUNT, getString(row, 3));
            oschadbankResponseFile.putField(OschadbankResponseFileField.PAYMENT_DATE, getString(row, 5));
            oschadbankResponseFile.putField(OschadbankResponseFileField.FEE_CODE, getString(row, 7));


            if (!String.format("%d-%02d", (Integer) commandParameters.get("year"), (Integer)commandParameters.get("month"))
                    .equals(oschadbankResponseFile.getStringField(OschadbankResponseFileField.REPORTING_PERIOD))){
                return false;
            }

            try {
                requestFile.setCheckSum(DigestUtils.md5Hex(new FileInputStream(requestFile.getAbsolutePath())));
            } catch (IOException e) {
                //
            }

            if (requestFileBean.checkLoaded(requestFile)){
                return false;
            }

            requestFile.setStatus(RequestFileStatus.LOADING);
            requestFileBean.save(requestFile);

            oschadbankResponseFile.setRequestFileId(requestFile.getId());


            row = it.next();

            oschadbankResponseFile.putField(OschadbankResponseFileField.SERVICE_NAME, getString(row, 1));
            oschadbankResponseFile.putField(OschadbankResponseFileField.PROVIDER_IBAN, getString(row, 3));
            oschadbankResponseFile.putField(OschadbankResponseFileField.TOTAL_AMOUNT, getString(row, 5));
            oschadbankResponseFile.putField(OschadbankResponseFileField.REGISTRY_ID, getString(row, 7));

            oschadbankResponseFileBean.save(oschadbankResponseFile);

            int recordCount = 0;

            while (it.hasNext()){
                row = it.next();

                if (row.getRowNum() < 6){
                    continue;
                }

                OschadbankResponse oschadbankResponse = new OschadbankResponse(requestFile.getId());

                oschadbankResponse.putField(OschadbankResponseField.UTSZN, getString(row, 0));
                oschadbankResponse.putField(OschadbankResponseField.OSCHADBANK_ACCOUNT, getString(row, 1));
                oschadbankResponse.putField(OschadbankResponseField.FIO, getString(row, 2));
                oschadbankResponse.putField(OschadbankResponseField.SERVICE_ACCOUNT, getString(row, 3));
                oschadbankResponse.putField(OschadbankResponseField.MONTH_SUM, getString(row, 4));
                oschadbankResponse.putField(OschadbankResponseField.SUM, getString(row, 5));
                oschadbankResponse.putField(OschadbankResponseField.SUBSIDY_SUM, getString(row, 6));
                oschadbankResponse.putField(OschadbankResponseField.DESCRIPTION, getString(row, 7));

                if (oschadbankResponse.getDbfFields().values().stream().anyMatch(Objects::nonNull)) {
                    recordCount++;

                    oschadbankResponseBean.save(oschadbankResponse);
                }
            }

            requestFile.setDbfRecordCount(recordCount);
            requestFile.setStatus(RequestFileStatus.LOADED);

            requestFileBean.save(requestFile);
        } catch (Exception e) {
            requestFile.setStatus(RequestFileStatus.LOAD_ERROR);

            requestFileBean.save(requestFile);

            throw new LoadException(e, requestFile, row != null ? row.getRowNum() : -1, "");
        }

        return true;
    }

    private String getString(Row row, int cellNum){
        Cell cell = row.getCell(cellNum);

        if (cell != null){
            if (cell.getCellType().equals(CellType.NUMERIC)){
                return BigDecimal.valueOf(cell.getNumericCellValue()).toPlainString()  + "";
            }

            return cell.getStringCellValue();
        }

        return null;
    }
}
