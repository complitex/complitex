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
import org.complitex.osznconnection.file.entity.privilege.PrivilegeFileGroup;
import org.complitex.osznconnection.file.entity.subsidy.RequestFileGroup;
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
import static org.complitex.osznconnection.file.entity.RequestFileStatus.*;
import static org.complitex.osznconnection.file.service.process.ProcessType.*;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 01.11.10 12:55
 */
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
@TransactionManagement(TransactionManagementType.BEAN)
public class ProcessManagerService {
    private final Logger log = LoggerFactory.getLogger(ProcessManagerService.class);

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
        if (processType == null){
            return false;
        }

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

    public boolean isGlobalProcessing(ProcessType processType) {
        for (Process process : getAllUsersProcess(processType)) {
            if (process.isProcessing()) {
                return true;
            }
        }

        return false;
    }

    private <T extends IExecutorObject> void execute(ProcessType processType, Class<? extends ITaskBean<T>> taskClass,
            List<T> list, IExecutorListener<T> listener,
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

    private List<RequestFileGroup> updateAndGetRequestFileGroups(RequestFileStatus status, List<Long> ids) {
        List<RequestFileGroup> groups = new ArrayList<>();

        for (Long id : ids) {
            RequestFileGroup group = requestFileGroupBean.getRequestFileGroup(id);

            if (!group.isWaiting() && !group.isProcessing()) {
                group.setStatus(status);

                requestFileGroupBean.save(group);

                groups.add(group);
            }
        }

        return groups;
    }

    private List<RequestFile> updateAndGetRequestFiles(RequestFileStatus status, List<Long> ids, RequestFileStatus... forStatuses) {
        List<RequestFile> requestFiles = new ArrayList<>();

        List<RequestFileStatus> statuses = Arrays.asList(forStatuses);

        for (Long id : ids) {
            RequestFile requestFile = requestFileBean.getRequestFile(id);

            if (!requestFile.isWaiting() && !requestFile.isProcessing()) {
                if (statuses.isEmpty() || statuses.contains(requestFile.getStatus())) {
                    requestFile.setStatus(status);

                    requestFileBean.save(requestFile);

                    requestFiles.add(requestFile);
                }
            }
        }

        return requestFiles;
    }

    private List<PrivilegeFileGroup> updateAndGetPrivilegeFileGroups(RequestFileStatus status, List<Long> ids) {
        List<PrivilegeFileGroup> groups = new ArrayList<>();

        for (Long id : ids) {
            PrivilegeFileGroup group = privilegeFileGroupBean.getPrivilegeFileGroup(id);

            if (!group.isWaiting() && !group.isProcessing()) {
                group.setStatus(status);

                if (group.getDwellingCharacteristicsRequestFile() != null){
                    group.getDwellingCharacteristicsRequestFile().setStatus(status);

                    requestFileBean.save(group.getDwellingCharacteristicsRequestFile());
                }

                if (group.getFacilityServiceTypeRequestFile() != null){
                    group.getFacilityServiceTypeRequestFile().setStatus(status);

                    requestFileBean.save(group.getFacilityServiceTypeRequestFile());
                }

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
                logBean.error(Module.NAME, ProcessManagerService.class, RequestFileGroup.class, null, rf.getId(),
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
            logBean.error(Module.NAME, ProcessManagerService.class, RequestFileGroup.class, null,
                    Log.EVENT.CREATE, "Ошибка процесса загрузки файлов. Причина: {0}", e.getMessage());
        }
    }


    private IExecutorListener<RequestFileGroup> groupListener = new IExecutorListener<RequestFileGroup>() {
        @Override
        public void onComplete(List<RequestFileGroup> processed) {
        }

        @Override
        public void onError(List<RequestFileGroup> unprocessed) {
            unprocessed.forEach(group -> {
                requestFileGroupBean.fixProcessingOnError(group);
            });
        }
    };

    public void bindGroup(List<Long> ids, Map processParameters) {
        execute(BIND_GROUP, GroupBindTaskBean.class, updateAndGetRequestFileGroups(BIND_WAIT, ids),
                groupListener, BIND_THREAD_SIZE, BIND_MAX_ERROR_COUNT, processParameters);
    }

    public void fillGroup(List<Long> ids, Map processParameters) {
        execute(FILL_GROUP, GroupFillTaskBean.class, updateAndGetRequestFileGroups(FILL_WAIT, ids),
                groupListener, FILL_THREAD_SIZE, FILL_MAX_ERROR_COUNT, processParameters);
    }

    public void saveGroup(List<Long> ids, Map processParameters) {
        IExecutorListener<RequestFileGroup> listener = new IExecutorListener<RequestFileGroup>() {

            @Override
            public void onComplete(List<RequestFileGroup> processed) {
                try {
                    SaveUtil.createResult(processed, reportWarningRenderer);
                } catch (StorageNotFoundException e) {
                    log.error("Ошибка создания файла Result.txt.", e);
                    logBean.error(Module.NAME, ProcessManagerService.class, RequestFileGroup.class, null,
                            Log.EVENT.CREATE, "Ошибка создания файла Result.txt. Причина: {0}", e.getMessage());
                }
            }

            @Override
            public void onError(List<RequestFileGroup> unprocessed) {
                unprocessed.forEach(g ->{
                    g.setStatus(SAVE_ERROR);
                    requestFileGroupBean.save(g);
                });

            }
        };

        execute(SAVE_GROUP, GroupSaveTaskBean.class, updateAndGetRequestFileGroups(SAVE_WAIT, ids),
                listener, SAVE_THREAD_SIZE, SAVE_MAX_ERROR_COUNT, processParameters);
    }

    private IExecutorListener<RequestFile> requestFileListener = new IExecutorListener<RequestFile>() {
        @Override
        public void onComplete(List<RequestFile> processed) {
        }

        @Override
        public void onError(List<RequestFile> unprocessed) {
            unprocessed.forEach(requestFile -> {
                requestFileBean.fixProcessingOnError(requestFile);
            });
        }
    };

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
            logBean.error(Module.NAME, ProcessManagerService.class, RequestFile.class, null,
                    Log.EVENT.CREATE, "Ошибка процесса загрузки файлов. Причина: {0}", e.getMessage());

            broadcastService.broadcast(getClass(), "onError", e);
        }
    }

    public void bindActualPayment(List<Long> ids, Map processParameters) {
        execute(BIND_ACTUAL_PAYMENT, ActualPaymentBindTaskBean.class, updateAndGetRequestFiles(BIND_WAIT, ids),
                requestFileListener, BIND_THREAD_SIZE, BIND_MAX_ERROR_COUNT, processParameters);
    }

    public void fillActualPayment(List<Long> ids, Map processParameters) {
        execute(FILL_ACTUAL_PAYMENT, ActualPaymentFillTaskBean.class, updateAndGetRequestFiles(FILL_WAIT, ids),
                requestFileListener, FILL_THREAD_SIZE, FILL_MAX_ERROR_COUNT, processParameters);
    }

    public void saveActualPayment(List<Long> ids, Map processParameters) {
        execute(SAVE_ACTUAL_PAYMENT, ActualPaymentSaveTaskBean.class, updateAndGetRequestFiles(SAVE_WAIT, ids),
                requestFileListener, SAVE_THREAD_SIZE, SAVE_MAX_ERROR_COUNT, processParameters);
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
            logBean.error(Module.NAME, ProcessManagerService.class, RequestFile.class, null,
                    Log.EVENT.CREATE, "Ошибка процесса загрузки файлов. Причина: {0}", e.getMessage());

            broadcastService.broadcast(getClass(), "onError", e);
        }
    }

    public void bindSubsidy(List<Long> ids, Map processParameters) {
        execute(BIND_SUBSIDY, SubsidyBindTaskBean.class, updateAndGetRequestFiles(BIND_WAIT, ids),
                requestFileListener, BIND_THREAD_SIZE, BIND_MAX_ERROR_COUNT, processParameters);
    }

    public void fillSubsidy(List<Long> ids, Map processParameters) {
        execute(FILL_SUBSIDY, SubsidyFillTaskBean.class, updateAndGetRequestFiles(FILL_WAIT, ids),
                requestFileListener, FILL_THREAD_SIZE, FILL_MAX_ERROR_COUNT, processParameters);
    }

    public void saveSubsidy(List<Long> ids, Map processParameters) {
        execute(SAVE_SUBSIDY, SubsidySaveTaskBean.class, updateAndGetRequestFiles(SAVE_WAIT, ids),
                requestFileListener, SAVE_THREAD_SIZE, SAVE_MAX_ERROR_COUNT, processParameters);
    }

    public void exportSubsidy(List<Long> ids) {
        execute(EXPORT_SUBSIDY, SubsidyExportTaskBean.class, updateAndGetRequestFiles(EXPORT_WAIT, ids, FILLED, FILL_ERROR),
                requestFileListener, SAVE_THREAD_SIZE, SAVE_MAX_ERROR_COUNT, null);
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
            logBean.error(Module.NAME, ProcessManagerService.class, RequestFile.class, null,
                    Log.EVENT.CREATE, "Ошибка процесса загрузки файлов. Причина: {0}", e.getMessage());

            broadcastService.broadcast(getClass(), "onError", e);
        }
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
        execute(SAVE_FACILITY_FORM2, FacilityForm2SaveTaskBean.class, updateAndGetRequestFiles(SAVE_WAIT, ids),
                requestFileListener, SAVE_THREAD_SIZE, SAVE_MAX_ERROR_COUNT, processParameters);
    }

    /*FacilityLocal*/

    public void loadFacilityLocal(Long serviceProviderId, Long userOrganizationId, Long organizationId, int year, int month){
        RequestFile requestFile = new RequestFile();

        requestFile.setServiceProviderId(serviceProviderId);
        requestFile.setUserOrganizationId(userOrganizationId);
        requestFile.setOrganizationId(organizationId);
        requestFile.setBeginDate(newDate(year, month));
        requestFile.setType(RequestFileType.FACILITY_LOCAL);

        execute(LOAD_FACILITY_LOCAL, FacilityLocalLoadTaskBean.class, Collections.singletonList(requestFile),
                null, LOAD_THREAD_SIZE, LOAD_MAX_ERROR_COUNT, null);

    }

    public void saveFacilityLocal(List<Long> ids, Map processParameters) {
        execute(SAVE_FACILITY_LOCAL, FacilityLocalSaveTaskBean.class, updateAndGetRequestFiles(SAVE_WAIT, ids),
                requestFileListener, SAVE_THREAD_SIZE, SAVE_MAX_ERROR_COUNT, processParameters);
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
            logBean.error(Module.NAME, ProcessManagerService.class, RequestFile.class, null,
                    Log.EVENT.CREATE, "Ошибка процесса загрузки файлов. Причина: {0}", e.getMessage());

            broadcastService.broadcast(getClass(), "onError", e);
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
            logBean.error(Module.NAME, ProcessManagerService.class, RequestFile.class, null,
                    Log.EVENT.CREATE, "Ошибка процесса загрузки файлов. Причина: {0}", e.getMessage());

            broadcastService.broadcast(getClass(), "onError", e);
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
            logBean.error(Module.NAME, ProcessManagerService.class, RequestFile.class, null,
                    Log.EVENT.CREATE, "Ошибка процесса загрузки файлов. Причина: {0}", e.getMessage());

            broadcastService.broadcast(getClass(), "onError", e);
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
                logBean.error(Module.NAME, ProcessManagerService.class, RequestFileGroup.class, null, rf.getId(),
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

            log.error("Ошибка процесса загрузки файлов.", e);
            logBean.error(Module.NAME, ProcessManagerService.class, RequestFileGroup.class, null,
                    Log.EVENT.CREATE, "Ошибка процесса загрузки файлов. Причина: {0}", e.getMessage());

            broadcastService.broadcast(getClass(), "onError", e);
        }
    }

    private IExecutorListener<PrivilegeFileGroup> privilegeFileGroupListener = new IExecutorListener<PrivilegeFileGroup>() {
        @Override
        public void onComplete(List<PrivilegeFileGroup> processed) {
        }

        @Override
        public void onError(List<PrivilegeFileGroup> unprocessed) {
            unprocessed.forEach(g -> {
                if (g.getDwellingCharacteristicsRequestFile() != null){
                    requestFileBean.fixProcessingOnError(g.getDwellingCharacteristicsRequestFile());
                }

                if (g.getFacilityServiceTypeRequestFile() != null){
                    requestFileBean.fixProcessingOnError(g.getFacilityServiceTypeRequestFile());
                }
            });
        }
    };

    public void bindPrivilegeGroup(List<Long> ids, Map processParameters) {
        execute(BIND_PRIVILEGE_GROUP, PrivilegeGroupBindTaskBean.class, updateAndGetPrivilegeFileGroups(BIND_WAIT, ids),
                privilegeFileGroupListener, BIND_THREAD_SIZE, BIND_MAX_ERROR_COUNT, processParameters);
    }

    public void fillPrivilegeGroup(List<Long> ids, Map processParameters) {
        execute(FILL_PRIVILEGE_GROUP, PrivilegeGroupFillTaskBean.class, updateAndGetPrivilegeFileGroups(FILL_WAIT, ids),
                privilegeFileGroupListener, FILL_THREAD_SIZE, FILL_MAX_ERROR_COUNT, processParameters);
    }

    public void savePrivilegeGroup(List<Long> ids, Map processParameters) {
        execute(SAVE_PRIVILEGE_GROUP, PrivilegeGroupSaveTaskBean.class, updateAndGetPrivilegeFileGroups(SAVE_WAIT, ids),
                privilegeFileGroupListener, SAVE_THREAD_SIZE, SAVE_MAX_ERROR_COUNT, processParameters);
    }

    /*PrivilegeProlongation*/

    public void loadPrivilegeProlongation(RequestFileSubType subType, Long userOrganizationId, Long organizationId, int year, int month) {
        try {
            List<RequestFile> list = LoadUtil.getPrivilegeProlongation(subType, userOrganizationId, organizationId, month, year);

            execute(LOAD_PRIVILEGE_PROLONGATION, PrivilegeProlongationLoadTaskBean.class, list, null, LOAD_THREAD_SIZE,
                    LOAD_MAX_ERROR_COUNT, null);
        } catch (Exception e) {
            log.error("Ошибка процесса загрузки файлов.", e);
            logBean.error(Module.NAME, ProcessManagerService.class, RequestFile.class, null,
                    Log.EVENT.CREATE, "Ошибка процесса загрузки файлов. Причина: {0}", e.getMessage());

            broadcastService.broadcast(getClass(), "onError", e);
        }
    }

    public void bindPrivilegeProlongation(List<Long> ids, Map processParameters) {
        execute(BIND_PRIVILEGE_PROLONGATION, PrivilegeProlongationBindTaskBean.class,
                updateAndGetRequestFiles(BIND_WAIT, ids),
                requestFileListener, BIND_THREAD_SIZE, BIND_MAX_ERROR_COUNT, processParameters);
    }

    public void exportPrivilegeProlongation(List<Long> ids, Map processParameters) {
        execute(EXPORT_PRIVILEGE_PROLONGATION, PrivilegeProlongationExportTaskBean.class,
                updateAndGetRequestFiles(EXPORT_WAIT, ids),
                requestFileListener, SAVE_THREAD_SIZE, SAVE_MAX_ERROR_COUNT, processParameters);
    }

    @SuppressWarnings("Duplicates")
    public void loadOschadbankRequest(Long serviceProviderId, Long userOrganizationId, Long organizationId, int year, int month){
        try {
            List<RequestFile> list = LoadUtil.getOschadbankRequests(userOrganizationId, organizationId, month, year);

            execute(LOAD_OSCHADBANK_REQUEST, OschadbankRequestLoadTaskBean.class, list, null, LOAD_THREAD_SIZE,
                    LOAD_MAX_ERROR_COUNT, null);
        } catch (Exception e) {
            log.error("Ошибка процесса загрузки файлов.", e);
            logBean.error(Module.NAME, ProcessManagerService.class, RequestFile.class, null,
                    Log.EVENT.CREATE, "Ошибка процесса загрузки файлов. Причина: {0}", e.getMessage());

            broadcastService.broadcast(getClass(), "onError", e);
        }
    }

    public void fillOschadbankRequest(List<Long> ids, Map processParameters) {
        execute(FILL_OSCHADBANK_REQUEST, OschadbankRequestFillTaskBean.class, updateAndGetRequestFiles(FILL_WAIT, ids),
                requestFileListener, FILL_THREAD_SIZE, FILL_MAX_ERROR_COUNT, processParameters);
    }

    public void saveOschadbankRequest(List<Long> ids, Map processParameters) {
        execute(SAVE_OSCHADBANK_REQUEST, OschadbankRequestSaveTaskBean.class, updateAndGetRequestFiles(SAVE_WAIT, ids),
                requestFileListener, SAVE_THREAD_SIZE, SAVE_MAX_ERROR_COUNT, processParameters);
    }
}