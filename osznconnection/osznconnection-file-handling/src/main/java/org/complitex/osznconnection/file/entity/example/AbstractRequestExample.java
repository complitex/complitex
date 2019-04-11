package org.complitex.osznconnection.file.entity.example;

import org.complitex.osznconnection.file.entity.RequestStatus;

import java.io.Serializable;

public class AbstractRequestExample implements Serializable {

    private RequestStatus status;
    private Long requestFileId;

    private Long organizationId;
    private Long userOrganizationId;

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public Long getRequestFileId() {
        return requestFileId;
    }

    public void setRequestFileId(Long requestFileId) {
        this.requestFileId = requestFileId;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Long getUserOrganizationId() {
        return userOrganizationId;
    }

    public void setUserOrganizationId(Long userOrganizationId) {
        this.userOrganizationId = userOrganizationId;
    }
}
