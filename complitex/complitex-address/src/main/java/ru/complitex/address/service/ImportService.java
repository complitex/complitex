package ru.complitex.address.service;

import ru.complitex.address.Module;
import ru.complitex.address.entity.AddressImportFile;
import ru.complitex.common.entity.DictionaryConfig;
import ru.complitex.common.entity.IImportFile;
import ru.complitex.common.entity.ImportMessage;
import ru.complitex.common.entity.Log;
import ru.complitex.common.exception.*;
import ru.complitex.common.exception.*;
import ru.complitex.common.service.ConfigBean;
import ru.complitex.common.service.IImportListener;
import ru.complitex.common.service.LogBean;
import ru.complitex.common.util.DateUtil;
import ru.complitex.organization.entity.OrganizationImportFile;
import ru.complitex.organization.exception.RootOrganizationNotFound;
import ru.complitex.organization.service.OrganizationImportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.ejb.*;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 28.02.11 18:05
 */
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
@TransactionManagement(TransactionManagementType.BEAN)
public class ImportService {

    private final Logger log = LoggerFactory.getLogger(ImportService.class);
    public static final long INT_ORG_ID = 0L;

    @Resource
    private UserTransaction userTransaction;

    @EJB
    private AddressImportService addressImportService;

    @EJB
    private OrganizationImportService organizationImportService;

    @EJB
    private ConfigBean configBean;

    @EJB
    private LogBean logBean;

    private boolean processing;
    private boolean error;
    private boolean success;
    private String errorMessage;
    private Map<IImportFile, ImportMessage> dictionaryMap = new LinkedHashMap<>();
    private IImportListener dictionaryListener = new IImportListener() {

        @Override
        public void beginImport(IImportFile importFile, int recordCount) {
            dictionaryMap.put(importFile, new ImportMessage(importFile, recordCount, 0));
        }

        @Override
        public void recordProcessed(IImportFile importFile, int recordIndex) {
            dictionaryMap.get(importFile).setIndex(recordIndex);
        }

        @Override
        public void completeImport(IImportFile importFile, int recordCount) {
            logBean.info(Module.NAME, ImportService.class, importFile.getClass(), null, Log.EVENT.CREATE,
                    "Имя файла: {0}, количество записей: {1}", importFile.getFileName(), recordCount);
        }

        @Override
        public void warn(IImportFile importFile, String message) {
        }
    };

    public boolean isProcessing() {
        return processing;
    }

    public boolean isError() {
        return error;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public ImportMessage getMessage(IImportFile importFile) {
        return dictionaryMap.get(importFile);
    }

    private void init() {
        dictionaryMap.clear();
        processing = true;
        error = false;
        success = false;
        errorMessage = null;
    }

    private <T extends IImportFile> void processDictionary(T importFile, Locale locale) throws ImportFileNotFoundException,
            ImportObjectLinkException, ImportFileReadException, ImportDuplicateException, RootOrganizationNotFound,
            ImportDistrictLinkException {
        if (importFile instanceof AddressImportFile) { //Address
            addressImportService.process(importFile, dictionaryListener, locale, DateUtil.getCurrentDate());
        } else if (importFile instanceof OrganizationImportFile){ //Organization
            organizationImportService.process(dictionaryListener, locale, DateUtil.getCurrentDate());
        }
    }

    @Asynchronous
    public <T extends IImportFile> void process(List<T> dictionaryFiles, Locale locale) {
        if (processing) {
            return;
        }

        init();

        configBean.getString(DictionaryConfig.IMPORT_FILE_STORAGE_DIR, true); //reload config cache

        try {
            //Dictionary
            for (T t : dictionaryFiles) {
                userTransaction.begin();

                processDictionary(t, locale);

                userTransaction.commit();
            }

            success = true;
        } catch (Exception e) {
            log.error("Ошибка импорта", e);

            try {
                userTransaction.rollback();
            } catch (SystemException e1) {
                log.error("Ошибка отката транзакции", e1);
            }

            error = true;
            errorMessage = e instanceof AbstractException ? e.getMessage() : new ImportCriticalException(e).getMessage();

            logBean.error(Module.NAME, ImportService.class, null, null, Log.EVENT.CREATE, errorMessage);
        } finally {
            processing = false;
        }
    }
}
