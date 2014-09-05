package ru.flexpay.eirc.registry.service.handle;

import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.complitex.common.entity.FilterWrapper;
import org.complitex.common.service.ConfigBean;
import org.complitex.common.service.executor.ExecuteException;
import org.complitex.common.util.EjbBeanLocator;
import org.complitex.correction.service.AddressService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.cal10n.LocLogger;
import ru.flexpay.eirc.dictionary.entity.EircConfig;
import ru.flexpay.eirc.registry.entity.*;
import ru.flexpay.eirc.registry.entity.log.GeneralProcessing;
import ru.flexpay.eirc.registry.entity.log.Handling;
import ru.flexpay.eirc.registry.entity.log.Rollback;
import ru.flexpay.eirc.registry.service.*;
import ru.flexpay.eirc.registry.service.handle.exchange.Operation;
import ru.flexpay.eirc.registry.service.handle.exchange.OperationFactory;
import ru.flexpay.eirc.registry.service.handle.exchange.OperationResult;
import ru.flexpay.eirc.service_provider_account.service.ServiceProviderAccountBean;

import javax.ejb.*;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author Pavel Sknar
 */
@Stateless
public class RegistryHandler {

    private static final Logger log = LoggerFactory.getLogger(RegistryHandler.class);

    @EJB
    private ConfigBean configBean;

    @EJB
    private JobProcessor processor;

    @EJB
    private HandleQueueProcessor handleQueueProcessor;

    @EJB
    private RegistryBean registryBean;

    @EJB
    private RegistryWorkflowManager registryWorkflowManager;

    @EJB
    private RegistryRecordBean registryRecordBean;

    @EJB
    private RegistryRecordWorkflowManager registryRecordWorkflowManager;

    @EJB
    private AddressService addressService;

    @EJB
    private ServiceProviderAccountBean serviceProviderAccountBean;

    @EJB
    private OperationFactory operationFactory;

    @EJB
    private CanceledProcessing canceledProcessing;

    private static final ReentrantReadWriteLock registryLock = new ReentrantReadWriteLock();

    public void handle(final Long registryId, final AbstractMessenger imessenger, final AbstractFinishCallback finishHandle) {
        handle(FilterWrapper.<RegistryRecordData>of(new RegistryRecord(registryId)), imessenger, finishHandle);
    }

    private void handle(final FilterWrapper<RegistryRecordData> filter, final AbstractMessenger imessenger,
                        final AbstractFinishCallback finishHandle) {
        final AtomicBoolean finishReadRecords = new AtomicBoolean(false);
        final AtomicInteger recordHandlingCounter = new AtomicInteger(0);

        final Long registryId = filter.getObject().getRegistryId();

        List<Registry> registries = registryBean.getRegistries(FilterWrapper.of(new Registry(registryId)));

        // check registry exist
        if (registries.size() == 0) {
            final LocLogger logger = getProcessLogger(registryId, imessenger);
            logger.info(GeneralProcessing.REGISTRY_NOT_FOUND);
            return;
        }

        final Registry registry = registries.get(0);

        final LocLogger logger = getProcessLogger(registry.getRegistryNumber(), imessenger);

        logger.info(Handling.STARTING_HANDLE_REGISTRIES);
        finishHandle.init(registry.getId());

        handleQueueProcessor.execute(
                new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        try {

                            // one process on handling
                            registryLock.writeLock().lock();
                            try {
                                // check registry status
                                if (!registryWorkflowManager.canProcess(registry)) {
                                    logger.error(Handling.REGISTRY_FAILED_STATUS);
                                    return null;
                                }

                                // change registry status
                                if (!EjbBeanLocator.getBean(RegistryHandler.class).setHandlingStatus(registry)) {
                                    logger.error(Handling.REGISTRY_STATUS_INNER_ERROR);
                                    return null;
                                }
                            } finally {
                                registryLock.writeLock().unlock();
                            }

                            // check registry records status
                            if (!registryRecordBean.hasRecordsToProcessing(registry)) {
                                logger.info(Handling.NOT_FOUND_HANDLING_REGISTRY_RECORDS);
                                EjbBeanLocator.getBean(RegistryHandler.class).setHandledStatus(registry);
                                return null;
                            }

                            try {

                                final BatchProcessor<JobResult> batchProcessor = new BatchProcessor<>(10, processor);

                                final Statistics<Handling> statistics = new Statistics<>(registry.getRegistryNumber(), imessenger, Handling.HANDLED_BULK_RECORDS);

                                int numberFlushRegistryRecords = configBean.getInteger(EircConfig.NUMBER_FLUSH_REGISTRY_RECORDS, true);
                                FilterWrapper<RegistryRecordData> innerFilter = FilterWrapper.of(filter.getObject(), 0, numberFlushRegistryRecords);
                                do {
                                    final List<RegistryRecordData> recordsToProcessing = registryRecordBean.getRecordsToProcessing(innerFilter);

                                    if (!isContinue(recordsToProcessing, registry)) {
                                        finishReadRecords.set(true);
                                    }

                                    if (recordsToProcessing.size() > 0) {

                                        recordHandlingCounter.incrementAndGet();

                                        batchProcessor.processJob(new Callable<JobResult>() {
                                                                      @Override
                                                                      public JobResult call() throws Exception {

                                                                          List<OperationResult> results = null;
                                                                          try {
                                                                              results = EjbBeanLocator.getBean(RegistryHandler.class).
                                                                                      handleRegistryRecords(registry, recordsToProcessing);

                                                                              return JobResult.SUCCESSFUL;
                                                                          } catch (Throwable th) {
                                                                              EjbBeanLocator.getBean(RegistryHandler.class).setErrorStatus(registry);
                                                                              String message = th.getLocalizedMessage();
                                                                              if (StringUtils.isEmpty(message) && th.getCause() != null) {
                                                                                  message = th.getCause().getLocalizedMessage();
                                                                              }
                                                                              logger.error(Handling.REGISTRY_FAILED_HANDLED, message);
                                                                              throw new ExecuteException(th, "Failed handle registry " + registryId);
                                                                          } finally {

                                                                              statistics.add(recordsToProcessing.size(), results == null ? 0 : results.size());

                                                                              if (recordHandlingCounter.decrementAndGet() == 0 && finishReadRecords.get()) {
                                                                                  finalizeRegistryHandled(finishHandle, registry, logger);
                                                                              }
                                                                          }
                                                                      }
                                                                  }
                                        );

                                        // next registry record`s id is last in this partition
                                        innerFilter.setFirst(recordsToProcessing.get(recordsToProcessing.size() - 1).getId().intValue() + 1);
                                    } else if (recordHandlingCounter.get() == 0) {
                                        finalizeRegistryHandled(finishHandle, registry, logger);
                                    }
                                } while (!finishReadRecords.get());

                            } catch (Throwable th) {

                                logger.error(Handling.REGISTRY_FAILED_HANDLED, th.getMessage());
                                log.error("Can not handle registry " + registryId, th);

                                EjbBeanLocator.getBean(RegistryHandler.class).setErrorStatus(registry);

                            }

                        } finally {
                            if (!finishReadRecords.get()) {
                                if (registryWorkflowManager.isProcessing(registry)) {
                                    try {
                                        setErrorStatus(registry);
                                    } catch (Throwable th) {
                                        log.error("Can not change status", th);
                                        logger.error(Handling.REGISTRY_STATUS_INNER_ERROR);
                                    }
                                    try {
                                        setHandledStatus(registry);
                                    } catch (Throwable th) {
                                        log.error("Can not change status", th);
                                        logger.error(Handling.REGISTRY_STATUS_INNER_ERROR);
                                    }
                                }
                                logger.info(Handling.REGISTRY_FINISH_HANDLE);
                                finishHandle.complete();
                            }
                        }
                        return null;
                    }
                }

        );
    }

    public void finalizeRegistryHandled(AbstractFinishCallback finishHandle, final Registry registry, final LocLogger logger) throws ExecuteException {
        finishHandle.complete();

        // Проставляем статус отмены
        if (canceledProcessing.isCancel(registry.getId(), new Runnable() {
            @Override
            public void run() {
                EjbBeanLocator.getBean(RegistryHandler.class).setCancelStatus(registry);
                logger.error(Handling.HANDLING_CANCELED);
            }
        })) {
            return;
        }

        logger.info(Handling.REGISTRY_FINISH_HANDLE);

        // если не было отмены, то статус завершения
        EjbBeanLocator.getBean(RegistryHandler.class).setHandledStatus(registry);
    }

    protected boolean isContinue(final List<RegistryRecordData> data, final Registry registry) throws ExecuteException {
        return data.size() != 0 && !canceledProcessing.isCanceling(registry.getId());
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<OperationResult> handleRegistryRecords(Registry registry, List<RegistryRecordData> registryRecords) throws TransitionNotAllowed {
        List<OperationResult> results = Lists.newArrayListWithCapacity(registryRecords.size());
        List<OperationResult> recordResults = Lists.newArrayList();
        for (RegistryRecordData registryRecord : registryRecords) {
            handelRegistryRecord(registry, results, recordResults, registryRecord);
            recordResults.clear();
        }
        registryRecordBean.updateBulk(registryRecords);
        return results;
    }

    @TransactionAttribute(TransactionAttributeType.MANDATORY)
    public void handelRegistryRecord(Registry registry,
                                     List<OperationResult> results,
                                     List<OperationResult> recordResults,
                                     RegistryRecordData registryRecord) throws TransitionNotAllowed {
        try {
            for (Container container : registryRecord.getContainers()) {
                Operation operation = operationFactory.getOperation(container);
                operation.process(registry, registryRecord, container, recordResults);
            }
            results.addAll(recordResults);
            registryRecordWorkflowManager.setNextSuccessStatus(registryRecord);
        } catch (Exception ex) {
            EjbBeanLocator.getBean(RegistryHandler.class).setErrorStatus(registryRecord, registry);
            throw new TransactionRolledbackLocalException("Error in registry record " + registryRecord.getId() +
                    "(account number - " + registryRecord.getPersonalAccountExt() + ")", ex);
        }
    }

    public void rollback(final Long registryId, final AbstractMessenger imessenger, final AbstractFinishCallback finishHandle) {
        rollbackRegistry(FilterWrapper.<RegistryRecordData>of(new RegistryRecord(registryId)), imessenger, finishHandle);
    }

    public void rollbackRegistry(final FilterWrapper<RegistryRecordData> filter, final AbstractMessenger imessenger,
                                 final AbstractFinishCallback finishRollback) {
        final AtomicBoolean finishReadRecords = new AtomicBoolean(false);
        final AtomicInteger recordRollbackCounter = new AtomicInteger(0);

        final Long registryId = filter.getObject().getRegistryId();

        List<Registry> registries = registryBean.getRegistries(FilterWrapper.of(new Registry(registryId)));

        // check registry exist
        if (registries.size() == 0) {
            final LocLogger logger = getProcessLogger(registryId, imessenger);
            logger.info(GeneralProcessing.REGISTRY_NOT_FOUND);
            return;
        }

        final Registry registry = registries.get(0);

        final LocLogger logger = getProcessLogger(registry.getRegistryNumber(), imessenger);

        logger.info(Rollback.STARTING_ROLLBACK_REGISTRIES);
        final RegistryStatus oldStatus = initRollbackStatus(registry, imessenger, finishRollback);
        if (oldStatus == null) {
            return;
        }

        handleQueueProcessor.execute(
                new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        final Statistics<Rollback> statistics = new Statistics<>(registry.getRegistryNumber(), imessenger, Rollback.ROLLBACK_BULK_RECORDS);
                        try {

                            // check registry records status
                            if (!registryRecordBean.hasRecordsToRollback(registry)) {
                                logger.info(Rollback.NOT_FOUND_ROLLBACK_REGISTRY_RECORDS);
                                EjbBeanLocator.getBean(RegistryHandler.class).setRolledBackStatus(registry, false, oldStatus);
                                return null;
                            }

                            try {

                                final BatchProcessor<JobResult> batchProcessor = new BatchProcessor<>(10, processor);

                                int numberFlushRegistryRecords = configBean.getInteger(EircConfig.NUMBER_FLUSH_REGISTRY_RECORDS, true);
                                FilterWrapper<RegistryRecordData> innerFilter = FilterWrapper.of(filter.getObject(), 0, numberFlushRegistryRecords);
                                do {
                                    final List<RegistryRecordData> recordsToRollback = registryRecordBean.getRecordsToRollback(innerFilter);

                                    if (!isContinue(recordsToRollback, registry)) {
                                        finishReadRecords.set(true);
                                    }

                                    if (recordsToRollback.size() > 0) {

                                        recordRollbackCounter.incrementAndGet();

                                        batchProcessor.processJob(
                                                new Callable<JobResult>() {
                                                    @Override
                                                    public JobResult call() throws Exception {

                                                        int rolledBack = 0;
                                                        try {
                                                            rolledBack = EjbBeanLocator.getBean(RegistryHandler.class).
                                                                    rollbackRegistryRecords(registry, recordsToRollback, imessenger, finishRollback);

                                                            return JobResult.SUCCESSFUL;
                                                        } catch (Throwable th) {
                                                            EjbBeanLocator.getBean(RegistryHandler.class).setErrorStatus(registry);
                                                            String message = th.getLocalizedMessage();
                                                            if (StringUtils.isEmpty(message) && th.getCause() != null) {
                                                                message = th.getCause().getLocalizedMessage();
                                                            }
                                                            logger.error(Handling.REGISTRY_FAILED_HANDLED, message);
                                                            throw new ExecuteException(th, "Failed handle registry " + registryId);
                                                        } finally {

                                                            statistics.add(recordsToRollback.size(), rolledBack);

                                                            if (recordRollbackCounter.decrementAndGet() == 0 && finishReadRecords.get()) {
                                                                finalizeRegistryRollback(registry, statistics.getTotalOperations() > 0,
                                                                        oldStatus, logger, finishRollback);
                                                            }
                                                        }
                                                    }
                                                }
                                        );

                                        // next registry record`s id is last in this partition
                                        innerFilter.setFirst(recordsToRollback.get(recordsToRollback.size() - 1).getId().intValue() + 1);
                                    } else if (recordRollbackCounter.get() == 0) {
                                        finalizeRegistryRollback(registry, statistics.getTotalOperations() > 0, oldStatus, logger, finishRollback);
                                    }
                                } while (!finishReadRecords.get());

                            } catch (Throwable th) {

                                logger.error(Rollback.REGISTRY_FAILED_ROLLBACK, th.getMessage());
                                log.error("Can not rollback registry " + registryId, th);

                            }

                        } finally {
                            if (!finishReadRecords.get()) {
                                finalizeRegistryRollback(registry, statistics.getTotalOperations() > 0, oldStatus, logger, finishRollback);
                            }
                        }
                        return null;
                    }
                }
        );
    }

    public void finalizeRegistryRollback(Registry registry, boolean rolledBack, RegistryStatus oldStatus,
                                         LocLogger logger, AbstractFinishCallback finishCallback) {
        if (oldStatus != null) {
            try {
                EjbBeanLocator.getBean(RegistryHandler.class).setRolledBackStatus(registry, rolledBack, oldStatus);
            } catch (Exception ex) {
                logger.error(Rollback.REGISTRY_FAILED_ROLLBACK, ex);
            }
            logger.info(Rollback.REGISTRY_FINISH_ROLLBACK);
            finishCallback.complete();
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public int rollbackRegistryRecords(final Registry registry, final List<RegistryRecordData> registryRecords, final AbstractMessenger imessenger,
                                       final AbstractFinishCallback finishCallback) throws Exception {
        final AtomicReference<RegistryStatus> oldStatusReference = new AtomicReference<>(null);
        if (!registryWorkflowManager.isRollbacking(registry)) {
            RegistryStatus oldStatus = initRollbackStatus(registry, imessenger, finishCallback);
            if (oldStatus == null) {
                return 0;
            }
            oldStatusReference.set(oldStatus);
        }
        final Callable<Integer> callable = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                int rolledBack = 0;
                final LocLogger logger = getProcessLogger(registry.getRegistryNumber(), imessenger);
                try {
                    for (RegistryRecordData registryRecord : registryRecords) {
                        rolledBack += rollbackRegistryRecord(registry, registryRecord) ? 1 : 0;
                    }
                    if (rolledBack > 0) {
                        EjbBeanLocator.getBean(RegistryHandler.class).update(registryRecords);
                    }
                } catch (Exception ex) {
                    logger.error(Rollback.REGISTRY_FAILED_ROLLBACK, ex);
                } finally {
                    finalizeRegistryRollback(registry, rolledBack > 0, oldStatusReference.get(), logger, finishCallback);
                }
                return rolledBack;
            }
        };
        if (oldStatusReference.get() != null) {
            handleQueueProcessor.execute(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    callable.call();
                    return null;
                }
            });
            return 0;
        }
        return callable.call();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void update(List<RegistryRecordData> registryRecords) {
        registryRecordBean.updateBulk(registryRecords);
    }

    private RegistryStatus initRollbackStatus(Registry registry, AbstractMessenger imessenger, AbstractFinishCallback finishHandle) {
        final LocLogger logger = getProcessLogger(registry.getRegistryNumber(), imessenger);
        registryLock.writeLock().lock();
        RegistryStatus oldStatus = registry.getStatus();
        try {
            // check registry status
            if (!registryWorkflowManager.canRollback(registry)) {
                logger.error(Rollback.REGISTRY_FAILED_STATUS);
                return null;
            }
            logger.info(Rollback.STARTING_ROLLBACK_REGISTRIES);
            finishHandle.init(registry.getId());

            // change registry status
            if (!EjbBeanLocator.getBean(RegistryHandler.class).setRollbackStatus(registry)) {
                logger.error(Rollback.REGISTRY_STATUS_INNER_ERROR);
                finishHandle.complete();
                return null;
            }
        } finally {
            registryLock.writeLock().unlock();
        }
        return oldStatus;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void setRolledBackStatus(Registry registry, boolean rollback, RegistryStatus oldStatus) throws TransitionNotAllowed {
        if (oldStatus != null && rollback) {
            boolean hasRecordsToRollback = registryRecordBean.hasRecordsToRollback(registry);
            if (RegistryStatus.PROCESSED_WITH_ERROR.equals(oldStatus)) {
                registryWorkflowManager.setNextStatus(registry, RegistryStatus.PROCESSED_WITH_ERROR);
            } else if (hasRecordsToRollback) {
                registryWorkflowManager.setNextStatus(registry, RegistryStatus.PROCESSED_PARTLY);
            } else {
                registryWorkflowManager.setNextStatus(registry, RegistryStatus.ROLLBACKED);
            }
        } else if (oldStatus != null) {
            // Restore old status
            registryWorkflowManager.setNextStatus(registry, oldStatus);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public boolean setRollbackStatus(Registry registry) {
        try {
            registryWorkflowManager.setNextStatus(registry, RegistryStatus.ROLLBACKING);
            return true;
        } catch (TransitionNotAllowed transitionNotAllowed) {
            log.error("Can not set rollbacking status. Current status: " + transitionNotAllowed.getType(), transitionNotAllowed);
        }
        return false;
    }

    public boolean rollbackRegistryRecord(Registry registry, RegistryRecordData registryRecord) throws TransitionNotAllowed {
        try {
            for (Container container : registryRecord.getContainers()) {
                Operation operation = operationFactory.getOperation(container);
                if (!operation.canRollback(null, container)) {
                    return false;
                }
            }
            for (Container container : registryRecord.getContainers()) {
                Operation operation = operationFactory.getOperation(container);
                operation.rollback(null, container);
            }
            registryRecordWorkflowManager.setNextSuccessStatus(registryRecord);
            return true;
        } catch (Exception ex) {
            //EjbBeanLocator.getBean(RegistryHandler.class).setErrorStatus(registryRecord, registry);
            throw new TransactionRolledbackLocalException("Error in registry record " + registryRecord.getId() +
                    "(account number - " + registryRecord.getPersonalAccountExt() + ")", ex);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public boolean setErrorStatus(RegistryRecordData registryRecord, Registry registry) {
        try {
            registryRecordWorkflowManager.setNextErrorStatus(registryRecord, registry);
            registryRecordBean.save(registryRecord);
            return true;
        } catch (TransitionNotAllowed transitionNotAllowed) {
            log.error("Can not set error status. Current status: " + transitionNotAllowed.getType(), transitionNotAllowed);
        }
        return false;
    }


    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public boolean setHandlingStatus(Registry registry) {
        try {
            registryWorkflowManager.setNextSuccessStatus(registry);
            return true;
        } catch (TransitionNotAllowed transitionNotAllowed) {
            log.error("Can not set handling status. Current status: " + transitionNotAllowed.getType(), transitionNotAllowed);
        }
        return false;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public boolean setHandledStatus(Registry registry) {
        try {
            registryWorkflowManager.setNextSuccessStatus(registry);
            return true;
        } catch (TransitionNotAllowed transitionNotAllowed) {
            log.error("Can set set handled status. Current status: " + transitionNotAllowed.getType(), transitionNotAllowed);
        } catch (Exception ex) {
            log.error("Can set set handled status", ex);
        }
        return false;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public boolean setErrorStatus(Registry registry) {
        try {
            registryWorkflowManager.markProcessingHasError(registry);
            return true;
        } catch (TransitionNotAllowed transitionNotAllowed) {
            log.error("Can not set error status. Current status: " + transitionNotAllowed.getType(), transitionNotAllowed);
        } catch (Exception ex) {
            log.error("Can not set error status", ex);
        }
        return false;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public boolean setCancelStatus(Registry registry) {
        try {
            registryWorkflowManager.markProcessingCanceled(registry);
            return true;
        } catch (TransitionNotAllowed transitionNotAllowed) {
            log.error("Can not set handled canceled status. Current status: " + transitionNotAllowed.getType(), transitionNotAllowed);
        }
        return false;
    }

    private LocLogger getProcessLogger(Long registryId, AbstractMessenger imessenger) {
        return RegistryLogger.getInstance(registryId, imessenger, RegistryHandler.class);
    }

    private class Statistics<T extends Enum> {
        private int totalHandledRecords = 0;
        private int totalOperations = 0;

        private Lock lock = new ReentrantLock();

        private LocLogger logger;

        private T message;

        private Statistics(Long registryNumber, AbstractMessenger imessenger, T message) {
            this.logger = getProcessLogger(registryNumber, imessenger);
            this.message = message;
        }

        public int getTotalHandledRecords() {
            return totalHandledRecords;
        }

        public int getTotalOperations() {
            return totalOperations;
        }

        public void add(int totalHandledRecords, int totalOperations) {
            lock.lock();
            try {
                this.totalHandledRecords += totalHandledRecords;
                this.totalOperations += totalOperations;
                logger.info(message, this.totalHandledRecords, this.totalOperations);
            } finally {
                lock.unlock();
            }
        }
    }

}
