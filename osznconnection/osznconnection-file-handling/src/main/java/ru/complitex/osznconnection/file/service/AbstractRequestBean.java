package ru.complitex.osznconnection.file.service;

import ru.complitex.common.service.AbstractBean;
import ru.complitex.osznconnection.file.entity.RequestFileType;
import ru.complitex.osznconnection.file.service.warning.RequestWarningBean;

import javax.ejb.EJB;

public abstract class AbstractRequestBean extends AbstractBean {

    @EJB
    private RequestWarningBean requestWarningBean;

    protected void clearWarnings(long requestFileId, RequestFileType requestFileType) {
        requestWarningBean.delete(requestFileId, requestFileType);
    }
}
