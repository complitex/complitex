package org.complitex.pspoffice.report.service;

import java.io.InputStream;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 31.05.11 15:30
 */
public abstract class AbstractReportService implements IReportService{
    protected final static String TEMPLATE_PATH = "/org/complitex/pspoffice/report/template";

    protected InputStream getTemplateInputStream(String templateName){
        InputStream inputStream = getClass().getResourceAsStream(TEMPLATE_PATH + "/" + templateName);

        if (inputStream == null){
            String ext = templateName.substring(templateName.indexOf("."));
            String name = templateName.substring(0, templateName.indexOf("_"));

            return getClass().getResourceAsStream(TEMPLATE_PATH + "/" + name + ext);
        }

        return inputStream;
    }
}
