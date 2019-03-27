package org.complitex.osznconnection.file.service.subsidy.task;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.complitex.common.exception.ExecuteException;
import org.complitex.osznconnection.file.entity.RequestFile;
import org.complitex.osznconnection.file.entity.RequestFileStatus;
import org.complitex.osznconnection.file.entity.subsidy.OschadbankRequest;
import org.complitex.osznconnection.file.entity.subsidy.OschadbankRequestField;
import org.complitex.osznconnection.file.entity.subsidy.OschadbankRequestFile;
import org.complitex.osznconnection.file.entity.subsidy.OschadbankRequestFileField;
import org.complitex.osznconnection.file.service.AbstractRequestTaskBean;
import org.complitex.osznconnection.file.service.RequestFileBean;
import org.complitex.osznconnection.file.service.exception.LoadException;
import org.complitex.osznconnection.file.service.subsidy.OschadbankRequestBean;
import org.complitex.osznconnection.file.service.subsidy.OschadbankRequestFileBean;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Anatoly A. Ivanov
 * 26.03.2019 15:17
 */
@Stateless
public class OschadbankRequestLoadTaskBean extends AbstractRequestTaskBean<RequestFile> {
    @EJB
    private RequestFileBean requestFileBean;

    @EJB
    private OschadbankRequestFileBean oschadbankRequestFileBean;

    @EJB
    private OschadbankRequestBean oschadbankRequestBean;

    @Override
    public boolean execute(RequestFile requestFile, Map commandParameters) throws ExecuteException {
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

        Row row = null;

        try {
            XSSFWorkbook workbook = new XSSFWorkbook(requestFile.getAbsolutePath());

            XSSFSheet sheet = workbook.getSheetAt(0);

            Iterator<Row> it = sheet.iterator();

            OschadbankRequestFile oschadbankRequestFile = new OschadbankRequestFile(requestFile.getId());

            row = it.next();

            oschadbankRequestFile.putField(OschadbankRequestFileField.EDRPOU, row.getCell(1).getStringCellValue());
            oschadbankRequestFile.putField(OschadbankRequestFileField.REPORTING_PERIOD, row.getCell(3).getStringCellValue());

            row = it.next();

            oschadbankRequestFile.putField(OschadbankRequestFileField.PROVIDER_NAME, row.getCell(1).getStringCellValue());
            oschadbankRequestFile.putField(OschadbankRequestFileField.PROVIDER_CODE, row.getCell(3).getStringCellValue());

            row = it.next();

            oschadbankRequestFile.putField(OschadbankRequestFileField.DOCUMENT_NUMBER, row.getCell(1).getStringCellValue());
            oschadbankRequestFile.putField(OschadbankRequestFileField.PROVIDER_ACCOUNT, row.getCell(3).getStringCellValue());

            row = it.next();

            oschadbankRequestFile.putField(OschadbankRequestFileField.SERVICE_NAME, row.getCell(1).getStringCellValue());
            oschadbankRequestFile.putField(OschadbankRequestFileField.PROVIDER_IBAN, row.getCell(3).getStringCellValue());

            oschadbankRequestFileBean.save(oschadbankRequestFile);

            it.next();

            int recordCount = 0;

            while (it.hasNext()){
                row = it.next();

                recordCount++;

                OschadbankRequest oschadbankRequest = new OschadbankRequest(requestFile.getId());

                oschadbankRequest.putField(OschadbankRequestField.UTSZN, row.getCell(0).getStringCellValue());
                oschadbankRequest.putField(OschadbankRequestField.ACCOUNT_NUMBER, row.getCell(1).getStringCellValue());
                oschadbankRequest.putField(OschadbankRequestField.FIO, row.getCell(2).getStringCellValue());
                oschadbankRequest.putField(OschadbankRequestField.SERVICE_ACCOUNT, row.getCell(3).getStringCellValue());
                oschadbankRequest.putField(OschadbankRequestField.MONTH_SUM, row.getCell(4).getStringCellValue());
                oschadbankRequest.putField(OschadbankRequestField.SUM, row.getCell(5).getStringCellValue());

                oschadbankRequestBean.save(oschadbankRequest);
            }

            requestFile.setDbfRecordCount(recordCount);
            requestFile.setStatus(RequestFileStatus.LOADED);

            requestFileBean.save(requestFile);
        } catch (IOException e) {
            requestFile.setStatus(RequestFileStatus.LOAD_ERROR);

            requestFileBean.save(requestFile);

            throw new LoadException(e, requestFile, row != null ? row.getRowNum() : -1, "");
        }

        return true;
    }
}
