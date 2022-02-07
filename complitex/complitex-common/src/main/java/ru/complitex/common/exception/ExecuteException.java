package ru.complitex.common.exception;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 29.10.10 18:55
 */
public class ExecuteException extends AbstractException {
    private boolean warn = false;
    private boolean skipTrace = false;

    public ExecuteException(Throwable cause, String pattern, Object... arguments) {
        super(cause, pattern, arguments);
    }

    public ExecuteException(String pattern, Object... arguments) {
        super(pattern, arguments);
    }

    public ExecuteException(Throwable cause, boolean warn, String pattern, Object... arguments) {
        super(cause, pattern, arguments);
        this.warn = warn;
    }

    public ExecuteException(boolean warn, String pattern, Object... arguments) {
        super(pattern, arguments);
        this.warn = warn;
    }

    public boolean isWarn() {
        return warn;
    }

    public boolean isSkipTrace() {
        return skipTrace;
    }

    public ExecuteException setSkipTrace(boolean skipTrace) {
        this.skipTrace = skipTrace;

        return this;
    }
}
