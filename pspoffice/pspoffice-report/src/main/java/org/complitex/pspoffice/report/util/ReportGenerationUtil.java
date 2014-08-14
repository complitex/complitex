/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.report.util;

import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.apache.wicket.util.string.Strings;
import org.complitex.dictionary.util.EjbBeanLocator;
import org.complitex.dictionary.util.StringUtil;
import org.complitex.pspoffice.report.entity.IReportField;
import org.complitex.pspoffice.report.service.CreateReportException;
import org.complitex.pspoffice.report.service.IReportService;
import org.complitex.pspoffice.report.web.AbstractReportDownload;
import org.complitex.pspoffice.report.web.ReportDownloadPanel;

/**
 *
 * @author Artem
 */
public final class ReportGenerationUtil {

    private ReportGenerationUtil() {
    }

    public static void write(String type, AbstractReportDownload<?> reportDownload, OutputStream output, String locale)
            throws CreateReportException {
        Map<IReportField, Object> values = reportDownload.getValues(getLocale(locale));
        Map<String, String> map = new HashMap<String, String>();

        for (IReportField key : reportDownload.getReportFields()) {
            map.put(key.getFieldName(), displayValue(values.get(key)));
        }

        IReportService reportService = getReportService(type);
        reportService.createReport(reportDownload.getReportName() + "_" + locale + "." + type, map, output);
    }

    private static IReportService getReportService(String type) {
        String beanName = Strings.capitalize(type) + "ReportService";
        return EjbBeanLocator.getBean(beanName, true);
    }

    private static String displayValue(Object value) {
        if (value instanceof Date) {
            return ReportDateFormatter.format((Date) value);
        }
        return StringUtil.valueOf(value);
    }

    public static Locale getLocale(String locale) {
        if (ReportDownloadPanel.RUSSIAN_REPORT_LOCALE.equals(locale)) {
            return new Locale("ru");
        }
        if (ReportDownloadPanel.UKRAIN_REPORT_LOCALE.equals(locale)) {
            return new Locale("uk");
        }
        return null;
    }
}
