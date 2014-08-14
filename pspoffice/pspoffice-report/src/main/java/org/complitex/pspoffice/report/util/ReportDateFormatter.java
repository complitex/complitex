/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.report.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Artem
 */
public final class ReportDateFormatter {

    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("dd.MM.yyyy");

    private ReportDateFormatter() {
    }

    public static String format(Date date) {
        return DATE_FORMATTER.format(date);
    }
}
