/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.report.web;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.HttpConstraint;
import javax.servlet.annotation.ServletSecurity;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.complitex.pspoffice.report.util.ReportGenerationUtil;
import org.complitex.template.web.security.SecurityRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Artem
 */
@WebServlet(name = "PrintReportServlet", urlPatterns = "/PrintReportServlet")
@ServletSecurity(@HttpConstraint(rolesAllowed = {SecurityRole.AUTHORIZED}))
public class PrintReportServlet extends HttpServlet {

    private final Logger log = LoggerFactory.getLogger(PrintReportServlet.class);

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletOutputStream servletOutputStream = null;

        try {
            servletOutputStream = response.getOutputStream();
            response.setContentType("application/pdf");

            String sessionKey = request.getParameter("key");
            String locale = request.getParameter("locale") != null ? request.getParameter("locale")
                    : ReportDownloadPanel.RUSSIAN_REPORT_LOCALE;
            AbstractReportDownload<?> reportDownload = (AbstractReportDownload) request.getSession().getAttribute(sessionKey);
            request.getSession().removeAttribute(sessionKey);

            ReportGenerationUtil.write("pdf", reportDownload, servletOutputStream, locale);
        } catch (Exception e) {
            String error = "Ошибка генерации отчета";
            log.error(error, e);
            response.setContentType("text/plain");
            response.setCharacterEncoding("UTF-8");
            try {
                if (servletOutputStream == null) {
                    servletOutputStream = response.getOutputStream();
                }
                servletOutputStream.print(error);
            } catch (Exception t) {
                log.error("Couldn't to output error message.", t);
            }
        } finally {
            if (servletOutputStream != null) {
                try {
                    servletOutputStream.flush();
                    servletOutputStream.close();
                } catch (Exception e) {
                    log.error("Couldn't to flush and close servlet output stream.", e);
                }
            }
        }
    }
}
