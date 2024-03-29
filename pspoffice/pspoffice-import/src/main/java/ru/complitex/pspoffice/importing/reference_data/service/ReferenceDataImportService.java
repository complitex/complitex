package ru.complitex.pspoffice.importing.reference_data.service;

import ru.complitex.address.entity.AddressImportFile;
import ru.complitex.address.service.AddressImportService;
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
import ru.complitex.pspoffice.departure_reason.entity.DepartureReasonImportFile;
import ru.complitex.pspoffice.departure_reason.service.DepartureReasonImportService;
import ru.complitex.pspoffice.document_type.entity.DocumentTypeImportFile;
import ru.complitex.pspoffice.document_type.service.DocumentTypeImportService;
import ru.complitex.pspoffice.housing_rights.entity.HousingRightsImportFile;
import ru.complitex.pspoffice.housing_rights.service.HousingRightsImportService;
import ru.complitex.pspoffice.military.entity.MilitaryServiceRelationImportFile;
import ru.complitex.pspoffice.military.service.MilitaryServiceRelationImportService;
import ru.complitex.pspoffice.ownerrelationship.entity.OwnerRelationshipImportFile;
import ru.complitex.pspoffice.ownerrelationship.service.OwnerRelationshipImportService;
import ru.complitex.pspoffice.ownership.entity.OwnershipFormImportFile;
import ru.complitex.pspoffice.ownership.service.OwnershipFormImportService;
import ru.complitex.pspoffice.person.Module;
import ru.complitex.pspoffice.registration_type.entity.RegistrationTypeImportFile;
import ru.complitex.pspoffice.registration_type.service.RegistrationTypeImportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.ejb.*;
import javax.transaction.UserTransaction;
import java.util.*;

/**
 *
 * @author Artem
 */
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
@TransactionManagement(TransactionManagementType.BEAN)
public class ReferenceDataImportService {

    private final Logger log = LoggerFactory.getLogger(ReferenceDataImportService.class);
    @Resource
    private UserTransaction userTransaction;
    @EJB
    private AddressImportService addressImportService;
    @EJB
    private OwnerRelationshipImportService ownerRelationshipImportService;
    @EJB
    private OwnershipFormImportService ownershipFormImportService;
    @EJB
    private RegistrationTypeImportService registrationTypeImportService;
    @EJB
    private DocumentTypeImportService documentTypeImportService;
    @EJB
    private MilitaryServiceRelationImportService militaryServiceRelationImportService;
    @EJB
    private DepartureReasonImportService departureReasonImportService;
    @EJB
    private HousingRightsImportService housingRightsImportService;
    @EJB
    private ConfigBean configBean;
    @EJB
    private LogBean logBean;
    private volatile boolean processing;
    private volatile boolean error;
    private volatile boolean success;
    private volatile String errorMessage;
    private final Map<IImportFile, ImportMessage> referenceDataMap =
            Collections.synchronizedMap(new LinkedHashMap<IImportFile, ImportMessage>());

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

    public ImportMessage getReferenceDataMessage(IImportFile importFile) {
        return referenceDataMap.get(importFile);
    }

    private void init() {
        referenceDataMap.clear();
        processing = true;
        error = false;
        success = false;
        errorMessage = null;
    }

    private <T extends IImportFile> void processReferenceData(T importFile, final Locale locale)
            throws ImportFileNotFoundException, ImportObjectLinkException, ImportFileReadException, ImportDuplicateException, ImportDistrictLinkException {

        final IImportListener referenceDataListener = new IImportListener() {

            @Override
            public void beginImport(IImportFile importFile, int recordCount) {
                referenceDataMap.put(importFile, new ImportMessage(importFile, recordCount, 0));
            }

            @Override
            public void recordProcessed(IImportFile importFile, int recordIndex) {
                referenceDataMap.get(importFile).setIndex(recordIndex);
            }

            @Override
            public void completeImport(IImportFile importFile, int recordCount) {
                referenceDataMap.get(importFile).setCompleted();
                logBean.info(Module.NAME, ReferenceDataImportService.class, importFile.getClass(), null, Log.EVENT.CREATE,
                        "Имя файла: {0}, количество записей: {1}, Идентификатор локали: {2}",
                        importFile.getFileName(), recordCount, locale);
            }

            @Override
            public void warn(IImportFile importFile, String message) {
            }
        };

        if (importFile instanceof AddressImportFile) { //address
            addressImportService.process(importFile, referenceDataListener, locale, DateUtil.getCurrentDate());
        } else if (importFile instanceof OwnerRelationshipImportFile) { // owner relationship
            ownerRelationshipImportService.process(referenceDataListener, locale);
        } else if (importFile instanceof OwnershipFormImportFile) { //ownership form
            ownershipFormImportService.process(referenceDataListener, locale);
        } else if (importFile instanceof RegistrationTypeImportFile) {//registration type
            registrationTypeImportService.process(referenceDataListener, locale);
        } else if (importFile instanceof DocumentTypeImportFile) {//document type
            documentTypeImportService.process(referenceDataListener, locale);
        } else if (importFile instanceof MilitaryServiceRelationImportFile) {//military service relation
            militaryServiceRelationImportService.process(referenceDataListener, locale);
        } else if (importFile instanceof DepartureReasonImportFile) {//departure reason
            departureReasonImportService.process(referenceDataListener, locale);
        } else if (importFile instanceof HousingRightsImportFile) {//housing rights
            housingRightsImportService.process(referenceDataListener, locale);
        }
    }

    @Asynchronous
    public <T extends IImportFile> void process(List<T> referenceDataFiles, Locale locale) {
        if (processing) {
            return;
        }

        init();
        configBean.getString(DictionaryConfig.IMPORT_FILE_STORAGE_DIR, true); //reload config cache

        try {
            for (T referenceDataFile : referenceDataFiles) {
                userTransaction.begin();

                processReferenceData(referenceDataFile, locale);

                userTransaction.commit();
            }
            success = true;
        } catch (Exception e) {
            log.error("Import error.", e);

            try {
                userTransaction.rollback();
            } catch (Exception e1) {
                log.error("Couldn't rollback transaction.", e1);
            }

            error = true;
            errorMessage = e instanceof AbstractException ? e.getMessage() : new ImportCriticalException(e).getMessage();

            logBean.error(Module.NAME, ReferenceDataImportService.class, null, null, Log.EVENT.CREATE, errorMessage);
        } finally {
            processing = false;
        }
    }
}
