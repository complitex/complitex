package org.complitex.osznconnection.file.service.status.details;

import org.complitex.osznconnection.file.entity.RequestStatus;
import org.complitex.osznconnection.file.entity.StatusDetail;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import java.util.Locale;


public class StatusDetailRenderUtil {
    public static String displayStatusDetail(RequestStatus status, StatusDetail statusDetail,
            IStatusDetailRenderer statusDetailRenderer, Locale locale) {
        String countString = displayCount(statusDetail.getCount());

        return statusDetailRenderer.displayStatusDetail(status, statusDetail, locale) + countString;
    }

    public static String displayCount(Long count) {
        return count > 1 ? " (" + count + ")" : "";
    }
}
