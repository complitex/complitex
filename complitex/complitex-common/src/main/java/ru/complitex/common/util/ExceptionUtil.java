package ru.complitex.common.util;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 16.10.12 17:43
 */
public class ExceptionUtil {
    public static String getCauseMessage(Exception e, boolean initial){
        if (e.getCause() != null){
            if (initial || e.getCause().getMessage() == null){
                Throwable t = e;

                while (t.getCause() != null){
                    t = t.getCause();
                }

                return t.getMessage();
            }

            return e.getMessage() + " ->  " + e.getCause().getMessage();
        }

        return e.getMessage() + "";
    }

    public static String getCauseMessage(Exception e){
        return getCauseMessage(e, false) + "";
    }
}
