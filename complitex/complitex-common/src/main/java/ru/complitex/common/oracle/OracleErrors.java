package ru.complitex.common.oracle;

import java.sql.SQLException;

/**
 *
 * @author Artem
 */
public final class OracleErrors {

    public static final String CURSOR_IS_CLOSED_ERROR = "Cursor is closed.";

    private OracleErrors() {
    }

    public static boolean isCursorClosedError(Exception e){
        for (Throwable t = e; t != null; t = t.getCause()){
            if (t instanceof SQLException && CURSOR_IS_CLOSED_ERROR.equals(t.getMessage())){
                return true;
            }
        }

        return false;
    }
}
