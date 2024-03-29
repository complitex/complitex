package ru.complitex.osznconnection.file.service.warning;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;

/**
 *
 * @author Artem
 */
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class ReportWarningRenderer extends AbstractWarningRenderer {

    @Override
    protected String getBundle() {
        return ReportWarningRenderer.class.getName();
    }
}
