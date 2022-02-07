package ru.complitex.pspoffice.report.html.web;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.pspoffice.report.html.entity.Report;
import ru.complitex.pspoffice.report.html.service.ReportBean;
import ru.complitex.pspoffice.report.html.service.ReportService;
import ru.complitex.template.web.template.TemplatePage;

import javax.ejb.EJB;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 14.06.12 17:17
 */
public class ReportView extends TemplatePage {
    @EJB
    private ReportService reportService;

    @EJB
    private ReportBean reportBean;

    public ReportView(PageParameters parameters) {
        Report report = reportBean.getReport(parameters.get("id").toLongObject());

        add(new Label("title", report.getName()));
        add(new Label("report", reportService.fillMarkup(report)).setEscapeModelStrings(false));
    }
}

