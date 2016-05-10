package org.complitex.common.service.executor;

import org.complitex.common.entity.IExecutorObject;
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
    private LogBean logBean;

    @Asynchronous
    public <T extends IExecutorObject> void executeNext(ExecutorCommand<T> executorCommand){
        IExecutorObject object = executorCommand.getQueue().poll();
        IExecutorListener listener = executorCommand.getListener();
        ITaskBean task = executorCommand.getTask();

        //Все задачи выполнены
        if (object == null){
            if (executorCommand.isDone()){
                executorCommand.setStatus(COMPLETED);

                if (listener != null) {
                    listener.onComplete(executorCommand.getProcessed());
                }

                log.info("Процесс {} завершен", task.getControllerClass());
            }

            return;
        }

        //Отмена процесса
        if (executorCommand.isStop()){
            if (executorCommand.isRunning()) {
                executorCommand.setStatus(CANCELED);

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

                log.debug("Задача {} завершена успешно.", task);

            }else{
                executorCommand.incrementSkippedCount();

                log.debug("Задача {} пропущена.", task);
            }
        } catch (ExecuteException e) {
            object.setErrorMessage(e.getMessage());

            try {
                task.onError(object);
            } catch (Exception e1) {
                log.error("Критическая ошибка", e1);
            }

            if (e.isWarn()) {
                log.warn(e.getMessage());
            }else{
                log.error(e.getMessage(), e);
            }

            executorCommand.incrementErrorCount();

            //next
            executeNext(executorCommand);
        } catch (Exception e){
            object.setErrorMessage(e.getMessage());

            try {
                task.onError(object);
            } catch (Exception e1) {
                log.error("Критическая ошибка", e1);
            }

            log.error("Критическая ошибка", e);

            executorCommand.incrementErrorCount();
            executorCommand.setStatus(CRITICAL_ERROR);
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
            return;
        }

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
