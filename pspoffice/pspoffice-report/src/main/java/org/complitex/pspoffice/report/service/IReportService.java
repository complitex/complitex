package org.complitex.pspoffice.report.service;

import java.io.OutputStream;
import java.util.Map;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 19.05.11 14:58
 */
public interface IReportService {
    void createReport(String templateName, Map<String, String> values, OutputStream out) throws CreateReportException;
}
