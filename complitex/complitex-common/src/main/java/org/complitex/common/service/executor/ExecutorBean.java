package org.complitex.common.service.executor;

import org.complitex.common.entity.IExecutorObject;
import org.complitex.common.service.BroadcastService;
import org.complitex.common.service.LogBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.*;

import static org.complitex.common.service.executor.ExecutorCommand.STATUS.*;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 01.11.10 12:50
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class ExecutorBean {
    private final Logger log = LoggerFactory.getLogger(ExecutorBean.class);

    @EJB
    private BroadcastService broadcastService;

    @EJB
    private LogBean logBean;

    @Asynchronous
    public <T extends IExecutorObject> void executeNext(ExecutorCommand<T> executorCommand){
        T object = executorCommand.getQueue().poll();
        ITaskBean<T> task = executorCommand.getTask();

        //Все задачи выполнены
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

        //Отмена процесса
        if (executorCommand.isStop()){
            if (executorCommand.isRunning()) {
                executorCommand.setStatus(CANCELED);

                broadcastService.broadcast(getClass(), "onCancel", executorCommand);

                log.warn("Процесс {} отменен пользователем", task.getControllerClass());
            }

            return;
        }

        //Похоже что-то отломалось
        if (executorCommand.getErrorCount() > executorCommand.getMaxErrors()){
            if (executorCommand.isDone()){
                executorCommand.setStatus(CRITICAL_ERROR);

                log.error("Превышено количество ошибок в процессе {}", task.getControllerClass());
            }

            return;
        }

        //Выполняем задачу
        executorCommand.setObject(object);
        executorCommand.startTask();

        log.info("Выполнение процесса {} над объектом {}", task.getControllerClass().getSimpleName(), object);

        try {
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

            //next
            executeNext(executorCommand);
        } catch (Exception e){
            executorCommand.incrementErrorCount();

            executorCommand.setStatus(CRITICAL_ERROR);
            object.setErrorMessage(e.getMessage());

            broadcastService.broadcast(getClass(), "onError", object);

            log.error("Критическая ошибка", e);
        }finally {
            executorCommand.getProcessed().add(object);
            executorCommand.stopTask();
        }
    }

    public void execute(final ExecutorCommand executorCommand){
        if (executorCommand.isRunning()){
            throw new IllegalStateException();
        }

        if (executorCommand.getQueue().isEmpty()){
            executorCommand.setStatus(COMPLETED);

            broadcastService.broadcast(getClass(), "onComplete", executorCommand);

            return;
        }

        broadcastService.broadcast(getClass(), "onBegin", executorCommand);

        log.info("Начат процесс {}, количество объектов: {}",
                executorCommand.getTask().getControllerClass().getSimpleName(),
                executorCommand.getQueue().size());

        //execute threads
        executorCommand.setStatus(ExecutorCommand.STATUS.RUNNING);

        for (int i = 0; i < executorCommand.getMaxThread(); ++i){
            executeNext(executorCommand);
        }
    }
}
