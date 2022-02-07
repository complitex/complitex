package ru.complitex.pspoffice.report.service;

import ru.complitex.pspoffice.report.util.RtfTemplate;

import javax.ejb.Stateless;
import java.io.*;
import java.util.Map;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 11.05.11 17:45
 */
@Stateless
public class RtfReportService extends AbstractReportService {

    @Override
    public void createReport(String templateName, Map<String, String> values, OutputStream out) throws CreateReportException {
        new RtfTemplate(getTemplateInputStream(templateName), values).fill(out, RtfTemplate.VARIABLE_TYPE.FORM);
    }
}
