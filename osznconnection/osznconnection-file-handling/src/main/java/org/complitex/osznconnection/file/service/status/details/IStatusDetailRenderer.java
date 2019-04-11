package org.complitex.osznconnection.file.service.status.details;

import org.complitex.osznconnection.file.entity.RequestStatus;
import org.complitex.osznconnection.file.entity.StatusDetail;

import java.io.Serializable;
import java.util.Locale;

public interface IStatusDetailRenderer extends Serializable {

    String displayStatusDetail(StatusDetail statusDetail, RequestStatus status, Locale locale);
}
