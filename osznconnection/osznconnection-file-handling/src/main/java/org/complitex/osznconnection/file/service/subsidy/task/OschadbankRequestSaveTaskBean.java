package org.complitex.osznconnection.file.service.subsidy.task;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.complitex.common.entity.FilterWrapper;
import org.complitex.common.exception.ExecuteException;
import org.complitex.osznconnection.file.entity.RequestFile;
import org.complitex.osznconnection.file.entity.RequestFileStatus;
import org.complitex.osznconnection.file.entity.subsidy.OschadbankRequest;
import org.complitex.osznconnection.file.entity.subsidy.OschadbankRequestField;
import org.complitex.osznconnection.file.entity.subsidy.OschadbankRequestFile;
import org.complitex.osznconnection.file.entity.subsidy.OschadbankRequestFileField;
import org.complitex.osznconnection.file.service.AbstractRequestTaskBean;
import org.complitex.osznconnection.file.service.RequestFileBean;
import org.complitex.osznconnection.file.service.process.RequestFileDirectoryType;
import org.complitex.osznconnection.file.service.process.RequestFileStorage;
import org.complitex.osznconnection.file.service.subsidy.OschadbankRequestBean;
import org.complitex.osznconnection.file.service.subsidy.OschadbankRequestFileBean;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

/**
 * @author Anatoly A. Ivanov
 * 27.03.2019 21:58
 */
@Stateless
public class OschadbankRequestSaveTaskBean extends AbstractRequestTaskBean<RequestFile> {
    @EJB
    private RequestFileBean requestFileBean;

    @EJB
    private OschadbankRequestFileBean oschadbankRequestFileBean;

    @EJB
    private OschadbankRequestBean oschadbankRequestBean;

    @Override
    public boolean execute(RequestFile requestFile, Map commandParameters) throws ExecuteException {
        requestFile.setStatus(RequestFileStatus.SAVING);
        requestFileBean.save(requestFile);

        try {
            OschadbankRequestFile oschadbankRequestFile = oschadbankRequestFileBean.getOschadbankRequestFile(
                    requestFile.getId());

            List<OschadbankRequest> oschadbankRequests = oschadbankRequestBean.getOschadbankRequests(
                    FilterWrapper.of(new OschadbankRequest(requestFile.getId())));

            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Реєстр нарахувань");

            XSSFRow row;

            row = sheet.createRow(0);

            row.createCell(0).setCellValue("ЄДРПОУ:");
            row.createCell(1).setCellValue(oschadbankRequestFile.getStringField(OschadbankRequestFileField.EDRPOU));
            row.createCell(2).setCellValue("Звітний період:");
            row.createCell(3).setCellValue(oschadbankRequestFile.getStringField(OschadbankRequestFileField.REPORTING_PERIOD));

            row = sheet.createRow(1);

            row.createCell(0).setCellValue("Назва постачальника:");
            row.createCell(1).setCellValue(oschadbankRequestFile.getStringField(OschadbankRequestFileField.PROVIDER_NAME));
            row.createCell(2).setCellValue("Код Банку Постачальника послуги:");
            row.createCell(3).setCellValue(oschadbankRequestFile.getStringField(OschadbankRequestFileField.PROVIDER_CODE));

            row = sheet.createRow(2);

            row.createCell(0).setCellValue("№ Анкети:");
            row.createCell(1).setCellValue(oschadbankRequestFile.getStringField(OschadbankRequestFileField.DOCUMENT_NUMBER));
            row.createCell(2).setCellValue("р/р Постачальника послуги:");
            row.createCell(3).setCellValue(oschadbankRequestFile.getStringField(OschadbankRequestFileField.PROVIDER_ACCOUNT));

            row = sheet.createRow(3);

            row.createCell(0).setCellValue("Назва послуги:");
            row.createCell(1).setCellValue(oschadbankRequestFile.getStringField(OschadbankRequestFileField.SERVICE_NAME));
            row.createCell(2).setCellValue("IBAN Постачальника:");
            row.createCell(3).setCellValue(oschadbankRequestFile.getStringField(OschadbankRequestFileField.PROVIDER_ACCOUNT));

            //todo row format

            row = sheet.createRow(5);
            row.createCell(0).setCellValue("Номер УПСЗН");
            row.createCell(1).setCellValue("Номер облікового запису одержувача житлової субсидії в АТ «Ощадбанк»");
            row.createCell(2).setCellValue("ПІБ одержувача субсидії");
            row.createCell(3).setCellValue("Номер особового рахунку у постачальника");
            row.createCell(4).setCellValue("Загальна нарахована сума за спожиті послуги у звітному місяці (грн.)");
            row.createCell(5).setCellValue("Загальна сума до сплати, що включає заборгованість/переплату за попередні періоди (грн.)");

            for (int i = 0; i < oschadbankRequests.size(); i++) {
                OschadbankRequest r = oschadbankRequests.get(i);

                row = sheet.createRow(6 + i);

                row.createCell(0).setCellValue(r.getStringField(OschadbankRequestField.UTSZN));
                row.createCell(1).setCellValue(r.getStringField(OschadbankRequestField.OSCHADBANK_ACCOUNT));
                row.createCell(2).setCellValue(r.getStringField(OschadbankRequestField.FIO));
                row.createCell(3).setCellValue(r.getStringField(OschadbankRequestField.SERVICE_ACCOUNT));
                row.createCell(4).setCellValue(r.getStringField(OschadbankRequestField.MONTH_SUM));
                row.createCell(5).setCellValue(r.getStringField(OschadbankRequestField.SUM));
            }

            String dir = RequestFileStorage.INSTANCE.getRequestFilesStorageDirectory(requestFile.getUserOrganizationId(),
                    requestFile.getOrganizationId(), RequestFileDirectoryType.SAVE_OSCHADBANK_REQUEST_DIR);

            try (FileOutputStream fileOutputStream = new FileOutputStream(new File(dir, requestFile.getName()))){
                workbook.write(fileOutputStream);
            }

            requestFile.setStatus(RequestFileStatus.SAVED);
            requestFileBean.save(requestFile);
        } catch (Exception e) {
            requestFile.setStatus(RequestFileStatus.SAVE_ERROR);
            requestFileBean.save(requestFile);

            throw new RuntimeException(e);
        }

        return true;
    }
}
