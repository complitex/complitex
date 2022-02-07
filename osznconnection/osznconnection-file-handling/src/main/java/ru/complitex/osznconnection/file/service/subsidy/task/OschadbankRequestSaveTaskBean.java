package ru.complitex.osznconnection.file.service.subsidy.task;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.exception.ExecuteException;
import ru.complitex.osznconnection.file.entity.RequestFile;
import ru.complitex.osznconnection.file.entity.RequestFileStatus;
import ru.complitex.osznconnection.file.entity.RequestStatus;
import ru.complitex.osznconnection.file.entity.subsidy.OschadbankRequest;
import ru.complitex.osznconnection.file.entity.subsidy.OschadbankRequestField;
import ru.complitex.osznconnection.file.service.AbstractRequestTaskBean;
import ru.complitex.osznconnection.file.service.RequestFileBean;
import ru.complitex.osznconnection.file.service.process.RequestFileDirectoryType;
import ru.complitex.osznconnection.file.service.process.RequestFileStorage;
import ru.complitex.osznconnection.file.service.subsidy.OschadbankRequestBean;
import ru.complitex.osznconnection.file.service.subsidy.OschadbankRequestFileBean;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.complitex.osznconnection.file.service.process.RequestFileDirectoryType.LOAD_OSCHADBANK_REQUEST_DIR;

/**
 * @author Anatoly A. Ivanov
 * 27.03.2019 21:58
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
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
            List<OschadbankRequest> oschadbankRequests = oschadbankRequestBean.getOschadbankRequests(
                    FilterWrapper.of(new OschadbankRequest(requestFile.getId())));

            String loadDir = RequestFileStorage.INSTANCE.getRequestFilesStorageDirectory(requestFile.getUserOrganizationId(),
                    requestFile.getOrganizationId(), LOAD_OSCHADBANK_REQUEST_DIR);

            XSSFWorkbook workbook = new XSSFWorkbook(loadDir + File.separator + requestFile.getName());
            workbook.getProperties().getCoreProperties().setCreated(Optional.of(new Date()));
            workbook.getProperties().getCoreProperties().setCreator(XSSFWorkbook.DOCUMENT_CREATOR);
            XSSFSheet sheet = workbook.getSheetAt(0);

            XSSFRow row;

            Map<String, OschadbankRequest> map = oschadbankRequests.stream()
                    .collect(Collectors.toMap(r -> r.getStringField(OschadbankRequestField.OSCHADBANK_ACCOUNT), r -> r));

            for (int i = 0; i < oschadbankRequests.size(); i++) {
                row = sheet.getRow(6 + i);

                OschadbankRequest r = map.get(row.getCell(1).getStringCellValue());

                if (r.getStatus().equals(RequestStatus.PROCESSED)) {
                    row.createCell(4).setCellValue(r.getStringField(OschadbankRequestField.MONTH_SUM));
                    row.createCell(5).setCellValue(r.getStringField(OschadbankRequestField.SUM));
                }else {
                    row.createCell(4).setCellValue("0");
                    row.createCell(5).setCellValue("0");
                }
            }

            String saveDir = RequestFileStorage.INSTANCE.getRequestFilesStorageDirectory(requestFile.getUserOrganizationId(),
                    requestFile.getOrganizationId(), RequestFileDirectoryType.SAVE_OSCHADBANK_REQUEST_DIR);

            try (FileOutputStream fileOutputStream = new FileOutputStream(new File(saveDir, requestFile.getName()))){
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
