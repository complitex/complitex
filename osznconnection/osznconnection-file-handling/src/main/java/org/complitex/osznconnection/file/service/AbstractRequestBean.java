package org.complitex.osznconnection.file.service;

import org.complitex.common.service.AbstractBean;
import org.complitex.osznconnection.file.entity.RequestFileType;
import org.complitex.osznconnection.file.service.warning.RequestWarningBean;

import javax.ejb.EJB;

public abstract class AbstractRequestBean extends AbstractBean {

    @EJB
    private RequestWarningBean requestWarningBean;

    protected void clearWarnings(long requestFileId, RequestFileType requestFileType) {
        requestWarningBean.delete(requestFileId, requestFileType);
    }
}
