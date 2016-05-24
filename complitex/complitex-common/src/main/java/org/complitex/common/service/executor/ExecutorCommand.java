package org.complitex.common.service.executor;

import org.complitex.common.entity.IExecutorObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.complitex.common.service.executor.ExecutorCommand.STATUS.*;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *        Date: 24.01.11 15:18
 */
public class ExecutorCommand<T extends IExecutorObject> {
    public enum STATUS {
        NEW, RUNNING, COMPLETED, CRITICAL_ERROR, CANCELED
    }

    protected STATUS status = STATUS.NEW;

    private AtomicInteger successCount = new AtomicInteger(0);
    private AtomicInteger skippedCount = new AtomicInteger(0);
    private AtomicInteger errorCount = new AtomicInteger(0);
    protected AtomicBoolean stop = new AtomicBoolean(false);

    protected List<IExecutorObject> processed = new CopyOnWriteArrayList<>();

    private AtomicInteger runningThread = new AtomicInteger(0);

    private List<T> list = new ArrayList<>();
    private AtomicInteger index = new AtomicInteger(0);

    private Class<? extends ITaskBean<T>> taskClass;
    private IExecutorListener listener;
    
    // параметры управления ходом выполнения комманды
    protected Map commandParameters;

    private int maxErrors;
    private int maxThread;

    private String errorMessage;

    private int size;

    public void init(){
        successCount.set(0);
        skippedCount.set(0);
        errorCount.set(0);

        processed.clear();
        list.clear();
        index.set(0);

        stop.set(false);
    }

    public void cancel(){
        stop.set(true);

        list.forEach(IExecutorObject::cancel);
    }

    public void clear(){
        list.clear();
        index.set(0);
    }

    public boolean isWaiting(IExecutorObject executorObject){
        return list.stream().skip(index.get())
                .filter(o -> executorObject.getId().equals(o.getId()))
                .findAny()
                .isPresent();
    }

    public void addObjects(List<T> objects){
        list.addAll(objects);

        size = list.size();
    }

    public T pollObject(){
        if (index.get() >= list.size()){
            return null;
        }

        return list.get(index.getAndIncrement());
    }

    public boolean isEmpty(){
        return list.size() - index.get() == 0;
    }

    public STATUS getStatus() {
        return status;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

    public int getSuccessCount() {
        return successCount.get();
    }

    public void incrementSuccessCount(){
        successCount.incrementAndGet();
    }

    public int getSkippedCount() {
        return skippedCount.get();
    }

    public void incrementSkippedCount(){
        skippedCount.incrementAndGet();
    }

    public int getErrorCount() {
        return errorCount.get();
    }

    public void incrementErrorCount(){
        errorCount.incrementAndGet();
    }

    public List<IExecutorObject> getProcessed() {
        return processed;
    }

    public boolean isStop() {
        return stop.get();
    }

    public boolean isRunning(){
        return RUNNING.equals(status);
    }

    public boolean isDone(){
        return isRunning() && runningThread.get() == 0;
    }

    public boolean isCriticalError(){
        return CRITICAL_ERROR.equals(status);
    }

    public boolean isCompleted(){
        return COMPLETED.equals(status);
    }

    public boolean isCanceled(){
        return CANCELED.equals(status);
    }

    public void startTask(){
        runningThread.incrementAndGet();
    }

    public void stopTask(){
        runningThread.decrementAndGet();
    }

    public Class<? extends ITaskBean<T>> getTaskClass() {
        return taskClass;
    }

    public void setTaskClass(Class<? extends ITaskBean<T>> taskClass) {
        this.taskClass = taskClass;
    }

    public IExecutorListener getListener() {
        return listener;
    }

    public void setListener(IExecutorListener listener) {
        this.listener = listener;
    }

    public int getMaxErrors() {
        return maxErrors;
    }

    public void setMaxErrors(int maxErrors) {
        this.maxErrors = maxErrors;
    }

    public int getMaxThread() {
        return maxThread;
    }

    public void setMaxThread(int maxThread) {
        this.maxThread = maxThread;
    }

    public int getRunningThreadCount(){
        return runningThread.get();
    }

    public Map getCommandParameters() {
        return commandParameters;
    }

    public void setCommandParameters(Map processParameters) {
        this.commandParameters = processParameters;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public int getSize() {
        return size;
    }
}
