/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.report.web;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.resource.AbstractResourceStreamWriter;
import org.apache.wicket.util.resource.IResourceStream;
import org.complitex.pspoffice.report.service.CreateReportException;
import org.complitex.pspoffice.report.util.ReportGenerationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @author Artem
 */
public final class DownloadPage extends WebPage {

    private final Logger log = LoggerFactory.getLogger(DownloadPage.class);

    public DownloadPage(PageParameters parameters) {
        String sessionKey = parameters.get("key").toString();
        String type = parameters.get("type").toString() != null ? parameters.get("type").toString().toLowerCase()
                : ReportDownloadPanel.PDF_REPORT_FORMAT.toLowerCase();
        String locale = parameters.get("locale").toString() != null ? parameters.get("locale").toString()
                : ReportDownloadPanel.RUSSIAN_REPORT_LOCALE;

        AbstractReportDownload<?> reportDownload = retrieveReportDownload(sessionKey);
        getRequestCycle().scheduleRequestHandlerAfterCurrent(getResourceStreamRequestHandler(reportDownload, type, locale));

    }

    private static AbstractReportDownload<?> retrieveReportDownload(String key) {
        HttpSession session = ((HttpServletRequest) RequestCycle.get().getRequest().getContainerRequest()).getSession();
        AbstractReportDownload<?> reportDownload = (AbstractReportDownload) session.getAttribute(key);
        session.removeAttribute(key);
        return reportDownload;
    }

    private static ResourceStreamRequestHandler getResourceStreamRequestHandler(AbstractReportDownload<?> reportDownload,
            String type, String locale) {
        return new ResourceStreamRequestHandler(getResourceStreamWriter(reportDownload, type, locale),
                reportDownload.getFileName(ReportGenerationUtil.getLocale(locale)));
    }

    private static IResourceStream getResourceStreamWriter(final AbstractReportDownload<?> reportDownload, final String type,
            final String locale) {
        return new AbstractResourceStreamWriter() {

            @Override
            public String getContentType() {
                if (ReportDownloadPanel.PDF_REPORT_FORMAT.equalsIgnoreCase(type)) {
                    return "application/pdf";
                } else if (ReportDownloadPanel.RTF_REPORT_FORMAT.equalsIgnoreCase(type)) {
                    return "application/rtf";
                }
                return null;
            }

            @Override
            public void write(OutputStream output) throws IOException {
                try {
                    ReportGenerationUtil.write(type, reportDownload, output, locale);
                } catch (CreateReportException e) {
                    LoggerFactory.getLogger(DownloadPage.class).error("Couldn't create report.", e);
                }
            }
        };
    }
}
