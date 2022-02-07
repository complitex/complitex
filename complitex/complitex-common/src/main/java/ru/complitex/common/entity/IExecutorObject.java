package ru.complitex.common.entity;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 14.04.11 17:26
 */
public interface IExecutorObject extends ILoggable{
    void cancel();
    String getErrorMessage();
    void setErrorMessage(String message);
    Enum getStatus();
    String getObjectName();
    boolean isCanceled();
    boolean isProcessing();
    boolean isWaiting();
}
