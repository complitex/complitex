package ru.complitex.common.exception;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 25.05.11 18:51
 */
public class CanceledByUserException extends AbstractException {
    public CanceledByUserException() {
        super("Процесс остановлен пользователем");
    }
}
