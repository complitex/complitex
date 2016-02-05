package org.complitex.osznconnection.file.entity.example;

import org.complitex.osznconnection.file.entity.RequestStatus;

import java.io.Serializable;

/**
 *
 * @author Artem
 */
public class AbstractRequestExample implements Serializable {

    private RequestStatus status;
    private Long requestFileId;

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
}
