package org.complitex.common.service.executor;

import org.complitex.common.entity.IExecutorObject;
import org.complitex.common.exception.ExecuteException;
import org.complitex.common.service.BroadcastService;
import org.complitex.common.service.LogBean;
import org.complitex.common.util.EjbBeanLocator;
import org.complitex.common.util.ExceptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.*;

import static org.complitex.common.service.executor.ExecutorCommand.STATUS.*;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 01.11.10 12:50
 */
@Singleton
@TransactionManagement(TransactionManagementType.BEAN)
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class ExecutorService {
    private final Logger log = LoggerFactory.getLogger(ExecutorService.class);

    @EJB
    private BroadcastService broadcastService;

    @EJB
    private LogBean logBean;

    @EJB
    private AsyncService asyncService;

    public <T extends IExecutorObject> void executeNextAsync(ExecutorCommand<T> executorCommand){
        asyncService.async(() -> executeNext(executorCommand));
    }

    public <T extends IExecutorObject> void executeNext(ExecutorCommand<T> executorCommand){
        ITaskBean<T> task = EjbBeanLocator.getBean(executorCommand.getTaskClass());

        if (executorCommand.isStop()){
            if (executorCommand.isRunning()) {
                executorCommand.setStatus(CANCELED);

                broadcastService.broadcast(getClass(), "onCancel", executorCommand);

                log.warn("Процесс {} отменен пользователем", task.getControllerClass());
            }

            return;
        }

        if (executorCommand.getErrorCount() > executorCommand.getMaxErrors()){
            if (executorCommand.isDone()){
                executorCommand.clear();
                executorCommand.setStatus(CRITICAL_ERROR);

                log.error("Превышено количество ошибок в процессе {}", task.getControllerClass());
            }

            return;
        }

        if (executorCommand.getStatus().equals(CRITICAL_ERROR)){
            return;
        }

        //object
        T object = executorCommand.pollObject();

        if (object == null){
            if (executorCommand.isDone()){
                executorCommand.setStatus(COMPLETED);

                if (executorCommand.getListener() != null) {
                    executorCommand.getListener().onComplete(executorCommand.getProcessed());
                }

                broadcastService.broadcast(getClass(), "onComplete", executorCommand);

                log.info("Процесс {} завершен", task.getControllerClass());
            }

            return;
        }

        log.info("Выполнение процесса {} над объектом {}", task.getControllerClass().getSimpleName(), object);

        //execute
        try {
            executorCommand.startTask();

            boolean noSkip = task.execute(object, executorCommand.getCommandParameters());

            if (noSkip) {
                executorCommand.incrementSuccessCount();

                broadcastService.broadcast(getClass(), "onSuccess", object);

                log.debug("Задача {} завершена успешно.", task);
            }else{
                executorCommand.incrementSkippedCount();

                broadcastService.broadcast(getClass(), "onSkip", object);

                log.debug("Задача {} пропущена.", task);
            }
        } catch (ExecuteException e) {
            executorCommand.incrementErrorCount();
            object.setErrorMessage(e.getMessage());
            broadcastService.broadcast(getClass(), "onError", object);

            if (e.isWarn()) {
                log.warn(e.getMessage());
            }else{
                log.error(e.getMessage(), e);
            }
        } catch (Exception e){
            executorCommand.clear();

            executorCommand.incrementErrorCount();
            executorCommand.setStatus(CRITICAL_ERROR);
            executorCommand.setErrorMessage(ExceptionUtil.getCauseMessage(e));

            broadcastService.broadcast(getClass(), "onCriticalError", executorCommand);

            log.error("Критическая ошибка", e);
        }finally {
            executorCommand.getProcessed().add(object);
            executorCommand.stopTask();
            executeNext(executorCommand);
        }
    }

    public void execute(final ExecutorCommand executorCommand){
        if (executorCommand.isRunning()){
            throw new IllegalStateException();
        }

        if (executorCommand.isEmpty()){
            executorCommand.setStatus(COMPLETED);

            broadcastService.broadcast(getClass(), "onComplete", executorCommand);

            return;
        }

        broadcastService.broadcast(getClass(), "onBegin", executorCommand);

        log.info("Начат процесс {}, количество объектов: {}",
                executorCommand.getTaskClass().getSimpleName(),
                executorCommand.getSize());

        executorCommand.setStatus(ExecutorCommand.STATUS.RUNNING);

        int size = Math.min(executorCommand.getMaxThread(), executorCommand.getSize());

        //execute threads
        for (int i = 0; i < size; ++i){
            asyncService.async(() -> executeNext(executorCommand));
        }
    }
}
