package ru.complitex.keconnection.heatmeter.service;

import ru.complitex.common.util.ResourceUtil;
import ru.complitex.keconnection.heatmeter.entity.HeatmeterBindingStatus;

import javax.ejb.Stateless;
import java.util.Locale;

/**
 *
 * @author Artem
 */
@Stateless
public class HeatmeterBindingStatusRenderer {

    private static final String RESOURCE_BUNDLE = HeatmeterBindingStatusRenderer.class.getName();

    public String render(HeatmeterBindingStatus status, Locale locale) {
        if (status == null) {
            status = HeatmeterBindingStatus.UNBOUND;
        }
        return ResourceUtil.getString(RESOURCE_BUNDLE, status.name(), locale);
    }
}
