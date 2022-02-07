package ru.complitex.osznconnection.file.service;

import ru.complitex.common.util.ResourceUtil;
import ru.complitex.osznconnection.file.entity.RequestStatus;

import java.util.Locale;

public class StatusRenderUtil { //todo move properties to status package
    private static final String RESOURCE_BUNDLE = StatusRenderUtil.class.getName();

    public static String displayStatus(RequestStatus status, Locale locale) {
        return ResourceUtil.getString(RESOURCE_BUNDLE, status.name(), locale);
    }
}
