package org.complitex.osznconnection.file.service.warning;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;

/**
 *
 * @author Artem
 */
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class WebWarningRenderer extends AbstractWarningRenderer {

    @Override
    protected String getBundle() {
        return WebWarningRenderer.class.getName();
    }
}
