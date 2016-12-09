package org.complitex.osznconnection.file.service.process;

import com.google.common.collect.ImmutableMap;
import org.complitex.common.entity.IExecutorObject;
import org.complitex.common.entity.Log;
import org.complitex.common.service.BroadcastService;
import org.complitex.common.service.ConfigBean;
import org.complitex.common.service.LogBean;
import org.complitex.common.service.executor.ExecutorService;
import org.complitex.common.service.executor.IExecutorListener;
import org.complitex.common.service.executor.ITaskBean;
import org.complitex.osznconnection.file.Module;
import org.complitex.osznconnection.file.entity.*;
import org.complitex.osznconnection.file.entity.FileHandlingConfig;
import org.complitex.osznconnection.file.entity.privilege.PrivilegeFileGroup;
import org.complitex.osznconnection.file.entity.subsidy.RequestFileGroup;
import org.complitex.osznconnection.file.entity.subsidy.SubsidyMasterDataFile;
import org.complitex.osznconnection.file.service.RequestFileBean;
import org.complitex.osznconnection.file.service.exception.StorageNotFoundException;
import org.complitex.osznconnection.file.service.privilege.PrivilegeFileGroupBean;
import org.complitex.osznconnection.file.service.privilege.task.*;
import org.complitex.osznconnection.file.service.subsidy.RequestFileGroupBean;
import org.complitex.osznconnection.file.service.subsidy.SubsidyBean;
import org.complitex.osznconnection.file.service.subsidy.task.*;
import org.complitex.osznconnection.file.service.warning.ReportWarningRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.ejb.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.complitex.common.util.DateUtil.newDate;
import static org.complitex.osznconnection.file.entity.FileHandlingConfig.*;
import static org.complitex.osznconnection.file.service.process.ProcessType.*;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 01.11.10 12:55
 */
@Singleton(name = "ProcessManagerBean")
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
@TransactionManagement(TransactionManagementType.BEAN)
public class ProcessManagerBean {
    private final Logger log = LoggerFactory.getLogger(ProcessManagerBean.class);

    @Resource
    private SessionContext sessionContext;

    @EJB
    private ExecutorService executorService;

    @EJB
    private ConfigBean configBean;

    @EJB
    private LogBean logBean;

    @EJB
    private ReportWarningRenderer reportWarningRenderer;

    @EJB
    private RequestFileGroupBean requestFileGroupBean;

    @EJB
    private PrivilegeFileGroupBean privilegeFileGroupBean;

    @EJB
    private RequestFileBean requestFileBean;

    @EJB
    private SubsidyBean subsidyBean;

    @EJB
    private BroadcastService broadcastService;

    private Map<String, Map<ProcessType, Process>> processStatusMap = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    private <T extends IExecutorObject> Process<T> getProcess(ProcessType processType) {
        //Principal Name
        String principalName = sessionContext.getCallerPrincipal().getName();

        Map<ProcessType, Process> map = processStatusMap.get(principalName);

        Process process = null;

        if (map != null) {
            process = map.get(processType);
        } else {
            map = new EnumMap<>(ProcessType.class);
            processStatusMap.put(principalName, map);
        }

        if (process == null) {
            process = new Process(processType);
            map.put(processType, process);
        }

        return process;
    }

    private List<Process> getAllUsersProcess(ProcessType processType) {
        List<Process> processes = new ArrayList<Process>();

        for (Map<ProcessType, Process> map : processStatusMap.values()) {
            Process p = map.get(processType);

            if (p != null) {
                processes.add(p);
            }
        }

        return processes;
    }

    public List<RequestFile> getLinkError(ProcessType processType, boolean flush) {
        Process process = getProcess(processType);

        if (process != null) {
            return process.getLinkError(flush);
        }

        return Collections.emptyList();
    }

    public <T extends IExecutorObject> List<T> getProcessed(ProcessType processType, Object queryKey) {
        return (List<T>) getProcess(processType).getProcessed(queryKey);
    }

    public int getSuccessCount(ProcessType processType) {
        return getProcess(processType).getSuccessCount();
    }

    public int getSkippedCount(ProcessType processType) {
        return getProcess(processType).getSkippedCount();
    }

    public int getErrorCount(ProcessType processType) {
        return getProcess(processType).getErrorCount();
    }

    public boolean isProcessing(ProcessType processType) {
        return getProcess(processType).isProcessing();
    }

    public boolean isCriticalError(ProcessType processType) {
        return getProcess(processType).isCriticalError();
    }

    public boolean isCompleted(ProcessType processType) {
        return getProcess(processType).isCompleted();
    }

    public boolean isCanceled(ProcessType processType) {
        return getProcess(processType).isCanceled();
    }

    public void cancel(ProcessType processType) {
        getProcess(processType).cancel();
    }

    public boolean isGlobalWaiting(ProcessType processType, IExecutorObject executorObject) {
        for (Process process : getAllUsersProcess(processType)) {
            if (process.isRunning() && process.isWaiting(executorObject)) {
                return true;
            }
        }

        return false;
    }

    public boolean isProcessing(IExecutorObject executorObject, ProcessType... processTypes) {
        if (executorObject == null || executorObject.isProcessing()){
            return true;
        }

        for (ProcessType processType : processTypes) {
            for (Process process : getAllUsersProcess(processType)) {
                if (process.isRunning() && process.isWaiting(executorObject)) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean isGlobalProcessing(ProcessType processType) {
        for (Process process : getAllUsersProcess(processType)) {
            if (process.isProcessing()) {
                return true;
            }
        }

        return false;
    }

    private <T extends IExecutorObject> void execute(ProcessType processType, Class<? extends ITaskBean<T>> taskClass,
            List<T> list, IExecutorListener listener,
            FileHandlingConfig threadCount, FileHandlingConfig maxErrorCount, Map processParameters) {
        Process<T> process = getProcess(processType);

        if (!process.isRunning()) {
            process.init();

            process.setMaxThread(configBean.getInteger(threadCount, true));
            process.setMaxErrors(configBean.getInteger(maxErrorCount, true));
            process.setTaskClass(taskClass);
            process.setListener(listener);
            process.setCommandParameters(processParameters);

            process.addObjects(list);

            executorService.execute(process);
        } else {
            process.addObjects(list);

            int freeThreadCount = process.getMaxThread() - process.getRunningThreadCount();
            for (int i = 0; i < freeThreadCount; ++i) {
                executorService.executeNextAsync(process);
            }
        }
    }

    private List<RequestFileGroup> getRequestFileGroups(List<Long> ids) {
        List<RequestFileGroup> groups = new ArrayList<>();

        for (Long id : ids) {
            RequestFileGroup group = requestFileGroupBean.getRequestFileGroup(id);

            if (!isProcessing(group, BIND_GROUP, FILL_GROUP, SAVE_GROUP)) {
                groups.add(group);
            }
        }

        return groups;
    }

    private List<RequestFile> getRequestFiles(List<Long> ids, ProcessType... processTypes) {
        List<RequestFile> requestFiles = new ArrayList<>();

        for (Long id : ids) {
            RequestFile requestFile = requestFileBean.getRequestFile(id);

            if (!isProcessing(requestFile, processTypes)) { //todo check global processing
                requestFiles.add(requestFile);
            }
        }
        return requestFiles;
    }

    private List<RequestFile> getActualPaymentFiles(List<Long> ids) {
        return getRequestFiles(ids, BIND_ACTUAL_PAYMENT, FILL_ACTUAL_PAYMENT, SAVE_ACTUAL_PAYMENT);
    }

    private List<RequestFile> getSubsidyFiles(List<Long> ids) {
        return getRequestFiles(ids,  BIND_SUBSIDY, FILL_SUBSIDY, SAVE_SUBSIDY);
    }

    private List<RequestFile> getDwellingCharacteristicsFiles(List<Long> ids) {
        return getRequestFiles(ids, BIND_DWELLING_CHARACTERISTICS, FILL_DWELLING_CHARACTERISTICS, SAVE_DWELLING_CHARACTERISTICS);
    }

    private List<RequestFile> getFacilityServiceTypeFiles(List<Long> ids) {
        return getRequestFiles(ids, BIND_FACILITY_SERVICE_TYPE, FILL_FACILITY_SERVICE_TYPE, SAVE_FACILITY_SERVICE_TYPE);
    }

    private List<RequestFile> getFacilityForm2Files(List<Long> ids) {
        return getRequestFiles(ids, SAVE_FACILITY_FORM2);
    }

    private List<RequestFile> getFacilityLocalFiles(List<Long> ids) {
        return getRequestFiles(ids, SAVE_FACILITY_LOCAL);
    }

    private List<PrivilegeFileGroup> getPrivilegeFileGroups(List<Long> ids) {
        List<PrivilegeFileGroup> groups = new ArrayList<>();

        for (Long id : ids) {
            PrivilegeFileGroup group = privilegeFileGroupBean.getPrivilegeFileGroup(id);

            if (!isProcessing(group, BIND_GROUP, FILL_GROUP, SAVE_GROUP)) {
                groups.add(group);
            }
        }

        return groups;
    }

    /*Group*/

    public void loadGroup(Long userOrganizationId, Long organizationId, int year, int monthFrom, int monthTo) {
        Process<RequestFileGroup> process = getProcess(LOAD_GROUP);

        try {
            //поиск групп файлов запросов
            if (!process.isRunning()) {
                process.setPreprocess(true);
                process.init();
            }

            LoadGroupParameter<RequestFileGroup> loadParameter = LoadUtil.getLoadGroupParameter(userOrganizationId, organizationId,
                    monthFrom, monthTo, year);

            for (RequestFileGroup fileGroup : loadParameter.getRequestFileGroups()) {
                fileGroup.getPaymentFile().setUserOrganizationId(userOrganizationId);
                fileGroup.getBenefitFile().setUserOrganizationId(userOrganizationId);
            }

            List<RequestFile> linkError = loadParameter.getLinkError();

            process.addLinkError(linkError);

            for (RequestFile rf : linkError) {
                logBean.error(Module.NAME, ProcessManagerBean.class, RequestFileGroup.class, null, rf.getId(),
                        Log.EVENT.CREATE, rf.getLogChangeList(), "Связанный файл не найден для объекта {0}",
                        rf.getLogObjectName());
            }

            process.addObjects(loadParameter.getRequestFileGroups());

            //загрузка данных
            if (!process.isRunning()) {
                process.setPreprocess(false);
                process.setMaxErrors(configBean.getInteger(LOAD_MAX_ERROR_COUNT, true));
                process.setMaxThread(configBean.getInteger(LOAD_THREAD_SIZE, true));
                process.setTaskClass(GroupLoadTaskBean.class);

                executorService.execute(process);
            } else {
                int freeThreadCount = process.getMaxThread() - process.getRunningThreadCount();

                for (int i = 0; i < freeThreadCount; ++i) {
                    executorService.executeNextAsync(process);
                }
            }
        } catch (Exception e) {
            process.preprocessError();

            broadcastService.broadcast(getClass(), "onError", e);

            log.error("Ошибка процесса загрузки файлов.", e);
            logBean.error(Module.NAME, ProcessManagerBean.class, RequestFileGroup.class, null,
                    Log.EVENT.CREATE, "Ошибка процесса загрузки файлов. Причина: {0}", e.getMessage());
        }
    }

    public void bindGroup(List<Long> ids, Map processParameters) {
        execute(BIND_GROUP, GroupBindTaskBean.class, getRequestFileGroups(ids), null, BIND_THREAD_SIZE, BIND_MAX_ERROR_COUNT, processParameters);
    }

    public void fillGroup(List<Long> ids, Map processParameters) {
        execute(FILL_GROUP, GroupFillTaskBean.class, getRequestFileGroups(ids), null, FILL_THREAD_SIZE, FILL_MAX_ERROR_COUNT, processParameters);
    }

    public void saveGroup(List<Long> ids, Map processParameters) {
        IExecutorListener listener = new IExecutorListener() {

            @Override
            public void onComplete(List<IExecutorObject> processed) {
                try {
                    SaveUtil.createResult(processed, reportWarningRenderer);
                } catch (StorageNotFoundException e) {
                    log.error("Ошибка создания файла Result.txt.", e);
                    logBean.error(Module.NAME, ProcessManagerBean.class, RequestFileGroup.class, null,
                            Log.EVENT.CREATE, "Ошибка создания файла Result.txt. Причина: {0}", e.getMessage());
                }
            }
        };

        execute(SAVE_GROUP, GroupSaveTaskBean.class, getRequestFileGroups(ids), listener, SAVE_THREAD_SIZE, SAVE_MAX_ERROR_COUNT, processParameters);
    }

    /*ActualPayment*/

    public void loadActualPayment(Long userOrganizationId, Long organizationId, int year, int monthFrom, int monthTo) {
        try {
            List<RequestFile> list = LoadUtil.getActualPayments(userOrganizationId, organizationId, monthFrom, monthTo, year);

            for (RequestFile file : list) {
                file.setUserOrganizationId(userOrganizationId);
            }

            execute(LOAD_ACTUAL_PAYMENT, ActualPaymentLoadTaskBean.class, list, null, LOAD_THREAD_SIZE, LOAD_MAX_ERROR_COUNT, null);
        } catch (Exception e) {
            log.error("Ошибка процесса загрузки файлов.", e);
            logBean.error(Module.NAME, ProcessManagerBean.class, RequestFile.class, null,
                    Log.EVENT.CREATE, "Ошибка процесса загрузки файлов. Причина: {0}", e.getMessage());

            broadcastService.broadcast(getClass(), "error", e);
        }
    }

    public void bindActualPayment(List<Long> ids, Map processParameters) {
        execute(BIND_ACTUAL_PAYMENT, ActualPaymentBindTaskBean.class, getActualPaymentFiles(ids), null, BIND_THREAD_SIZE,
                BIND_MAX_ERROR_COUNT, processParameters);
    }

    public void fillActualPayment(List<Long> ids, Map processParameters) {
        execute(FILL_ACTUAL_PAYMENT, ActualPaymentFillTaskBean.class, getActualPaymentFiles(ids), null, FILL_THREAD_SIZE,
                FILL_MAX_ERROR_COUNT, processParameters);
    }

    public void saveActualPayment(List<Long> ids, Map processParameters) {
        execute(SAVE_ACTUAL_PAYMENT, ActualPaymentSaveTaskBean.class, getActualPaymentFiles(ids), null, SAVE_THREAD_SIZE,
                SAVE_MAX_ERROR_COUNT, processParameters);
    }

    /*Subsidy*/

    public void loadSubsidy(Long userOrganizationId, Long organizationId, int year, int monthFrom, int monthTo) {
        try {
            List<RequestFile> list = LoadUtil.getSubsidies(userOrganizationId, organizationId, monthFrom, monthTo, year);

            for (RequestFile file : list) {
                file.setUserOrganizationId(userOrganizationId);
            }

            execute(LOAD_SUBSIDY, SubsidyLoadTaskBean.class, list, null, LOAD_THREAD_SIZE, LOAD_MAX_ERROR_COUNT, null);
        } catch (Exception e) {
            log.error("Ошибка процесса загрузки файлов.", e);
            logBean.error(Module.NAME, ProcessManagerBean.class, RequestFile.class, null,
                    Log.EVENT.CREATE, "Ошибка процесса загрузки файлов. Причина: {0}", e.getMessage());

            broadcastService.broadcast(getClass(), "error", e);
        }
    }

    public void bindSubsidy(List<Long> ids, Map processParameters) {
        execute(BIND_SUBSIDY, SubsidyBindTaskBean.class, getSubsidyFiles(ids), null, BIND_THREAD_SIZE,
                BIND_MAX_ERROR_COUNT, processParameters);
    }

    public void fillSubsidy(List<Long> ids, Map processParameters) {
        execute(FILL_SUBSIDY, SubsidyFillTaskBean.class, getSubsidyFiles(ids), null, FILL_THREAD_SIZE,
                FILL_MAX_ERROR_COUNT, processParameters);
    }

    public void saveSubsidy(List<Long> ids, Map processParameters) {
        execute(SAVE_SUBSIDY, SubsidySaveTaskBean.class, getSubsidyFiles(ids), null, SAVE_THREAD_SIZE,
                SAVE_MAX_ERROR_COUNT, processParameters);
    }

    public void exportSubsidyMasterData(List<Long> ids, ExportType type, RequestFileType requestFileType, Date date) {
        List<SubsidyMasterDataFile> list = subsidyBean.getSubsidyMasterDataFiles(ids, type, date);

        //set type
        for (SubsidyMasterDataFile f : list){
            f.setType(requestFileType);
        }

        execute(EXPORT_SUBSIDY_MASTER_DATA, SubsidyMasterDataExportTaskBean.class, list, null, SAVE_THREAD_SIZE, SAVE_MAX_ERROR_COUNT, null);
    }

    public void exportSubsidy(List<Long> ids) {
        execute(EXPORT_SUBSIDY, SubsidyExportTaskBean.class,
                getRequestFiles(ids, EXPORT_SUBSIDY), null, SAVE_THREAD_SIZE, SAVE_MAX_ERROR_COUNT,
                null);
    }

    /*SubsidyTarif*/

    public void loadSubsidyTarif(Long userOrganizationId, Long organizationId, int year, int month) {
        try {
            List<RequestFile> list = LoadUtil.getSubsidyTarifs(userOrganizationId, organizationId, month, year);

            for (RequestFile file : list) {
                file.setUserOrganizationId(userOrganizationId);
            }

            execute(LOAD_SUBSIDY_TARIF, SubsidyTarifLoadTaskBean.class, list, null, LOAD_THREAD_SIZE, LOAD_MAX_ERROR_COUNT, null);
        } catch (Exception e) {
            log.error("Ошибка процесса загрузки файлов.", e);
            logBean.error(Module.NAME, ProcessManagerBean.class, RequestFile.class, null,
                    Log.EVENT.CREATE, "Ошибка процесса загрузки файлов. Причина: {0}", e.getMessage());

            broadcastService.broadcast(getClass(), "error", e);
        }
    }

    /*DwellingCharacteristics*/

    public void loadDwellingCharacteristics(Long userOrganizationId, Long osznId, int year, int month) {
        try {
            List<RequestFile> list = LoadUtil.getDwellingCharacteristics(userOrganizationId, osznId, month, year);

            for (RequestFile file : list) {
                file.setUserOrganizationId(userOrganizationId);
            }

            execute(LOAD_DWELLING_CHARACTERISTICS, DwellingCharacteristicsLoadTaskBean.class, list, null,
                    LOAD_THREAD_SIZE, LOAD_MAX_ERROR_COUNT, null);
        } catch (Exception e) {
            log.error("Ошибка процесса загрузки файлов.", e);
            logBean.error(Module.NAME, ProcessManagerBean.class, RequestFile.class, null,
                    Log.EVENT.CREATE, "Ошибка процесса загрузки файлов. Причина: {0}", e.getMessage());

            broadcastService.broadcast(getClass(), "error", e);
        }
    }

    public void bindDwellingCharacteristics(List<Long> ids, Map processParameters) {
        execute(BIND_DWELLING_CHARACTERISTICS, DwellingCharacteristicsBindTaskBean.class, getDwellingCharacteristicsFiles(ids),
                null, BIND_THREAD_SIZE, BIND_MAX_ERROR_COUNT, processParameters);
    }

    public void fillDwellingCharacteristics(List<Long> ids, Map processParameters) {
        execute(FILL_DWELLING_CHARACTERISTICS, DwellingCharacteristicsFillTaskBean.class, getDwellingCharacteristicsFiles(ids),
                null, BIND_THREAD_SIZE, BIND_MAX_ERROR_COUNT, processParameters);
    }

    public void saveDwellingCharacteristics(List<Long> ids, Map processParameters) {
        execute(SAVE_DWELLING_CHARACTERISTICS, DwellingCharacteristicsSaveTaskBean.class,
                getDwellingCharacteristicsFiles(ids), null, SAVE_THREAD_SIZE, SAVE_MAX_ERROR_COUNT, processParameters);
    }

    /*FacilityServiceType*/

    public void loadFacilityServiceType(long userOrganizationId, long osznId, int year, int month) {
        try {
            List<RequestFile> list = LoadUtil.getFacilityServiceTypes(userOrganizationId, osznId, month, year);

            for (RequestFile file : list) {
                file.setUserOrganizationId(userOrganizationId);
            }

            execute(LOAD_FACILITY_SERVICE_TYPE, FacilityServiceTypeLoadTaskBean.class, list, null,
                    LOAD_THREAD_SIZE, LOAD_MAX_ERROR_COUNT, null);
        } catch (Exception e) {
            log.error("Ошибка процесса загрузки файлов.", e);
            logBean.error(Module.NAME, ProcessManagerBean.class, RequestFile.class, null,
                    Log.EVENT.CREATE, "Ошибка процесса загрузки файлов. Причина: {0}", e.getMessage());

            broadcastService.broadcast(getClass(), "error", e);
        }
    }

    public void bindFacilityServiceType(List<Long> ids, Map processParameters) {
        execute(BIND_FACILITY_SERVICE_TYPE, FacilityServiceTypeBindTaskBean.class, getFacilityServiceTypeFiles(ids),
                null, BIND_THREAD_SIZE, BIND_MAX_ERROR_COUNT, processParameters);
    }

    public void fillFacilityServiceType(List<Long> ids, Map processParameters) {
        execute(FILL_FACILITY_SERVICE_TYPE, FacilityServiceTypeFillTaskBean.class, getFacilityServiceTypeFiles(ids),
                null, FILL_THREAD_SIZE, FILL_MAX_ERROR_COUNT, processParameters);
    }

    public void saveFacilityServiceType(List<Long> ids, Map processParameters) {
        execute(SAVE_FACILITY_SERVICE_TYPE, FacilityServiceTypeSaveTaskBean.class,
                getFacilityServiceTypeFiles(ids), null, SAVE_THREAD_SIZE, SAVE_MAX_ERROR_COUNT, processParameters);
    }

    /*FacilityForm2*/

    public void loadFacilityForm2(Long serviceProviderId, Long userOrganizationId, Long organizationId, int year, int month){
        RequestFile requestFile = new RequestFile();
        requestFile.setUserOrganizationId(userOrganizationId);
        requestFile.setOrganizationId(organizationId);
        requestFile.setServiceProviderId(serviceProviderId);
        requestFile.setBeginDate(newDate(year, month));
        requestFile.setType(RequestFileType.FACILITY_FORM2);

        execute(LOAD_FACILITY_FORM2, FacilityForm2LoadTaskBean.class, Collections.singletonList(requestFile), null,
                LOAD_THREAD_SIZE, LOAD_MAX_ERROR_COUNT, null);
    }

    public void saveFacilityForm2(List<Long> ids, Map processParameters) {
        execute(SAVE_FACILITY_FORM2, FacilityForm2SaveTaskBean.class,
                getFacilityForm2Files(ids), null, SAVE_THREAD_SIZE, SAVE_MAX_ERROR_COUNT, processParameters);
    }

    /*FacilityLocal*/

    public void loadFacilityLocal(Long serviceProviderId, Long userOrganizationId, Long organizationId, int year, int month){
        RequestFile requestFile = new RequestFile();

        requestFile.setServiceProviderId(serviceProviderId);
        requestFile.setUserOrganizationId(userOrganizationId);
        requestFile.setOrganizationId(organizationId);
        requestFile.setBeginDate(newDate(year, month));
        requestFile.setType(RequestFileType.FACILITY_LOCAL);

        execute(LOAD_FACILITY_LOCAL, FacilityLocalLoadTaskBean.class, Collections.singletonList(requestFile), null,
                LOAD_THREAD_SIZE, LOAD_MAX_ERROR_COUNT, null);

    }

    public void saveFacilityLocal(List<Long> ids, Map processParameters) {
        execute(SAVE_FACILITY_LOCAL, FacilityLocalSaveTaskBean.class,
                getFacilityLocalFiles(ids), null, SAVE_THREAD_SIZE, SAVE_MAX_ERROR_COUNT, processParameters);
    }

    public void loadFacilityJanitorLocal(Long serviceProviderId, Long userOrganizationId, Long organizationId, int year, int month){
        RequestFile requestFile = new RequestFile();

        requestFile.setServiceProviderId(serviceProviderId);
        requestFile.setUserOrganizationId(userOrganizationId);
        requestFile.setOrganizationId(organizationId);
        requestFile.setBeginDate(newDate(year, month));
        requestFile.setType(RequestFileType.FACILITY_LOCAL);
        requestFile.setSubType(RequestFileSubType.FACILITY_LOCAL_JANITOR);

        execute(LOAD_FACILITY_LOCAL, FacilityLocalJanitorLoadTaskBean.class, Collections.singletonList(requestFile), null,
                LOAD_THREAD_SIZE, LOAD_MAX_ERROR_COUNT, null);
    }

    public void loadFacilityCompensationLocal(Long serviceProviderId, Long userOrganizationId, Long organizationId, int year, int month){
        RequestFile requestFile = new RequestFile();

        requestFile.setServiceProviderId(serviceProviderId);
        requestFile.setUserOrganizationId(userOrganizationId);
        requestFile.setOrganizationId(organizationId);
        requestFile.setBeginDate(newDate(year, month));
        requestFile.setType(RequestFileType.FACILITY_LOCAL);
        requestFile.setSubType(RequestFileSubType.FACILITY_LOCAL_COMPENSATION);

        execute(LOAD_FACILITY_LOCAL, FacilityLocalCompensationLoadTaskBean.class, Collections.singletonList(requestFile), null,
                LOAD_THREAD_SIZE, LOAD_MAX_ERROR_COUNT, null);
    }

    /*FacilityTarifReferences*/

    public void loadFacilityStreetTypeReferences(Long userOrganizationId, Long organizationId, int year, int month) {
        try {
            List<RequestFile> list = LoadUtil.getFacilityStreetTypeReferences(userOrganizationId, organizationId, month, year);

            for (RequestFile file : list) {
                file.setUserOrganizationId(userOrganizationId);
            }

            execute(LOAD_FACILITY_STREET_TYPE_REFERENCE, FacilityStreetTypeLoadTaskBean.class, list, null,
                    LOAD_THREAD_SIZE, LOAD_MAX_ERROR_COUNT, null);
        } catch (StorageNotFoundException e) {
            log.error("Ошибка процесса загрузки файлов.", e);
            logBean.error(Module.NAME, ProcessManagerBean.class, RequestFile.class, null,
                    Log.EVENT.CREATE, "Ошибка процесса загрузки файлов. Причина: {0}", e.getMessage());
        }
    }

    public void loadFacilityStreetReferences(Long userOrganizationId, Long organizationId, int year, int month,
                                             Locale locale) {
        try {
            List<RequestFile> list = LoadUtil.getFacilityStreetReferences(userOrganizationId, organizationId, month, year);

            for (RequestFile file : list) {
                file.setUserOrganizationId(userOrganizationId);
            }

            execute(LOAD_FACILITY_STREET_REFERENCE, FacilityStreetLoadTaskBean.class, list, null,
                    LOAD_THREAD_SIZE, LOAD_MAX_ERROR_COUNT,
                    ImmutableMap.of(FacilityStreetLoadTaskBean.LOCALE_TASK_PARAMETER_KEY, locale));
        } catch (StorageNotFoundException e) {
            log.error("Ошибка процесса загрузки файлов.", e);
            logBean.error(Module.NAME, ProcessManagerBean.class, RequestFile.class, null,
                    Log.EVENT.CREATE, "Ошибка процесса загрузки файлов. Причина: {0}", e.getMessage());
        }
    }

    public void loadFacilityTarifReferences(Long userOrganizationId, Long organizationId, int year, int month,
                                            Locale locale) {
        try {
            List<RequestFile> list = LoadUtil.getFacilityTarifReferences(userOrganizationId, organizationId, month, year);

            for (RequestFile file : list) {
                file.setUserOrganizationId(userOrganizationId);
            }

            execute(LOAD_FACILITY_TARIF_REFERENCE, FacilityTarifLoadTaskBean.class, list, null,
                    LOAD_THREAD_SIZE, LOAD_MAX_ERROR_COUNT, null);
        } catch (StorageNotFoundException e) {
            log.error("Ошибка процесса загрузки файлов.", e);
            logBean.error(Module.NAME, ProcessManagerBean.class, RequestFile.class, null,
                    Log.EVENT.CREATE, "Ошибка процесса загрузки файлов. Причина: {0}", e.getMessage());
        }
    }

    /*PrivilegeGroup*/

    public void loadPrivilegeGroup(Long userOrganizationId, Long organizationId, int year, int month) {
        Process<PrivilegeFileGroup> process = getProcess(LOAD_PRIVILEGE_GROUP);

        try {
            //поиск групп файлов запросов
            if (!process.isRunning()) {
                process.setPreprocess(true);
                process.init();
            }

            LoadGroupParameter<PrivilegeFileGroup> loadParameter = LoadUtil.getLoadPrivilegeGroupParameter(userOrganizationId, organizationId, month, year);

            List<RequestFile> linkError = loadParameter.getLinkError();

            process.addLinkError(linkError);

            for (RequestFile rf : linkError) {
                logBean.error(Module.NAME, ProcessManagerBean.class, RequestFileGroup.class, null, rf.getId(),
                        Log.EVENT.CREATE, rf.getLogChangeList(), "Связанный файл не найден для объекта {0}",
                        rf.getLogObjectName());
            }

            process.addObjects(loadParameter.getRequestFileGroups());

            //загрузка данных
            if (!process.isRunning()) {
                process.setPreprocess(false);
                process.setMaxErrors(configBean.getInteger(LOAD_MAX_ERROR_COUNT, true));
                process.setMaxThread(configBean.getInteger(LOAD_THREAD_SIZE, true));
                process.setTaskClass(PrivilegeGroupLoadTaskBean.class);

                executorService.execute(process);
            } else {
                int freeThreadCount = process.getMaxThread() - process.getRunningThreadCount();

                for (int i = 0; i < freeThreadCount; ++i) {
                    executorService.executeNextAsync(process);
                }
            }
        } catch (Exception e) {
            process.preprocessError();

            broadcastService.broadcast(getClass(), "onError", e);

            log.error("Ошибка процесса загрузки файлов.", e);
            logBean.error(Module.NAME, ProcessManagerBean.class, RequestFileGroup.class, null,
                    Log.EVENT.CREATE, "Ошибка процесса загрузки файлов. Причина: {0}", e.getMessage());
        }
    }

    public void bindPrivilegeGroup(List<Long> ids, Map processParameters) {
        execute(BIND_PRIVILEGE_GROUP, PrivilegeGroupBindTaskBean.class, getPrivilegeFileGroups(ids), null, BIND_THREAD_SIZE, BIND_MAX_ERROR_COUNT, processParameters);
    }

    public void fillPrivilegeGroup(List<Long> ids, Map processParameters) {
        execute(FILL_PRIVILEGE_GROUP, PrivilegeGroupFillTaskBean.class, getPrivilegeFileGroups(ids), null, FILL_THREAD_SIZE, FILL_MAX_ERROR_COUNT, processParameters);
    }

    public void savePrivilegeGroup(List<Long> ids, Map processParameters) {
        execute(SAVE_PRIVILEGE_GROUP, PrivilegeGroupSaveTaskBean.class, getPrivilegeFileGroups(ids), null, SAVE_THREAD_SIZE, SAVE_MAX_ERROR_COUNT, processParameters);
    }

    /*PrivilegeProlongation*/

    public void loadPrivilegeProlongation(RequestFileSubType subType, Long userOrganizationId, Long organizationId, int year, int month) {
        try {
            List<RequestFile> list = LoadUtil.getPrivilegeProlongation(subType, userOrganizationId, organizationId, month, year);

            execute(LOAD_PRIVILEGE_PROLONGATION, PrivilegeProlongationLoadTaskBean.class, list, null, LOAD_THREAD_SIZE,
                    LOAD_MAX_ERROR_COUNT, null);
        } catch (Exception e) {
            log.error("Ошибка процесса загрузки файлов.", e);
            logBean.error(Module.NAME, ProcessManagerBean.class, RequestFile.class, null,
                    Log.EVENT.CREATE, "Ошибка процесса загрузки файлов. Причина: {0}", e.getMessage());

            broadcastService.broadcast(getClass(), "error", e);
        }
    }

    public void bindPrivilegeProlongation(List<Long> ids, Map processParameters) {
        execute(BIND_PRIVILEGE_PROLONGATION, PrivilegeProlongationBindTaskBean.class,
                getRequestFiles(ids, BIND_PRIVILEGE_PROLONGATION), null, BIND_THREAD_SIZE, BIND_MAX_ERROR_COUNT,
                processParameters);
    }

    public void exportPrivilegeProlongation(List<Long> ids, Map processParameters) {
        execute(EXPORT_PRIVILEGE_PROLONGATION, PrivilegeProlongationSaveTaskBean.class,
                getRequestFiles(ids, EXPORT_PRIVILEGE_PROLONGATION), null, SAVE_THREAD_SIZE, SAVE_MAX_ERROR_COUNT,
                processParameters);
    }
}