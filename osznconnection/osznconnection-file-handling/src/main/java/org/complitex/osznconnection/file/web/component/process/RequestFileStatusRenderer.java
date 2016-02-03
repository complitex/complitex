package org.complitex.osznconnection.file.web.component.process;

import org.complitex.common.util.ResourceUtil;
import org.complitex.osznconnection.file.entity.RequestFileStatus;

import java.util.Locale;

public final class RequestFileStatusRenderer {

    private RequestFileStatusRenderer() {
    }

    public static String render(RequestFileStatus status, Locale locale) {
        return ResourceUtil.getString(RequestFileStatusRenderer.class.getName(), status.name(), locale);
    }
}
