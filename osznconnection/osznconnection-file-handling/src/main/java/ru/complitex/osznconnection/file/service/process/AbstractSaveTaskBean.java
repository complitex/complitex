package ru.complitex.osznconnection.file.service.process;

import com.google.common.collect.Lists;
import com.linuxense.javadbf.DBFField;
import com.linuxense.javadbf.DBFWriter;
import ru.complitex.common.entity.Log;
import ru.complitex.common.exception.CanceledByUserException;
import ru.complitex.common.exception.ExecuteException;
import ru.complitex.osznconnection.file.Module;
import ru.complitex.osznconnection.file.entity.AbstractAccountRequest;
import ru.complitex.osznconnection.file.entity.RequestFile;
import ru.complitex.osznconnection.file.entity.RequestFileStatus;
import ru.complitex.osznconnection.file.service.RequestFileBean;
import ru.complitex.osznconnection.file.service.exception.*;
import ru.complitex.osznconnection.file.service.file_description.RequestFileDescription;
import ru.complitex.osznconnection.file.service.file_description.RequestFileDescriptionBean;
import ru.complitex.osznconnection.file.service.file_description.RequestFileFieldDescription;
import ru.complitex.osznconnection.file.service.file_description.convert.DBFFieldTypeConverter;
import ru.complitex.osznconnection.file.web.pages.util.GlobalOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;

public abstract class AbstractSaveTaskBean{
    private final Logger log = LoggerFactory.getLogger(getClass());

    @EJB
    private RequestFileBean requestFileBean;

    @EJB
    private RequestFileDescriptionBean requestFileDescriptionBean;

    public boolean execute(RequestFile requestFile, Map commandParameters) throws ExecuteException{
        try {
            // получаем значение опции и параметров комманды
            // опция перезаписи номера л/с поставщика услуг номером л/с модуля начислений при выгрузке файла запроса
            final boolean updatePuAccount = (Boolean) commandParameters.get(GlobalOptions.UPDATE_PU_ACCOUNT);

            if (requestFileBean.getRequestFileStatus(requestFile.getId()).isProcessing()) { //проверяем что не обрабатывается в данный момент
                throw new SaveException(new AlreadyProcessingException(requestFile.getFullName()), true, requestFile);
            }

            requestFile.setStatus(RequestFileStatus.SAVING);
            requestFileBean.save(requestFile);

            //сохранение
            save(requestFile, updatePuAccount);

            requestFile.setStatus(RequestFileStatus.SAVED);
            requestFileBean.save(requestFile);

            return true;
        } catch (Exception e) {
            requestFile.setStatus(RequestFileStatus.SAVE_ERROR);
            requestFileBean.save(requestFile);

            throw e;
        }
    }

    public String getModuleName() {
        return Module.NAME;
    }

    public Log.EVENT getEvent() {
        return Log.EVENT.VIEW;
    }

    private DBFField newDBFField(String name, Class<?> javaType, int length, Integer scale) {
        DBFField field = new DBFField();
        field.setName(name);
        field.setDataType(DBFFieldTypeConverter.toDBFType(name, javaType));
        if (javaType != Date.class) {
            field.setFieldLength(length);
            if (scale == null) {
                scale = 0;
            }
            field.setDecimalCount(scale);
        }
        return field;
    }

    protected abstract List<? extends AbstractAccountRequest> getAbstractRequests(RequestFile requestFile);

    protected abstract String getPuAccountFieldName();

    protected abstract RequestFileDirectoryType getSaveDirectoryType();

    private String getOutputBaseDirectory(long userOrganizationId, long osznId) throws StorageNotFoundException {
        return RequestFileStorage.INSTANCE.getRequestFilesStorageDirectory(userOrganizationId, osznId, getSaveDirectoryType());
    }

    private DBFField[] newDBFFields(RequestFileDescription description) {
        List<DBFField> dbfFields = Lists.newArrayList();

        for (RequestFileFieldDescription field : description.getFields()) {
            dbfFields.add(newDBFField(field.getName(), field.getFieldType(), field.getLength(), field.getScale()));
        }
        return dbfFields.toArray(new DBFField[dbfFields.size()]);
    }

    protected String getOutputFileName(String inputFileName) {
        return inputFileName;
    }

    protected  void save(RequestFile requestFile, boolean updatePuAccount) throws SaveException {
        final RequestFileDescription description = requestFileDescriptionBean.getFileDescription(requestFile.getType());

        DBFWriter writer = null;
        String fieldName = "";

        try {
            //устанавливаем абсолютный путь для сохранения файла запроса
            File file = RequestFileStorage.INSTANCE.createOutputRequestFileDirectory(
                    getOutputBaseDirectory(requestFile.getUserOrganizationId(), requestFile.getOrganizationId()),
                    getOutputFileName(requestFile.getName()), requestFile.getDirectory());
            requestFile.setAbsolutePath(file.getAbsolutePath());

            //Удаляем файл если такой есть и создаем новый.
            writer = new DBFWriter(RequestFileStorage.INSTANCE.deleteAndCreateFile(requestFile.getAbsolutePath()));
            writer.setCharactersetName("cp866");

            //Создание полей
            DBFField[] fields = newDBFFields(description);
            writer.setFields(fields);

            //Сохранение строк
            List<? extends AbstractAccountRequest> rows;
            try {
                rows = getAbstractRequests(requestFile);
                requestFile.setRequests(rows);
            } catch (Exception e) {
                throw new SqlSessionException(e);
            }

            for (AbstractAccountRequest request : rows) {
                if (requestFile.isCanceled()) {
                    throw new CanceledByUserException();
                }

                Object[] rowData = new Object[fields.length];

                for (int i = 0; i < fields.length; ++i) {
                    fieldName = fields[i].getName();

                    RequestFileFieldDescription fieldDescription = description.getField(fieldName);
                    if (fieldDescription == null) {
                        log.error("save error: field description not found {}, field name: '{}'",
                                request, fieldName);
                        throw new SaveException(new FieldNotFoundException(fieldName), requestFile);
                    }

                    rowData[i] = request.getDbfFields().get(fieldName);

                    // перезаписываем номер л/с ПУ номером л/с МН при наличии установленной опции
                    if (updatePuAccount && fieldName.equals(getPuAccountFieldName())) {
                        rowData[i] = request.getAccountNumber();
                    }
                }

                writer.addRecord(rowData);
            }

            //Выгрузка завершена
            writer.write();
        } catch (Exception e) {
            if (writer != null) {
                writer.rollback();
            }

            log.error("error save fieldName = {}", fieldName);

            throw new SaveException(e, requestFile);
        }
    }
}
