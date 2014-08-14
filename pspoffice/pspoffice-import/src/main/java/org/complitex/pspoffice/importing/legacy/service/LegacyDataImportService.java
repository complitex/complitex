/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.importing.legacy.service;

import java.io.Writer;
import java.util.Date;
import org.complitex.dictionary.util.DateUtil;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import org.complitex.pspoffice.importing.legacy.entity.ImportStatus;
import java.io.IOException;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import org.apache.wicket.util.string.Strings;
import org.complitex.address.strategy.apartment.ApartmentStrategy;
import org.complitex.address.strategy.building.BuildingStrategy;
import org.complitex.address.strategy.building.entity.Building;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.service.LocaleBean;
import org.complitex.dictionary.service.exception.AbstractException;
import org.complitex.dictionary.service.exception.ImportCriticalException;
import org.complitex.dictionary.service.exception.ImportFileNotFoundException;
import org.complitex.dictionary.service.exception.ImportFileReadException;
import org.complitex.dictionary.strategy.IStrategy;
import org.complitex.dictionary.strategy.StrategyFactory;
import org.complitex.dictionary.util.ResourceUtil;
import org.complitex.pspoffice.document_type.strategy.DocumentTypeStrategy;
import org.complitex.pspoffice.importing.legacy.entity.ApartmentCardCorrection;
import org.complitex.pspoffice.importing.legacy.entity.BuildingCorrection;
import org.complitex.pspoffice.importing.legacy.entity.ImportMessage;
import org.complitex.pspoffice.importing.legacy.entity.PersonCorrection;
import org.complitex.pspoffice.importing.legacy.entity.ProcessItem;
import org.complitex.pspoffice.importing.legacy.entity.LegacyDataImportFile;
import org.complitex.pspoffice.importing.legacy.entity.ReferenceDataCorrection;
import org.complitex.pspoffice.importing.legacy.entity.StreetCorrection;
import org.complitex.pspoffice.importing.legacy.service.exception.OpenErrorDescriptionFileException;
import org.complitex.pspoffice.importing.legacy.service.exception.OpenErrorFileException;
import org.complitex.pspoffice.importing.legacy.service.exception.TooManyResultsException;
import org.complitex.pspoffice.military.strategy.MilitaryServiceRelationStrategy;
import org.complitex.pspoffice.ownerrelationship.strategy.OwnerRelationshipStrategy;
import org.complitex.pspoffice.ownership.strategy.OwnershipFormStrategy;
import org.complitex.pspoffice.person.strategy.ApartmentCardStrategy;
import org.complitex.pspoffice.person.strategy.PersonStrategy;
import org.complitex.pspoffice.person.strategy.RegistrationStrategy;
import org.complitex.pspoffice.person.strategy.entity.ApartmentCard;
import org.complitex.pspoffice.person.strategy.entity.Person;
import org.complitex.pspoffice.person.strategy.entity.Registration;
import org.complitex.pspoffice.registration_type.strategy.RegistrationTypeStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.complitex.pspoffice.importing.legacy.entity.ImportMessage.ImportMessageLevel.*;

/**
 * Concurrency note: thread-safe bean.
 *
 * @author Artem
 */
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
@TransactionManagement(TransactionManagementType.BEAN)
public class LegacyDataImportService {

    private final Logger log = LoggerFactory.getLogger(LegacyDataImportService.class);
    private static final String RESOURCE_BUNDLE = LegacyDataImportService.class.getName();
    private static final char SEPARATOR = '\t';
    private static final String CHARSET = "UTF-8";
    private static final String ERROR_FILE_SUFFIX = "_errors.csv";
    private static final String ERROR_DESCRIPTION_FILE_SUFFIX = "_errors_description.txt";
    private static final int PROCESSING_BATCH = 500;
    @EJB
    private StrategyFactory strategyFactory;
    @Resource
    private UserTransaction userTransaction;
    @EJB
    private StreetCorrectionBean streetCorrectionBean;
    @EJB
    private BuildingCorrectionBean buildingCorrectionBean;
    @EJB
    private BuildingStrategy buildingStrategy;
    @EJB
    private PersonStrategy personStrategy;
    @EJB
    private ReferenceDataCorrectionBean referenceDataCorrectionBean;
    @EJB
    private PersonCorrectionBean personCorrectionBean;
    @EJB
    private ApartmentCardCorrectionBean apartmentCardCorrectionBean;
    @EJB
    private ApartmentStrategy apartmentStrategy;
    @EJB
    private ApartmentCardStrategy apartmentCardStrategy;
    @EJB
    private RegistrationCorrectionBean registrationCorrectionBean;
    @EJB
    private RegistrationStrategy registrationStrategy;
    @EJB
    private LocaleBean localeBean;
    /*
     * Concurrency note: volatile boolean.
     */
    private volatile boolean processing;
    /*
     * Concurrency note: volatile immutable variable.
     */
    private volatile Locale locale;
    /*
     * Concurrency note: volatile immutable variable.
     */
    private volatile Long cityId;
    /*
     * Concurrency note: volatile immutable variable.
     */
    private volatile String ownerType;
    /*
     * Concurrency note: volatile boolean.
     */
    private volatile boolean reservedDocumentTypesResolved;
    /*
     * Concurrency note: volatile boolean.
     */
    private volatile boolean reservedRegistrationTypesResolved;
    /*
     * Concurrency note: volatile boolean.
     */
    private volatile boolean reservedOwnerRelationshipResolved;
    /*
     * Concurrency note: volatile immutable set containing immutable objects.
     */
    private volatile ImmutableSet<String> jekIds;
    /*
     * Concurrency note: volatile immutable map containing immutable objects.
     */
    private volatile ImmutableMap<String, Long> organizationMap;
    /*
     * Concurrency note: volatile immutable variable.
     */
    private volatile String importDirectory;
    /*
     * Concurrency note: volatile immutable variable.
     */
    private volatile String errorsDirectory;
    /*
     * Concurrency note: synchronized map containing enum keys and thread-safe values.
     */
    private final Map<LegacyDataImportFile, ImportStatus> loadingStatuses =
            Collections.synchronizedMap(new EnumMap<LegacyDataImportFile, ImportStatus>(LegacyDataImportFile.class));
    /*
     * Concurrency note: synchronized map containing enum keys and thread-safe values.
     */
    private final Map<ProcessItem, ImportStatus> processingStatuses =
            Collections.synchronizedMap(new EnumMap<ProcessItem, ImportStatus>(ProcessItem.class));
    /*
     * Concurrency note: thread-safe queue containing immutable objects.
     */
    private final Queue<ImportMessage> messages = new ConcurrentLinkedQueue<ImportMessage>();

    public boolean isProcessing() {
        return processing;
    }

    public ImportStatus getLoadingStatus(LegacyDataImportFile importFile) {
        return loadingStatuses.get(importFile);
    }

    public ImportStatus getProcessingStatus(ProcessItem processItem) {
        return processingStatuses.get(processItem);
    }

    public ImportMessage getNextMessage() {
        return messages.poll();
    }

    private void clean() {
        loadingStatuses.clear();
        processingStatuses.clear();
        messages.clear();
        cityId = null;
        ownerType = null;
        reservedDocumentTypesResolved = false;
        reservedRegistrationTypesResolved = false;
        reservedOwnerRelationshipResolved = false;
        locale = null;
        jekIds = null;
        organizationMap = null;
        importDirectory = null;
        errorsDirectory = null;
    }

    @Asynchronous
    public void startImport(long cityId, Map<String, Long> organizationMap, String importDirectiry, String errorsDirectory, Locale locale) {
        if (processing) {
            return;
        }

        clean();
        processing = true;

        this.cityId = cityId;
        this.organizationMap = ImmutableMap.copyOf(organizationMap);
        this.jekIds = ImmutableSet.copyOf(this.organizationMap.keySet());
        this.importDirectory = importDirectiry;
        this.errorsDirectory = errorsDirectory;
        this.locale = locale;

        //load files
        try {
            userTransaction.begin();
            loadFiles();
            userTransaction.commit();
        } catch (Exception e) {
            processing = false;
            log.error("File loading error.", e);
            try {
                userTransaction.rollback();
            } catch (SystemException se) {
                log.error("Couldn't to rollback transaction.", se);
            }

            String errorMessage = e instanceof AbstractException ? e.getMessage() : new ImportCriticalException(e).getMessage();
            messages.add(new ImportMessage(errorMessage, ERROR));
        }

        if (!processing) {
            return;
        }

        //process files
        try {
            processFiles();
        } catch (Exception e) {
            log.error("Processing error.", e);
            
            String errorMessage = e instanceof AbstractException ? e.getMessage() : new ImportCriticalException(e).getMessage();
            messages.add(new ImportMessage(errorMessage, ERROR));
        } finally {
            processing = false;
        }
    }

    private void loadFiles() throws Exception {
        loadStreets();
        loadBuildings();
        loadReferenceData(LegacyDataImportFile.OWNERSHIP_FORM, "ownership_form");
        loadReferenceData(LegacyDataImportFile.MILITARY_DUTY, "military_duty");
        loadReferenceData(LegacyDataImportFile.OWNER_RELATIONSHIP, "owner_relationship");
        loadReferenceData(LegacyDataImportFile.DEPARTURE_REASON, "departure_reason");
        loadReferenceData(LegacyDataImportFile.REGISTRATION_TYPE, "registration_type");
        loadReferenceData(LegacyDataImportFile.DOCUMENT_TYPE, "document_type");
        loadReferenceData(LegacyDataImportFile.OWNER_TYPE, "owner_type");
        loadPersons();
        loadApartmentCards();
    }

    private static interface ILoader {

        void load() throws Exception;

        int getCurrentRecordIndex();
    }

    private static abstract class Loader implements ILoader {

        private int recordIndex;

        @Override
        public int getCurrentRecordIndex() {
            return recordIndex;
        }

        protected void incrementRecordIndex() {
            recordIndex++;
        }
    }

    private static Exception wrapLoadException(Exception e, String fileName, int recordIndex) {
        final Class<?> exceptionClass = e.getClass();
        if (IOException.class == exceptionClass) {
            return new ImportFileReadException(e, fileName, recordIndex);
        } else if (NumberFormatException.class == exceptionClass) {
            return new ImportFileReadException(e, fileName, recordIndex);
        } else {
            return e;
        }
    }

    private void handleLoad(CSVReader reader, String fileName, ILoader loader)
            throws Exception {
        try {
            loader.load();
        } catch (Exception e) {
            throw wrapLoadException(e, fileName, loader.getCurrentRecordIndex());
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                log.error("Couldn't to close csv reader.", e);
            }
        }
    }

    /**
     * id    Street type string(ukr)     Street name(ukr)     Street type string(rus)     Street name(rus)
     */
    private void loadStreets() throws Exception {
        final LegacyDataImportFile file = LegacyDataImportFile.STREET;

        final Map<String, Boolean> loadFileStatusMap = Maps.newHashMap();
        for (String idjek : jekIds) {
            loadFileStatusMap.put(idjek, !streetCorrectionBean.exists(idjek));
        }

        final Set<String> loadJekIds = Sets.newHashSet();
        for (Map.Entry<String, Boolean> e : loadFileStatusMap.entrySet()) {
            String idjek = e.getKey();
            if (e.getValue()) {
                loadJekIds.add(idjek);
            } else {
                messages.add(new ImportMessage(getString("already_loaded_file", file.getFileName(), idjek), WARN));
            }
        }

        if (!loadJekIds.isEmpty()) {
            //start file importing:
            loadingStatuses.put(file, new ImportStatus(0));
            messages.add(new ImportMessage(getString("begin_loading_file", file.getFileName()), INFO));

            final CSVReader reader = getCsvReader(importDirectory, file, CHARSET, SEPARATOR);
            handleLoad(reader, file.getFileName(), new Loader() {

                @Override
                public void load() throws Exception {
                    String[] line;

                    while ((line = reader.readNext()) != null) {
                        incrementRecordIndex();

                        long id = Long.parseLong(line[0].trim());

                        for (String idjek : loadJekIds) {
                            streetCorrectionBean.insert(new StreetCorrection(id, idjek, line[1].trim(), line[2].trim(),
                                    line[3].trim(), line[4].trim(), getContent(line)));
                        }
                        loadingStatuses.get(file).increment();
                    }

                    //finish file importing
                    messages.add(new ImportMessage(getString("finish_loading_file", file.getFileName()), INFO));
                    loadingStatuses.get(file).finish();
                }
            });
        }
    }

    /**
     * id   idjek   idul    dom     korpus
     */
    private void loadBuildings() throws Exception {
        final LegacyDataImportFile file = LegacyDataImportFile.BUILDING;

        final Map<String, Boolean> loadFileStatusMap = Maps.newHashMap();
        for (String idjek : jekIds) {
            loadFileStatusMap.put(idjek, !buildingCorrectionBean.exists(idjek));
        }

        final Set<String> loadJekIds = Sets.newHashSet();
        for (Map.Entry<String, Boolean> e : loadFileStatusMap.entrySet()) {
            String idjek = e.getKey();
            if (e.getValue()) {
                loadJekIds.add(idjek);
            } else {
                messages.add(new ImportMessage(getString("already_loaded_file", file.getFileName(), idjek), WARN));
            }
        }

        if (!loadJekIds.isEmpty()) {
            //start file importing:
            loadingStatuses.put(file, new ImportStatus(0));
            messages.add(new ImportMessage(getString("begin_loading_file", file.getFileName()), INFO));

            final CSVReader reader = getCsvReader(importDirectory, file, CHARSET, SEPARATOR);
            handleLoad(reader, file.getFileName(), new Loader() {

                @Override
                public void load() throws Exception {
                    String[] line;

                    while ((line = reader.readNext()) != null) {
                        incrementRecordIndex();

                        long id = Long.parseLong(line[0].trim());

                        String idjek = line[1].trim();

                        if (loadJekIds.contains(idjek)) {
                            buildingCorrectionBean.insert(new BuildingCorrection(id, idjek, line[2].trim(),
                                    line[3].trim(), line[4].trim(), getContent(line)));
                            loadingStatuses.get(file).increment();
                        }
                    }

                    //finish file importing
                    messages.add(new ImportMessage(getString("finish_loading_file", file.getFileName()), INFO));
                    loadingStatuses.get(file).finish();
                }
            });
        }
    }

    /**
     * id    nkod
     */
    private void loadReferenceData(final LegacyDataImportFile file, final String entity) throws Exception {
        final Map<String, Boolean> loadFileStatusMap = Maps.newHashMap();
        for (String idjek : jekIds) {
            loadFileStatusMap.put(idjek, !referenceDataCorrectionBean.exists(entity, idjek));
        }

        final Set<String> loadJekIds = Sets.newHashSet();
        for (Map.Entry<String, Boolean> e : loadFileStatusMap.entrySet()) {
            String idjek = e.getKey();
            if (e.getValue()) {
                loadJekIds.add(idjek);
            } else {
                messages.add(new ImportMessage(getString("already_loaded_file", file.getFileName(), idjek), WARN));
            }
        }

        if (!loadJekIds.isEmpty()) {
            //start file importing:
            loadingStatuses.put(file, new ImportStatus(0));
            messages.add(new ImportMessage(getString("begin_loading_file", file.getFileName()), INFO));

            final CSVReader reader = getCsvReader(importDirectory, file, CHARSET, SEPARATOR);
            handleLoad(reader, file.getFileName(), new Loader() {

                @Override
                public void load() throws Exception {
                    String[] line;

                    while ((line = reader.readNext()) != null) {
                        incrementRecordIndex();

                        long id = Long.parseLong(line[0].trim());

                        for (String idjek : loadJekIds) {
                            referenceDataCorrectionBean.insert(new ReferenceDataCorrection(entity, id, idjek, line[1].trim(),
                                    getContent(line)));
                        }
                        loadingStatuses.get(file).increment();
                    }

                    //finish file importing
                    messages.add(new ImportMessage(getString("finish_loading_file", file.getFileName()), INFO));
                    loadingStatuses.get(file).finish();
                }
            });
        }
    }

    /**
     * 0  2     3   4  5   6  7  8     9            11    12  13    17   18   19     20     21    22       23     24
     * id idbud rah kv fam im ot datar realtovlaskv grajd pol idrel nkra nobl nrayon nmisto iddok dokseria doknom dokvidan
     * 
     * 25        28    30   31   32     33     34       35    36   37    38  39       40    41     43   44   45     46
     * dokdatvid idarm pkra pobl prayon pmisto pdpribza pidul pbud pkorp pkv pdpribvm dprop idvidp vkra vobl vrayon vmisto
     * 
     * 47    48   49    50  51    52    53        56   57
     * vidul vbud vkorp vkv vdata idvip parentnom larc nom
     */
    private void loadPersons() throws Exception {
        final LegacyDataImportFile file = LegacyDataImportFile.PERSON;

        final boolean exists = personCorrectionBean.exists();

        if (exists) {
            messages.add(new ImportMessage(getString("already_loaded_person_file", file.getFileName()), WARN));
        }

        if (!exists) {
            //start file importing:
            loadingStatuses.put(file, new ImportStatus(0));
            messages.add(new ImportMessage(getString("begin_loading_file", file.getFileName()), INFO));

            final CSVReader reader = getCsvReader(importDirectory, file, CHARSET, SEPARATOR);
            handleLoad(reader, file.getFileName(), new Loader() {

                @Override
                public void load() throws Exception {
                    String[] line;

                    while ((line = reader.readNext()) != null) {
                        incrementRecordIndex();

                        long id = Long.parseLong(line[0].trim());
                        PersonCorrection p = new PersonCorrection(id, getContent(line));
                        p.setIdbud(line[2].trim());
                        p.setRah(line[3].trim());
                        p.setKv(line[4].trim());
                        p.setFam(line[5].trim());
                        p.setIm(line[6].trim());
                        p.setOt(line[7].trim());
                        p.setDatar(line[8].trim());
                        p.setReltovlaskv(line[9].trim());
                        p.setGrajd(line[11].trim());
                        p.setPol(line[12].trim());
                        p.setIdrel(line[13].trim());
                        p.setNkra(line[17].trim());
                        p.setNobl(line[18].trim());
                        p.setNrayon(line[19].trim());
                        p.setNmisto(line[20].trim());
                        p.setIddok(line[21].trim());
                        p.setDokseria(line[22].trim());
                        p.setDoknom(line[23].trim());
                        p.setDokvidan(line[24].trim());
                        p.setDokdatvid(line[25].trim());
                        p.setIdarm(line[28].trim());
                        p.setPkra(line[30].trim());
                        p.setPobl(line[31].trim());
                        p.setPrayon(line[32].trim());
                        p.setPmisto(line[33].trim());
                        p.setPdpribza(line[34].trim());
                        p.setPidul(line[35].trim());
                        p.setPbud(line[36].trim());
                        p.setPkorp(line[37].trim());
                        p.setPkv(line[38].trim());
                        p.setPdpribvm(line[39].trim());
                        p.setDprop(line[40].trim());
                        p.setIdvidp(line[41].trim());
                        p.setVkra(line[43].trim());
                        p.setVobl(line[44].trim());
                        p.setVrayon(line[45].trim());
                        p.setVmisto(line[46].trim());
                        p.setVidul(line[47].trim());
                        p.setVbud(line[48].trim());
                        p.setVkorp(line[49].trim());
                        p.setVkv(line[50].trim());
                        p.setVdata(line[51].trim());
                        p.setIdvip(line[52].trim());
                        p.setLarc(line[56].trim());
                        p.setNom(line[57].trim());
                        p.setParentnom(line[53].trim());

                        personCorrectionBean.insert(p);
                        loadingStatuses.get(file).increment();
                    }

                    //finish file importing
                    messages.add(new ImportMessage(getString("finish_loading_file", file.getFileName()), INFO));
                    loadingStatuses.get(file).finish();
                }
            });
        }
    }

    /**
     * 0  1     2   3  4   5        29
     * id idbud rah kv fio idprivat larc
     */
    private void loadApartmentCards() throws Exception {
        final LegacyDataImportFile file = LegacyDataImportFile.APARTMENT_CARD;

        final boolean exists = apartmentCardCorrectionBean.exists();

        if (exists) {
            messages.add(new ImportMessage(getString("already_loaded_apartment_card_file", file.getFileName()), WARN));
        }

        if (!exists) {
            //start file importing:
            loadingStatuses.put(file, new ImportStatus(0));
            messages.add(new ImportMessage(getString("begin_loading_file", file.getFileName()), INFO));

            final CSVReader reader = getCsvReader(importDirectory, file, CHARSET, SEPARATOR);
            handleLoad(reader, file.getFileName(), new Loader() {

                @Override
                public void load() throws Exception {
                    String[] line;

                    while ((line = reader.readNext()) != null) {
                        incrementRecordIndex();

                        long id = Long.parseLong(line[0].trim());
                        ApartmentCardCorrection c = new ApartmentCardCorrection(id, getContent(line));
                        c.setIdbud(line[1].trim());
                        c.setRah(line[2].trim());
                        c.setKv(line[3].trim());
                        c.setFio(line[4].trim());
                        c.setIdprivat(line[5].trim());
                        c.setLarc(line[29].trim());

                        apartmentCardCorrectionBean.insert(c);
                        loadingStatuses.get(file).increment();
                    }

                    //finish file importing
                    messages.add(new ImportMessage(getString("finish_loading_file", file.getFileName()), INFO));
                    loadingStatuses.get(file).finish();
                }
            });
        }
    }

    private void processFiles() throws Exception {
        processStreetsAndBuildings();
        processOwnershipForms();
        processOwnerRelationships();
        processMilitaryDuties();
        processRegistrationsTypes();
        processDocumentsTypes();
        processOwnerTypes();
        processPersons();
        processApartmentCards();
        processRegistrations();
    }

    private static interface IProcessor {

        void process() throws Exception;

        void clearProcessingStatus();

        void closeFiles();
    }

    private static abstract class Processor implements IProcessor {

        protected void closeFile(Writer fileStream) {
            if (fileStream != null) {
                try {
                    fileStream.close();
                } catch (IOException e) {
                    LoggerFactory.getLogger(LegacyDataImportService.class).error("Couldn't to close file stream.", e);
                }
            }
        }
    }

    private Exception wrapProcessException(Exception e) {
        if (e instanceof AbstractException) {
            return e;
        } else {
            return new RuntimeException(e);
        }
    }

    private void handleProcess(IProcessor processor) throws Exception {
        try {
            userTransaction.begin();
            processor.process();
            userTransaction.commit();
        } catch (Exception e) {
            try {
                if (userTransaction.getStatus() != Status.STATUS_NO_TRANSACTION) {
                    userTransaction.rollback();
                }
            } catch (Exception e1) {
                log.error("Couldn't to rollback transaction.", e1);
            }

            throw wrapProcessException(e);
        } finally {
            processor.closeFiles();

            try {
                userTransaction.begin();
                processor.clearProcessingStatus();
                userTransaction.commit();
            } catch (Exception e) {
                try {
                    if (userTransaction.getStatus() != Status.STATUS_NO_TRANSACTION) {
                        userTransaction.rollback();
                    }
                } catch (SystemException se) {
                    log.error("Couldn't to rollback transaction.", se);
                }
                log.error("Couldn't to clear processing status.", e);
            }
        }
    }

    private void processStreetsAndBuildings() throws Exception {
        handleProcess(new Processor() {

            BufferedWriter streetErrorFile;
            BufferedWriter streetErrorDescriptionFile;
            BufferedWriter buildingErrorFile;
            BufferedWriter buildingErrorDescriptionFile;

            @Override
            public void process() throws Exception {
                final ProcessItem item = ProcessItem.STREET_BUILDING;

                processingStatuses.put(item, new ImportStatus(0));
                final int count = buildingCorrectionBean.countForProcessing(jekIds);
                messages.add(new ImportMessage(getString("begin_street_building_processing", count), INFO));
                boolean wasErrors = false;

                for (String idjek : jekIds) {
                    int jekCount = buildingCorrectionBean.countForProcessing(idjek);
                    while (jekCount > 0) {
                        List<BuildingCorrection> buildings = buildingCorrectionBean.findForProcessing(idjek, PROCESSING_BATCH);
                        for (BuildingCorrection building : buildings) {
                            if (building.getSystemBuildingId() == null) {
                                String buildingErrorDescription = null;
                                String streetErrorDescription = null;

                                String idul = building.getIdul();
                                final StreetCorrection street = streetCorrectionBean.getById(idul, idjek);
                                if (street == null) {
                                    buildingErrorDescription = getString("building_invalid_street_id",
                                            building.getId(), building.getIdjek(), idul);
                                } else {
                                    Long systemStreetId = street.getSystemStreetId();
                                    if (systemStreetId == null && !street.isProcessed()) {
                                        final String ukrStreet = street.getNkod();
                                        final String rusStreet = street.getNkod1();
                                        final String ukrStreetType = street.getUtype();
                                        final String rusStreetType = street.getRtype();

                                        //street type
                                        Long systemStreetTypeId = streetCorrectionBean.findSystemStreetType(ukrStreetType, rusStreetType);
                                        if (systemStreetTypeId == null) {
                                            streetErrorDescription = getString("street_type_not_resolved", street.getId(),
                                                    street.getIdjek(), ukrStreetType, rusStreetType);
                                        } else {
                                            //street
                                            systemStreetId = streetCorrectionBean.findSystemStreet(cityId,
                                                    systemStreetTypeId, ukrStreet, rusStreet);
                                            if (systemStreetId == null) {
                                                streetErrorDescription = getString("street_not_resolved", street.getId(),
                                                        street.getIdjek(), ukrStreet, rusStreet);
                                            } else {
                                                street.setSystemStreetId(systemStreetId);
                                            }
                                        }
                                        street.setProcessed(true);
                                        streetCorrectionBean.update(street);
                                    }
                                    if (systemStreetId != null) {
                                        final String dom = building.getDom();
                                        final String korpus = building.getKorpus();

                                        try {
                                            Long systemBuildingId =
                                                    buildingCorrectionBean.findSystemBuilding(systemStreetId, dom, korpus);
                                            if (systemBuildingId == null) {
                                                Building systemBuilding =
                                                        buildingCorrectionBean.newBuilding(systemStreetId, dom, korpus,
                                                        organizationMap.get(building.getIdjek()));
                                                buildingStrategy.insert(systemBuilding, DateUtil.getCurrentDate());
                                                systemBuildingId = systemBuilding.getId();
                                            }
                                            building.setSystemBuildingId(systemBuildingId);
                                        } catch (TooManyResultsException e) {
                                            buildingErrorDescription = getString("building_system_many_objects",
                                                    building.getId(), building.getIdjek(), building.getDom(), building.getKorpus(),
                                                    building.getIdul());
                                        }
                                    } else {
                                        buildingErrorDescription = getString("building_system_street_not_resolved",
                                                building.getId(), building.getIdjek(), idul);
                                    }
                                }

                                building.setProcessed(true);
                                buildingCorrectionBean.update(building);

                                processingStatuses.get(item).increment();

                                if (streetErrorDescription != null) {
                                    wasErrors = true;
                                    if (streetErrorFile == null) {
                                        streetErrorFile = getErrorFile(errorsDirectory, LegacyDataImportFile.STREET);
                                        streetErrorFile.write(LegacyDataImportFile.STREET.getCsvHeader());
                                        streetErrorFile.newLine();
                                    }
                                    if (streetErrorDescriptionFile == null) {
                                        streetErrorDescriptionFile = getErrorDescriptionFile(errorsDirectory,
                                                LegacyDataImportFile.STREET);
                                    }

                                    streetErrorFile.write(street.getContent());
                                    streetErrorFile.newLine();

                                    streetErrorDescriptionFile.write(streetErrorDescription);
                                    streetErrorDescriptionFile.newLine();
                                }

                                if (buildingErrorDescription != null) {
                                    wasErrors = true;
                                    if (buildingErrorFile == null) {
                                        buildingErrorFile = getErrorFile(errorsDirectory, LegacyDataImportFile.BUILDING);
                                        buildingErrorFile.write(LegacyDataImportFile.BUILDING.getCsvHeader());
                                        buildingErrorFile.newLine();
                                    }
                                    if (buildingErrorDescriptionFile == null) {
                                        buildingErrorDescriptionFile = getErrorDescriptionFile(errorsDirectory,
                                                LegacyDataImportFile.BUILDING);
                                    }

                                    buildingErrorFile.write(building.getContent());
                                    buildingErrorFile.newLine();

                                    buildingErrorDescriptionFile.write(buildingErrorDescription);
                                    buildingErrorDescriptionFile.newLine();
                                }
                            }
                        }
                        jekCount = buildingCorrectionBean.countForProcessing(idjek);
                    }
                }

                if (wasErrors) {
                    messages.add(new ImportMessage(getString("fail_finish_street_building_processing", count), WARN));
                } else {
                    messages.add(new ImportMessage(getString("success_finish_street_building_processing", count), INFO));
                }
                processingStatuses.get(item).finish();
            }

            @Override
            public void clearProcessingStatus() {
                streetCorrectionBean.clearProcessingStatus(jekIds);
                buildingCorrectionBean.clearProcessingStatus(jekIds);
            }

            @Override
            public void closeFiles() {
                closeFile(streetErrorFile);
                closeFile(streetErrorDescriptionFile);
                closeFile(buildingErrorFile);
                closeFile(buildingErrorDescriptionFile);
            }
        });
    }

    private class ReferenceDataProcessor extends Processor {

        private static final String BEGIN_PROCESSING_KEY_SUFFIX = "_begin_processing";
        private static final String MANY_SYSTEM_OBJECTS_KEY_SUFFIX = "_system_many_objects";
        private static final String FAIL_PROCESSING_KEY_SUFFIX = "_fail_finish_processing";
        private static final String SUCCESS_PROCESSING_KEY_SUFFIX = "_success_finish_processing";
        private final String entityTable;
        private final long attributeTypeId;
        private final String correctionTable;
        private final ProcessItem item;
        private final String beginProcessingMessageKey;
        private final String manySystemObjectsMessageKey;
        private final String failProcessingMessageKey;
        private final String successProcessingMessageKey;
        private final LegacyDataImportFile importFile;
        private BufferedWriter errorFile;
        protected BufferedWriter errorDescriptionFile;

        ReferenceDataProcessor(String entityTable, long attributeTypeId, String correctionTable,
                ProcessItem item, LegacyDataImportFile importFile) {
            this.entityTable = entityTable;
            this.attributeTypeId = attributeTypeId;
            this.correctionTable = correctionTable;
            this.item = item;
            this.importFile = importFile;
            this.beginProcessingMessageKey = correctionTable + BEGIN_PROCESSING_KEY_SUFFIX;
            this.manySystemObjectsMessageKey = correctionTable + MANY_SYSTEM_OBJECTS_KEY_SUFFIX;
            this.failProcessingMessageKey = correctionTable + FAIL_PROCESSING_KEY_SUFFIX;
            this.successProcessingMessageKey = correctionTable + SUCCESS_PROCESSING_KEY_SUFFIX;
        }

        ReferenceDataProcessor(String entityTable, long attributeTypeId, ProcessItem item,
                LegacyDataImportFile importFile) {
            this(entityTable, attributeTypeId, entityTable, item, importFile);
        }

        protected final IStrategy strategy() {
            return strategyFactory.getStrategy(entityTable);
        }

        @Override
        public void clearProcessingStatus() {
            referenceDataCorrectionBean.clearProcessingStatus(correctionTable, jekIds);
        }

        @Override
        public void closeFiles() {
            closeFile(errorFile);
            closeFile(errorDescriptionFile);
        }

        /**
         * Checks whether reserved corrections were loaded.
         * @return false if and only if reserved corrections were loaded successfully.
         */
        protected boolean checkReservedCorrections() throws Exception {
            return false;
        }

        /**
         * Returns reserved correction ids mapped to corresponding reserved system object ids
         * @return map where each reserved correction's id is mapped to corresponding reserved system object's id.
         */
        protected Map<Long, Long> getReservedIdMap() {
            return null;
        }

        protected final LegacyDataImportFile getImportFile() {
            return importFile;
        }

        @Override
        public void process() throws Exception {
            final Map<Long, Long> reservedIdMap = getReservedIdMap();

            processingStatuses.put(item, new ImportStatus(0));
            final int count = referenceDataCorrectionBean.countForProcessing(correctionTable, jekIds);
            messages.add(new ImportMessage(getString(beginProcessingMessageKey, count), INFO));
            boolean wasErrors = false;

            for (String idjek : jekIds) {
                int jekCount = referenceDataCorrectionBean.countForProcessing(correctionTable, idjek);
                while (jekCount > 0) {
                    List<ReferenceDataCorrection> corrections =
                            referenceDataCorrectionBean.findForProcessing(correctionTable, idjek, PROCESSING_BATCH);

                    for (ReferenceDataCorrection correction : corrections) {
                        String errorDescription = null;

                        final long correctionId = correction.getId();
                        if (reservedIdMap != null && reservedIdMap.keySet().contains(correctionId)) {
                            correction.setSystemObjectId(reservedIdMap.get(correctionId));
                        } else {
                            try {
                                Long systemObjectId =
                                        referenceDataCorrectionBean.findSystemObject(entityTable, correction.getNkod());
                                if (systemObjectId == null) {
                                    IStrategy strategy = strategy();
                                    DomainObject systemObject = strategy.newInstance();
                                    Utils.setValue(systemObject.getAttribute(attributeTypeId), correction.getNkod());
                                    strategy.insert(systemObject, DateUtil.getCurrentDate());
                                    systemObjectId = systemObject.getId();
                                }
                                correction.setSystemObjectId(systemObjectId);
                            } catch (TooManyResultsException e) {
                                errorDescription = getString(manySystemObjectsMessageKey, correction.getId(),
                                        idjek, correction.getNkod());
                            }
                        }

                        correction.setProcessed(true);
                        referenceDataCorrectionBean.update(correction);

                        processingStatuses.get(item).increment();

                        if (errorDescription != null) {
                            wasErrors = true;
                            if (errorFile == null) {
                                errorFile = getErrorFile(errorsDirectory, importFile);
                                errorFile.write(importFile.getCsvHeader());
                                errorFile.newLine();
                            }
                            if (errorDescriptionFile == null) {
                                errorDescriptionFile = getErrorDescriptionFile(errorsDirectory,
                                        importFile);
                            }

                            errorFile.write(correction.getContent());
                            errorFile.newLine();

                            errorDescriptionFile.write(errorDescription);
                            errorDescriptionFile.newLine();
                        }
                    }

                    jekCount = referenceDataCorrectionBean.countForProcessing(correctionTable, idjek);
                }
            }

            wasErrors |= checkReservedCorrections();

            if (wasErrors) {
                messages.add(new ImportMessage(getString(failProcessingMessageKey, count), WARN));
            } else {
                messages.add(new ImportMessage(getString(successProcessingMessageKey, count), INFO));
            }
            processingStatuses.get(item).finish();
        }
    }

    private void processOwnershipForms() throws Exception {
        handleProcess(new ReferenceDataProcessor("ownership_form", OwnershipFormStrategy.NAME,
                ProcessItem.OWNERSHIP_FORM, LegacyDataImportFile.OWNERSHIP_FORM));
    }

    private void processOwnerRelationships() throws Exception {
        handleProcess(new ReferenceDataProcessor("owner_relationship", OwnerRelationshipStrategy.NAME,
                ProcessItem.OWNER_RELATIONSHIP, LegacyDataImportFile.OWNER_RELATIONSHIP) {

            @Override
            protected Map<Long, Long> getReservedIdMap() {
                return ImmutableMap.of(ReferenceDataCorrectionBean.DAUGHTER, OwnerRelationshipStrategy.DAUGHTER,
                        ReferenceDataCorrectionBean.SON, OwnerRelationshipStrategy.SON);
            }

            @Override
            protected boolean checkReservedCorrections() throws Exception {
                try {
                    referenceDataCorrectionBean.checkReservedOwnerRelationships(jekIds);
                    reservedOwnerRelationshipResolved = true;
                    return false;
                } catch (ReferenceDataCorrectionBean.OwnerRelationshipsNotResolved e) {
                    StringBuilder sb = new StringBuilder();
                    IStrategy strategy = strategy();
                    if (!e.isDaughterResolved()) {
                        sb.append(strategy.displayDomainObject(
                                strategy.findById(OwnerRelationshipStrategy.DAUGHTER, true), localeBean.getSystemLocale())).
                                append(", ");
                    }
                    if (!e.isSonResolved()) {
                        sb.append(strategy.displayDomainObject(
                                strategy.findById(OwnerRelationshipStrategy.SON, true), localeBean.getSystemLocale())).
                                append(", ");
                    }
                    sb.delete(sb.length() - 2, sb.length());
                    String error = getString("reserved_owner_relationship_not_resolved", sb.toString(), jekIds.toString());
                    if (errorDescriptionFile == null) {
                        errorDescriptionFile = getErrorDescriptionFile(errorsDirectory, getImportFile());
                    }
                    errorDescriptionFile.write(error);
                    errorDescriptionFile.newLine();

                    messages.add(new ImportMessage(error, WARN));
                    return true;
                }
            }
        });
    }

    private void processMilitaryDuties() throws Exception {
        handleProcess(new ReferenceDataProcessor("military_service_relation", MilitaryServiceRelationStrategy.NAME,
                "military_duty", ProcessItem.MILITARY_DUTY, LegacyDataImportFile.MILITARY_DUTY));
    }

    private void processRegistrationsTypes() throws Exception {
        handleProcess(new ReferenceDataProcessor("registration_type", RegistrationTypeStrategy.NAME,
                ProcessItem.REGISTRATION_TYPE, LegacyDataImportFile.REGISTRATION_TYPE) {

            @Override
            protected Map<Long, Long> getReservedIdMap() {
                return ImmutableMap.of(ReferenceDataCorrectionBean.PERMANENT, RegistrationTypeStrategy.PERMANENT);
            }

            @Override
            protected boolean checkReservedCorrections() throws Exception {
                try {
                    referenceDataCorrectionBean.checkReservedRegistrationTypes(jekIds);
                    reservedRegistrationTypesResolved = true;
                    return false;
                } catch (ReferenceDataCorrectionBean.RegistrationTypeNotResolved e) {
                    IStrategy strategy = strategy();
                    final String error = getString("reserved_registration_type_not_resolved",
                            strategy.displayDomainObject(
                            strategy.findById(RegistrationTypeStrategy.PERMANENT, true),
                            localeBean.getSystemLocale()),
                            jekIds.toString());

                    if (errorDescriptionFile == null) {
                        errorDescriptionFile = getErrorDescriptionFile(errorsDirectory, getImportFile());
                    }
                    errorDescriptionFile.write(error);
                    errorDescriptionFile.newLine();

                    messages.add(new ImportMessage(error, WARN));

                    return true;
                }
            }
        });
    }

    private void processDocumentsTypes() throws Exception {
        handleProcess(new ReferenceDataProcessor("document_type", DocumentTypeStrategy.NAME,
                ProcessItem.DOCUMENT_TYPE, LegacyDataImportFile.DOCUMENT_TYPE) {

            @Override
            protected Map<Long, Long> getReservedIdMap() {
                return ImmutableMap.of(ReferenceDataCorrectionBean.PASSPORT, DocumentTypeStrategy.PASSPORT,
                        ReferenceDataCorrectionBean.BIRTH_CERTIFICATE, DocumentTypeStrategy.BIRTH_CERTIFICATE);
            }

            @Override
            protected boolean checkReservedCorrections() throws Exception {
                try {
                    referenceDataCorrectionBean.checkReservedDocumentTypes(jekIds);
                    reservedDocumentTypesResolved = true;
                    return false;
                } catch (ReferenceDataCorrectionBean.DocumentTypesNotResolved e) {
                    StringBuilder sb = new StringBuilder();
                    IStrategy strategy = strategy();
                    if (!e.isPassportResolved()) {
                        sb.append(strategy.displayDomainObject(
                                strategy.findById(DocumentTypeStrategy.PASSPORT, true),
                                localeBean.getSystemLocale())).
                                append(", ");
                    }
                    if (!e.isBirthCertificateResolved()) {
                        sb.append(strategy.displayDomainObject(
                                strategy.findById(DocumentTypeStrategy.BIRTH_CERTIFICATE, true),
                                localeBean.getSystemLocale())).
                                append(", ");
                    }
                    sb.delete(sb.length() - 2, sb.length());
                    String error = getString("reserved_document_type_not_resolved", sb.toString(), jekIds.toString());

                    if (errorDescriptionFile == null) {
                        errorDescriptionFile = getErrorDescriptionFile(errorsDirectory, getImportFile());
                    }
                    errorDescriptionFile.write(error);
                    errorDescriptionFile.newLine();

                    messages.add(new ImportMessage(error, WARN));

                    return true;
                }
            }
        });
    }

    private void processOwnerTypes() throws Exception {
        handleProcess(new Processor() {

            BufferedWriter ownerTypeErrorDescriptionFile;

            @Override
            public void process() throws Exception {
                ownerType = referenceDataCorrectionBean.getReservedOwnerType(jekIds);
                if (!Strings.isEmpty(ownerType)) {
                    messages.add(new ImportMessage(getString("owner_type_success_finish_processing"), INFO));
                } else {
                    String error = getString("owner_type_fail_finish_processing", jekIds.toString());

                    ownerTypeErrorDescriptionFile = getErrorDescriptionFile(errorsDirectory, LegacyDataImportFile.OWNER_TYPE);
                    ownerTypeErrorDescriptionFile.write(error);
                    ownerTypeErrorDescriptionFile.newLine();

                    messages.add(new ImportMessage(error, WARN));
                }

                ImportStatus status = new ImportStatus(1);
                status.finish();
                processingStatuses.put(ProcessItem.OWNER_TYPE, status);
            }

            @Override
            public void clearProcessingStatus() {
            }

            @Override
            public void closeFiles() {
                closeFile(ownerTypeErrorDescriptionFile);
            }
        });
    }

    private void processPersons() throws Exception {
        if (!reservedDocumentTypesResolved) {
            return;
        }

        handleProcess(new Processor() {

            BufferedWriter personErrorFile;
            BufferedWriter personErrorDescriptionFile;

            @Override
            public void process() throws Exception {
                final Date creationDate = DateUtil.getCurrentDate();
                final ProcessItem item = ProcessItem.PERSON;
                final String searchIdJek = jekIds.iterator().next();

                processingStatuses.put(item, new ImportStatus(0));
                final int count = personCorrectionBean.countForProcessing();
                final int archiveCount = personCorrectionBean.archiveCount();
                messages.add(new ImportMessage(getString("begin_person_processing", count), INFO));
                boolean wasErrors = false;

                int leftPersons = count;
                while (leftPersons > 0) {
                    List<PersonCorrection> persons = personCorrectionBean.findForProcessing(PROCESSING_BATCH);

                    for (PersonCorrection p : persons) {
                        String errorDescription = null;

                        if (p.getSystemPersonId() == null) {
                            if (!Strings.isEmpty(p.getFam()) && !Strings.isEmpty(p.getIm()) && !Strings.isEmpty(p.getOt())) {
                                try {
                                    Person systemPerson = personCorrectionBean.findSystemPerson(p);
                                    if (systemPerson != null) {
                                        p.setSystemPersonId(systemPerson.getId());
                                        p.setKid(systemPerson.isKid());
                                        errorDescription = getString("person_exists", p.getId());
                                    } else {
                                        if (PersonCorrectionBean.isBirthDateValid(p.getDatar())
                                                && PersonCorrectionBean.isDocumentDataValid(p.getIddok(), p.getDokseria(), p.getDoknom())) {

                                            Date birthDate = DateUtil.asDate(p.getDatar(), Utils.DATE_PATTERN);
                                            p.setKid(!DateUtil.isValidDateInterval(creationDate, birthDate, PersonStrategy.AGE_THRESHOLD));

                                            //document type
                                            Long systemDocumentTypeId = null;
                                            /* Optimization: do not load reference data if it is reserved document type */
                                            if (String.valueOf(ReferenceDataCorrectionBean.PASSPORT).equalsIgnoreCase(p.getIddok())) {
                                                systemDocumentTypeId = DocumentTypeStrategy.PASSPORT;
                                            } else if (String.valueOf(ReferenceDataCorrectionBean.BIRTH_CERTIFICATE).equalsIgnoreCase(p.getIddok())) {
                                                systemDocumentTypeId = DocumentTypeStrategy.BIRTH_CERTIFICATE;
                                            } else {
                                                ReferenceDataCorrection documentType =
                                                        referenceDataCorrectionBean.getById("document_type", p.getIddok(), searchIdJek);
                                                if (documentType == null) {
                                                    errorDescription = getString("person_document_type_not_found",
                                                            p.getId(), p.getIddok(), searchIdJek);
                                                } else if (documentType.getSystemObjectId() == null) {
                                                    errorDescription = getString("person_document_type_not_resolved",
                                                            p.getId(), p.getIdarm(), searchIdJek);
                                                }
                                            }

                                            if (systemDocumentTypeId != null) {// document type resolved
                                                //military service relation
                                                ReferenceDataCorrection militaryDity =
                                                        referenceDataCorrectionBean.getById("military_duty", p.getIdarm(), searchIdJek);
                                                if (militaryDity == null) {
                                                    errorDescription = getString("person_military_duty_not_found",
                                                            p.getId(), p.getIdarm(), searchIdJek);
                                                } else if (militaryDity.getSystemObjectId() == null) {
                                                    errorDescription = getString("person_military_duty_not_resolved",
                                                            p.getId(), p.getIdarm(), searchIdJek);
                                                }

                                                systemPerson = personCorrectionBean.newSystemPerson(p, birthDate, systemDocumentTypeId,
                                                        militaryDity != null ? militaryDity.getSystemObjectId() : null);
                                                personStrategy.insert(systemPerson, creationDate);
                                                p.setSystemPersonId(systemPerson.getId());
                                            }
                                        } else {
                                            errorDescription = getString("invalid_person_data", p.getId(), p.getDatar(),
                                                    p.getIddok(), p.getDokseria(), p.getDoknom());
                                        }
                                    }
                                } catch (TooManyResultsException e) {
                                    errorDescription = getString("person_system_many_objects", p.getId(),
                                            p.getFam(), p.getIm(), p.getOt());
                                }
                            } else {
                                errorDescription = getString("invalid_person_fio", p.getId(), p.getFam(), p.getIm(),
                                        p.getOt());
                            }
                        } else {
                            errorDescription = getString("person_exists", p.getId());
                        }

                        p.setProcessed(true);
                        personCorrectionBean.update(p);

                        processingStatuses.get(item).increment();

                        if (errorDescription != null) {
                            wasErrors = true;
                            if (personErrorFile == null) {
                                personErrorFile = getErrorFile(errorsDirectory, LegacyDataImportFile.PERSON);
                                personErrorFile.write(LegacyDataImportFile.PERSON.getCsvHeader());
                                personErrorFile.newLine();
                            }
                            if (personErrorDescriptionFile == null) {
                                personErrorDescriptionFile = getErrorDescriptionFile(errorsDirectory,
                                        LegacyDataImportFile.PERSON);
                            }

                            personErrorFile.write(p.getContent());
                            personErrorFile.newLine();

                            personErrorDescriptionFile.write(errorDescription);
                            personErrorDescriptionFile.newLine();
                        }
                    }

                    leftPersons = personCorrectionBean.countForProcessing();
                }

                //children
                personCorrectionBean.clearProcessingStatus();

                List<PersonCorrection> children = personCorrectionBean.findChildren(PROCESSING_BATCH);
                while (!children.isEmpty()) {
                    for (PersonCorrection child : children) {
                        String errorDescription = null;
                        if (PersonCorrectionBean.isParentDataValid(child.getIdbud(), child.getKv(), child.getParentnom())) {
                            List<Long> systemParentIds = personCorrectionBean.findSystemParent(child.getIdbud(),
                                    child.getKv(), child.getParentnom());
                            if (systemParentIds.isEmpty()) {
                                errorDescription = getString("person_parent_not_found", child.getId(),
                                        child.getIdbud(), child.getKv(), child.getParentnom());
                            } else if (systemParentIds.size() > 1) {
                                errorDescription = getString("person_too_many_parents", child.getId(),
                                        child.getIdbud(), child.getKv(), child.getParentnom(), systemParentIds.toString());
                            } else {
                                personCorrectionBean.addChild(systemParentIds.get(0), child.getSystemPersonId(),
                                        child.getDatar(), creationDate);
                            }
                        } else {
                            errorDescription = getString("person_parent_data_invalid", child.getId(),
                                    child.getIdbud(), child.getKv(), child.getParentnom());
                        }

                        child.setProcessed(true);
                        personCorrectionBean.update(child);

                        if (errorDescription != null) {
                            wasErrors = true;
                            if (personErrorFile == null) {
                                personErrorFile = getErrorFile(errorsDirectory, LegacyDataImportFile.PERSON);
                                personErrorFile.write(LegacyDataImportFile.PERSON.getCsvHeader());
                                personErrorFile.newLine();
                            }
                            if (personErrorDescriptionFile == null) {
                                personErrorDescriptionFile = getErrorDescriptionFile(errorsDirectory,
                                        LegacyDataImportFile.PERSON);
                            }

                            personErrorFile.write(child.getContent());
                            personErrorFile.newLine();

                            personErrorDescriptionFile.write(errorDescription);
                            personErrorDescriptionFile.newLine();
                        }
                    }
                    children = personCorrectionBean.findChildren(PROCESSING_BATCH);
                }

                if (wasErrors) {
                    messages.add(new ImportMessage(getString("fail_finish_person_processing", count, archiveCount), WARN));
                } else {
                    messages.add(new ImportMessage(getString("success_finish_person_processing", count, archiveCount), INFO));
                }
                processingStatuses.get(item).finish();
            }

            @Override
            public void clearProcessingStatus() {
                personCorrectionBean.clearProcessingStatus();
            }

            @Override
            public void closeFiles() {
                closeFile(personErrorFile);
                closeFile(personErrorDescriptionFile);
            }
        });
    }

    private void processApartmentCards() throws Exception {
        if (Strings.isEmpty(ownerType) || !reservedDocumentTypesResolved) {
            return;
        }

        handleProcess(new Processor() {

            BufferedWriter apartmentCardErrorFile;
            BufferedWriter apartmentCardErrorDescriptionFile;

            @Override
            public void process() throws Exception {
                final ProcessItem item = ProcessItem.APARTMENT_CARD;

                processingStatuses.put(item, new ImportStatus(0));
                final int count = apartmentCardCorrectionBean.countForProcessing();
                final int archiveCount = apartmentCardCorrectionBean.archiveCount();
                messages.add(new ImportMessage(getString("begin_apartment_card_processing", count), INFO));
                boolean wasErrors = false;

                final Date creationDate = DateUtil.getCurrentDate();

                int leftCards = count;
                while (leftCards > 0) {
                    List<ApartmentCardCorrection> cards = apartmentCardCorrectionBean.findForProcessing(PROCESSING_BATCH);
                    for (ApartmentCardCorrection c : cards) {
                        String errorDescription = null;

                        if (c.getSystemApartmentCardId() == null) {

                            //building
                            if (!Strings.isEmpty(c.getIdbud())) {
                                BuildingCorrection building = null;
                                try {
                                    building = buildingCorrectionBean.findById(c.getIdbud(), jekIds);
                                    if (building != null) {
                                        final Long systemBuildingId = building.getSystemBuildingId();

                                        //apartment
                                        if (systemBuildingId != null) {
                                            if (!Strings.isEmpty(c.getKv())) {
                                                try {
                                                    Long systemApartmentId =
                                                            apartmentCardCorrectionBean.findSystemApartment(systemBuildingId, c.getKv());
                                                    if (systemApartmentId == null) {
                                                        //create new system apartment
                                                        DomainObject systemApartment =
                                                                apartmentCardCorrectionBean.newApartment(systemBuildingId, c.getKv(),
                                                                organizationMap.get(building.getIdjek()));
                                                        apartmentStrategy.insert(systemApartment, creationDate);
                                                        systemApartmentId = systemApartment.getId();
                                                    } else {
                                                        //system apartment already exists. Do nothing.
                                                    }

                                                    //apartment card itself
                                                    try {
                                                        Long systemApartmentCardId =
                                                                apartmentCardCorrectionBean.findSystemApartmentCard(c.getId(), systemApartmentId);
                                                        if (systemApartmentCardId == null) {
                                                            //create new system apartment card

                                                            //form of ownership: idprivat
                                                            if (!Strings.isEmpty(c.getIdprivat())) {
                                                                ReferenceDataCorrection ownershipForm =
                                                                        referenceDataCorrectionBean.getById("ownership_form", c.getIdprivat(), building.getIdjek());
                                                                if (ownershipForm == null) {
                                                                    errorDescription = getString("apartment_card_ownership_form_not_found",
                                                                            c.getId(), c.getIdprivat(), building.getIdjek());
                                                                } else if (ownershipForm.getSystemObjectId() == null) {
                                                                    errorDescription = getString("apartment_card_system_ownership_form_not_resolved",
                                                                            c.getId(), c.getIdprivat(), building.getIdjek());
                                                                } else {
                                                                    //owner.
                                                                    PersonCorrection owner =
                                                                            personCorrectionBean.getOwner(c.getIdbud(), c.getKv(), ownerType, c.getFio());
                                                                    if (owner == null) {
                                                                        errorDescription = getString("apartment_card_owner_not_resolved",
                                                                                c.getId(), c.getIdbud(), c.getKv(), ownerType);
                                                                    } else if (owner.getSystemPersonId() == null) {
                                                                        errorDescription = getString("apartment_card_system_owner_not_resolved",
                                                                                c.getId(), c.getIdbud(), c.getKv(), ownerType, owner.getId());
                                                                    } else {
                                                                        //system owner found. Now we can create new apartment card.
                                                                        ApartmentCard apartmentCard =
                                                                                apartmentCardCorrectionBean.newApartmentCard(
                                                                                c.getId(), systemApartmentId, owner.getSystemPersonId(),
                                                                                ownershipForm.getSystemObjectId(), organizationMap.get(building.getIdjek()));
                                                                        apartmentCardStrategy.insert(apartmentCard, creationDate);
                                                                        c.setSystemApartmentCardId(apartmentCard.getId());
                                                                    }
                                                                }
                                                            } else {
                                                                errorDescription = getString("apartment_card_ownership_form_not_found",
                                                                        c.getId(), c.getIdprivat(), building.getIdjek());
                                                            }
                                                        } else {
                                                            //system apartment card already exists. Do nothing.
                                                            errorDescription = getString("apartment_card_exists", c.getId());
                                                            c.setSystemApartmentCardId(systemApartmentCardId);
                                                        }
                                                    } catch (TooManyResultsException e) {
                                                        errorDescription = getString("apartment_card_too_many_system_apartment_cards",
                                                                c.getId(), c.getIdbud(), systemBuildingId, c.getKv(), systemApartmentId);
                                                    }
                                                } catch (TooManyResultsException e) {
                                                    errorDescription = getString("apartment_card_too_many_system_apartments",
                                                            c.getId(), c.getIdbud(), systemBuildingId, c.getKv());
                                                }
                                            } else {
                                                errorDescription = getString("apartment_card_apartment_empty", c.getId(),
                                                        c.getKv());
                                            }
                                        } else {
                                            errorDescription = getString("apartment_card_system_building_not_resolved", c.getId(),
                                                    c.getIdbud(), building.getIdjek());
                                        }
                                    } else {
                                        errorDescription = getString("apartment_card_building_not_found", c.getId(),
                                                c.getIdbud(), jekIds.toString());
                                    }
                                } catch (TooManyResultsException e) {
                                    errorDescription = getString("apartment_card_too_many_buildings", c.getId(),
                                            c.getIdbud(), jekIds.toString());
                                }
                            } else {
                                errorDescription = getString("apartment_card_building_empty", c.getId(),
                                        c.getIdbud());
                            }
                        } else {
                            //system apartment card already exists. Do nothing.
                            errorDescription = getString("apartment_card_exists", c.getId());
                        }

                        c.setProcessed(true);
                        apartmentCardCorrectionBean.update(c);

                        processingStatuses.get(item).increment();

                        if (errorDescription != null) {
                            wasErrors = true;
                            if (apartmentCardErrorFile == null) {
                                apartmentCardErrorFile = getErrorFile(errorsDirectory, LegacyDataImportFile.APARTMENT_CARD);
                                apartmentCardErrorFile.write(LegacyDataImportFile.APARTMENT_CARD.getCsvHeader());
                                apartmentCardErrorFile.newLine();
                            }
                            if (apartmentCardErrorDescriptionFile == null) {
                                apartmentCardErrorDescriptionFile = getErrorDescriptionFile(errorsDirectory,
                                        LegacyDataImportFile.APARTMENT_CARD);
                            }

                            apartmentCardErrorFile.write(c.getContent());
                            apartmentCardErrorFile.newLine();

                            apartmentCardErrorDescriptionFile.write(errorDescription);
                            apartmentCardErrorDescriptionFile.newLine();
                        }
                    }

                    leftCards = apartmentCardCorrectionBean.countForProcessing();
                }

                if (wasErrors) {
                    messages.add(new ImportMessage(getString("fail_finish_apartment_card_processing", count, archiveCount), WARN));
                } else {
                    messages.add(new ImportMessage(getString("success_finish_apartment_card_processing", count, archiveCount), INFO));
                }
                processingStatuses.get(item).finish();
            }

            @Override
            public void clearProcessingStatus() {
                apartmentCardCorrectionBean.clearProcessingStatus();
            }

            @Override
            public void closeFiles() {
                closeFile(apartmentCardErrorFile);
                closeFile(apartmentCardErrorDescriptionFile);
            }
        });
    }

    private void processRegistrations() throws Exception {
        if (Strings.isEmpty(ownerType) || !reservedDocumentTypesResolved
                || !reservedOwnerRelationshipResolved || !reservedRegistrationTypesResolved) {
            return;
        }

        handleProcess(new Processor() {

            BufferedWriter registrationErrorFile;
            BufferedWriter registrationErrorDescriptionFile;

            @Override
            public void process() throws Exception {
                final ProcessItem item = ProcessItem.REGISTRATION;

                processingStatuses.put(item, new ImportStatus(0));
                final int count = registrationCorrectionBean.countForProcessing();
                messages.add(new ImportMessage(getString("begin_registration_processing", count), INFO));
                boolean wasErrors = false;

                final Date creationDate = DateUtil.getCurrentDate();

                int leftCards = count;
                while (leftCards > 0) {
                    List<ApartmentCardCorrection> cards = registrationCorrectionBean.findApartmentCardsForProcessing(PROCESSING_BATCH);
                    for (ApartmentCardCorrection c : cards) {
                        List<String> errorDescriptions = Lists.newArrayList();

                        List<PersonCorrection> persons = personCorrectionBean.findByAddress(c.getIdbud(), c.getKv());
                        for (PersonCorrection p : persons) {
                            if (p.getSystemRegistrationId() == null) {
                                if (p.getSystemPersonId() == null) {
                                    errorDescriptions.add(getString("registration_system_person_not_resolved", p.getId()));
                                } else {
                                    ApartmentCard systemApartmentCard = apartmentCardStrategy.findById(c.getSystemApartmentCardId(),
                                            true, true, true, false);

                                    boolean exists = false;

                                    Date departureDate = DateUtil.asDate(p.getVdata(), Utils.DATE_PATTERN);
                                    boolean isFinishedRegistration = departureDate != null;
                                    for (Registration r : systemApartmentCard.getRegistrations()) {
                                        if (p.getSystemPersonId().equals(r.getPerson().getId())
                                                && ((isFinishedRegistration && r.isFinished())
                                                || (!isFinishedRegistration && !r.isFinished()))) {
                                            exists = true;
                                            break;
                                        }
                                    }

                                    if (exists) {
                                        errorDescriptions.add(getString("registration_exists", p.getId()));
                                    } else {
                                        //create new system registration
                                        Long systemRegistrationId = null;

                                        //registration date
                                        final Date registrationDate = DateUtil.asDate(p.getDprop(), Utils.DATE_PATTERN);
                                        if (registrationDate != null) {
                                            //owner relationship

                                            //at first find jek id:
                                            BuildingCorrection building = null;
                                            try {
                                                building = buildingCorrectionBean.findById(c.getIdbud(), jekIds);
                                            } catch (TooManyResultsException e) {
                                                //impossible case because we handle apartment cards that already 
                                                // were processed by processApartmentCards() and that processing 
                                                // includes search the building and check that there only one building found.
                                                throw new IllegalStateException("There are too many building corrections. Illegal state.");
                                            }
                                            ReferenceDataCorrection ownerRelationship =
                                                    referenceDataCorrectionBean.getById("owner_relationship", p.getIdrel(), building.getIdjek());
                                            if (ownerRelationship == null) {
                                                errorDescriptions.add(getString("registration_owner_relationship_not_found",
                                                        p.getId(), p.getIdrel(), building.getIdjek()));
                                            } else if (ownerRelationship.getSystemObjectId() == null) {
                                                errorDescriptions.add(getString("registration_system_owner_relationship_not_resolved",
                                                        p.getId(), p.getIdrel(), building.getIdjek()));
                                            } else {
                                                //registration type
                                                ReferenceDataCorrection registrationType =
                                                        referenceDataCorrectionBean.getById("registration_type", p.getIdvidp(), building.getIdjek());
                                                if (registrationType == null) {
                                                    errorDescriptions.add(getString("registration_registration_type_not_found",
                                                            p.getId(), p.getIdrel(), building.getIdjek()));
                                                } else if (registrationType.getSystemObjectId() == null) {
                                                    errorDescriptions.add(getString("registration_system_registration_type_not_resolved",
                                                            p.getId(), p.getIdvidp(), building.getIdjek()));
                                                } else {
                                                    final long systemRegistrationTypeId = registrationType.getSystemObjectId();

                                                    //arrival street
                                                    String arrivalStreet = null;
                                                    StreetCorrection arrivalStreetCorrection =
                                                            streetCorrectionBean.getById(p.getPidul(), building.getIdjek());
                                                    if (arrivalStreetCorrection != null) {
                                                        //at first try to take ukrainian street name
                                                        if (!Strings.isEmpty(arrivalStreetCorrection.getNkod())) {
                                                            arrivalStreet = arrivalStreetCorrection.getUtype()
                                                                    + " " + arrivalStreetCorrection.getNkod();
                                                        } else {
                                                            arrivalStreet = arrivalStreetCorrection.getRtype()
                                                                    + " " + arrivalStreetCorrection.getNkod1();
                                                        }
                                                        if (Strings.isEmpty(arrivalStreet)) {
                                                            errorDescriptions.add(getString("registration_arrival_street_empty",
                                                                    p.getId(), p.getPidul(),
                                                                    arrivalStreetCorrection.getUtype() + " " + arrivalStreetCorrection.getNkod(),
                                                                    arrivalStreetCorrection.getRtype() + " " + arrivalStreetCorrection.getNkod1(),
                                                                    building.getIdjek()));
                                                        }
                                                    } else {
                                                        errorDescriptions.add(getString("registration_arrival_street_not_found",
                                                                p.getId(), p.getPidul(), building.getIdjek()));
                                                    }

                                                    //arrival date
                                                    Date arrivalDate1 = DateUtil.asDate(p.getPdpribza(), Utils.DATE_PATTERN);
                                                    Date arrivalDate2 = DateUtil.asDate(p.getPdpribvm(), Utils.DATE_PATTERN);
                                                    Date arrivalDate = DateUtil.getMax(arrivalDate1, arrivalDate2);
                                                    if (arrivalDate == null) {
                                                        errorDescriptions.add(getString("registration_arrival_date_invalid",
                                                                p.getId(), p.getPdpribza(), p.getPdpribvm(), building.getIdjek()));
                                                    }

                                                    String departureStreet = null;
                                                    String departureReason = null;
                                                    if (isFinishedRegistration) {
                                                        //departure street
                                                        StreetCorrection departureStreetCorrection =
                                                                streetCorrectionBean.getById(p.getVidul(), building.getIdjek());
                                                        if (departureStreetCorrection != null) {
                                                            //at first try to take ukrainian street name
                                                            if (!Strings.isEmpty(departureStreetCorrection.getNkod())) {
                                                                departureStreet = departureStreetCorrection.getUtype()
                                                                        + " " + departureStreetCorrection.getNkod();
                                                            } else {
                                                                departureStreet = departureStreetCorrection.getRtype()
                                                                        + " " + departureStreetCorrection.getNkod1();
                                                            }
                                                            if (Strings.isEmpty(departureStreet)) {
                                                                errorDescriptions.add(getString("registration_departure_street_empty",
                                                                        p.getId(), p.getVidul(),
                                                                        departureStreetCorrection.getUtype() + " " + departureStreetCorrection.getNkod(),
                                                                        departureStreetCorrection.getRtype() + " " + departureStreetCorrection.getNkod1(),
                                                                        building.getIdjek()));
                                                            }
                                                        } else {
                                                            errorDescriptions.add(getString("registration_departure_street_not_found",
                                                                    p.getId(), p.getVidul(), building.getIdjek()));
                                                        }

                                                        //departure reason
                                                        ReferenceDataCorrection departureReasonCorrection =
                                                                referenceDataCorrectionBean.getById("departure_reason", p.getIdvip(), building.getIdjek());
                                                        if (departureReasonCorrection != null) {
                                                            departureReason = departureReasonCorrection.getNkod();
                                                            if (Strings.isEmpty(departureReason)) {
                                                                errorDescriptions.add(getString("registration_departure_reason_empty",
                                                                        p.getId(), p.getIdvip(), building.getIdjek()));
                                                            }
                                                        } else {
                                                            errorDescriptions.add(getString("registration_departure_reason_not_found",
                                                                    p.getId(), p.getIdvip(), building.getIdjek()));
                                                        }

                                                        //departure date
                                                        if (departureDate == null) {
                                                            errorDescriptions.add(getString("registration_departure_date_invalid",
                                                                    p.getId(), p.getVdata(), building.getIdjek()));
                                                        }
                                                    }

                                                    //now we can create new registration.
                                                    Registration registration =
                                                            registrationCorrectionBean.newRegistration(p, isFinishedRegistration,
                                                            p.getSystemPersonId(), ownerRelationship.getSystemObjectId(),
                                                            systemRegistrationTypeId, registrationDate,
                                                            arrivalDate, arrivalStreet,
                                                            departureDate, departureStreet, departureReason);

                                                    if (!isFinishedRegistration) {
                                                        //need validation

                                                        boolean isValid = true;
                                                        {
                                                            //registration date must be greater than person's birth date
                                                            if (registration.getPerson().getBirthDate().after(registration.getRegistrationDate())) {
                                                                isValid = false;
                                                                errorDescriptions.add(getString("registration_date_validation_error",
                                                                        p.getId(), Utils.displayDate(registrationDate),
                                                                        Utils.displayDate(registration.getPerson().getBirthDate())));
                                                            }

                                                            //permanent registration type
                                                            if (systemRegistrationTypeId == RegistrationTypeStrategy.PERMANENT) {
                                                                String address = personStrategy.findPermanentRegistrationAddress(registration.getPerson().getId(),
                                                                        localeBean.getSystemLocale());
                                                                if (!Strings.isEmpty(address)) {
                                                                    isValid = false;
                                                                    errorDescriptions.add(getString("registration_permanent_registration_type_validation_error",
                                                                            p.getId(), address));
                                                                }
                                                            }

                                                            //duplicate person registration check
                                                            if (!registrationStrategy.validateDuplicatePerson(systemApartmentCard.getId(), registration.getPerson().getId())) {
                                                                isValid = false;
                                                                errorDescriptions.add(getString("registration_person_already_registered_validation_error",
                                                                        p.getId()));
                                                            }
                                                        }
                                                        if (isValid) {
                                                            systemRegistrationId =
                                                                    registrationCorrectionBean.addRegistration(systemApartmentCard, registration, creationDate);
                                                        }
                                                    } else { // validation not needed.
                                                        systemRegistrationId =
                                                                registrationCorrectionBean.addRegistration(systemApartmentCard, registration, creationDate);
                                                        registrationStrategy.disable(registration, creationDate);
                                                    }
                                                }
                                            }
                                        } else {
                                            errorDescriptions.add(getString("registration_date_invalid", p.getId(), p.getDprop()));
                                        }

                                        if (systemRegistrationId != null) {
                                            p.setSystemRegistrationId(systemRegistrationId);
                                            registrationCorrectionBean.updatePerson(p);
                                        }
                                    }
                                }
                            } else {
                                errorDescriptions.add(getString("registration_exists", p.getId()));
                            }
                        }

                        c.setProcessed(true);
                        registrationCorrectionBean.updateApartmentCard(c);

                        processingStatuses.get(item).increment();

                        if (!errorDescriptions.isEmpty()) {
                            wasErrors = true;
                            if (registrationErrorFile == null) {
                                registrationErrorFile = getErrorFile(errorsDirectory, RegistrationCorrectionBean.REGISTRATION_FILE_NAME);
                                registrationErrorFile.write(RegistrationCorrectionBean.REGISTRATION_FILE_HEADER);
                                registrationErrorFile.newLine();
                            }
                            if (registrationErrorDescriptionFile == null) {
                                registrationErrorDescriptionFile = getErrorDescriptionFile(errorsDirectory,
                                        RegistrationCorrectionBean.REGISTRATION_FILE_NAME);
                            }

                            registrationErrorFile.write(c.getContent());
                            registrationErrorFile.newLine();

                            for (String error : errorDescriptions) {
                                registrationErrorDescriptionFile.write(error);
                                registrationErrorDescriptionFile.newLine();
                            }
                        }
                    }

                    leftCards = registrationCorrectionBean.countForProcessing();
                }

                if (wasErrors) {
                    messages.add(new ImportMessage(getString("fail_finish_registration_processing", count), WARN));
                } else {
                    messages.add(new ImportMessage(getString("success_finish_registration_processing", count), INFO));
                }
                processingStatuses.get(item).finish();
            }

            @Override
            public void clearProcessingStatus() {
                apartmentCardCorrectionBean.clearProcessingStatus();
            }

            @Override
            public void closeFiles() {
                closeFile(registrationErrorFile);
                closeFile(registrationErrorDescriptionFile);
            }
        });
    }

    private String getString(String key, Object... parameters) {
        return ResourceUtil.getFormatString(RESOURCE_BUNDLE, key, locale, parameters);
    }

    private static String getContent(String[] line) {
        StringBuilder content = new StringBuilder();
        boolean firstLine = true;
        for (String field : line) {
            if (!firstLine) {
                content.append(SEPARATOR);
            } else {
                firstLine = false;
            }
            content.append(field);
        }
        return content.toString();
    }

    public void cleanData(Set<String> jekIds) {
        try {
            clean();

            userTransaction.begin();

            streetCorrectionBean.cleanData(jekIds);
            buildingCorrectionBean.cleanData(jekIds);
            referenceDataCorrectionBean.cleanData("ownership_form", jekIds);
            referenceDataCorrectionBean.cleanData("military_duty", jekIds);
            referenceDataCorrectionBean.cleanData("owner_relationship", jekIds);
            referenceDataCorrectionBean.cleanData("departure_reason", jekIds);
            referenceDataCorrectionBean.cleanData("registration_type", jekIds);
            referenceDataCorrectionBean.cleanData("document_type", jekIds);
            referenceDataCorrectionBean.cleanData("owner_type", jekIds);
            personCorrectionBean.cleanData();
            apartmentCardCorrectionBean.cleanData();

            userTransaction.commit();
        } catch (Exception e) {
            try {
                userTransaction.rollback();
            } catch (SystemException e1) {
                log.error("Couldn't to rollback transaction.", e1);
            }
            throw new RuntimeException(e);
        }
    }

    private CSVReader getCsvReader(String dir, LegacyDataImportFile file, String charsetName, char separator) throws ImportFileNotFoundException {
        try {
            return new CSVReader(new InputStreamReader(new FileInputStream(
                    new File(dir, file.getFileName())), charsetName), separator, CSVWriter.NO_QUOTE_CHARACTER, 1);
        } catch (Exception e) {
            throw new ImportFileNotFoundException(e, file.getFileName());
        }
    }

    private BufferedWriter getErrorFile(String dir, LegacyDataImportFile file) throws OpenErrorFileException {
        return getErrorFile(dir, file.getFileName());
    }

    private BufferedWriter getErrorFile(String dir, String fileName) throws OpenErrorFileException {
        String name = fileName.substring(0, fileName.lastIndexOf(".")) + ERROR_FILE_SUFFIX;
        try {
            return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(dir, name)), CHARSET));
        } catch (Exception e) {
            throw new OpenErrorFileException(e, name);
        }
    }

    private BufferedWriter getErrorDescriptionFile(String dir, LegacyDataImportFile file) throws OpenErrorDescriptionFileException {
        return getErrorDescriptionFile(dir, file.getFileName());
    }

    private BufferedWriter getErrorDescriptionFile(String dir, String fileName) throws OpenErrorDescriptionFileException {
        String name = fileName.substring(0, fileName.lastIndexOf(".")) + ERROR_DESCRIPTION_FILE_SUFFIX;
        try {
            return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(dir, name)), CHARSET));
        } catch (Exception e) {
            throw new OpenErrorDescriptionFileException(e, name);
        }
    }
}
