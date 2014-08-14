/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.web.history;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Artem
 */
public class HistoryDateFormatter {

    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy");

    private HistoryDateFormatter() {
    }

    public static String format(Date date) {
        return DATE_FORMATTER.format(date);
    }
}
