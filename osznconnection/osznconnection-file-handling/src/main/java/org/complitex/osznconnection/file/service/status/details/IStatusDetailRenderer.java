package org.complitex.osznconnection.file.service.status.details;

import org.complitex.osznconnection.file.entity.RequestStatus;
import org.complitex.osznconnection.file.entity.StatusDetail;

import java.util.Locale;

/**
 *
 * @author Artem
 */
public interface IStatusDetailRenderer {

    String displayStatusDetail(RequestStatus status, StatusDetail statusDetail, Locale locale);
}
