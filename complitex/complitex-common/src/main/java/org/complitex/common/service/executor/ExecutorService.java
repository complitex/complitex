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
import java.util.ArrayList;
import java.util.List;

import static org.complitex.common.service.executor.ExecutorCommand.STATUS.*;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 01.11.10 12:50
 */
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
@TransactionManagement(TransactionManagementType.BEAN)
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

                //listener
                unprocessed(executorCommand, null);

                broadcastService.broadcast(getClass(), "onCancel", executorCommand);

                log.warn("Процесс {} отменен пользователем", task.getControllerClass());
            }

            return;
        }

        if (executorCommand.getErrorCount() > executorCommand.getMaxErrors()){
            if (executorCommand.isDone()){
                //listener
                unprocessed(executorCommand, null);

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

                log.info("Задача {} завершена успешно {}", task, object);
            }else{
                executorCommand.incrementSkippedCount();

                broadcastService.broadcast(getClass(), "onSkip", object);

                log.info("Задача {} пропущена {}", task, object);
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
            executorCommand.setStatus(CRITICAL_ERROR);

            //listener
            unprocessed(executorCommand, object);

            //error
            executorCommand.clear();
            executorCommand.incrementErrorCount();
            executorCommand.setErrorMessage(ExceptionUtil.getCauseMessage(e));

            broadcastService.broadcast(getClass(), "onCriticalError", executorCommand);

            log.error("Критическая ошибка", e);
        }finally {
            executorCommand.getProcessed().add(object);
            executorCommand.stopTask();
            executeNext(executorCommand);
        }
    }

    private <T extends IExecutorObject> void unprocessed(ExecutorCommand<T> executorCommand, T object) {
        List<T> unprocessed = new ArrayList<>();

        if (object != null) {
            unprocessed.add(object);
        }

        T o;

        while ((o = executorCommand.pollObject()) != null){
            unprocessed.add(o);
        }

        if (executorCommand.getListener() != null){
            executorCommand.getListener().onError(unprocessed);
        }
    }

    public void execute(ExecutorCommand<? extends IExecutorObject> executorCommand){
        if (executorCommand.isRunning()){
            throw new IllegalStateException();
        }

        broadcastService.broadcast(getClass(), "onBegin", executorCommand);

        if (executorCommand.isEmpty()){
            executorCommand.setStatus(COMPLETED);

            broadcastService.broadcast(getClass(), "onComplete", executorCommand);

            return;
        }

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
