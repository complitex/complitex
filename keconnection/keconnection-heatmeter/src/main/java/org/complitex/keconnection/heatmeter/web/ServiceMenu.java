package org.complitex.keconnection.heatmeter.web;

import org.complitex.common.util.EjbBeanLocator;
import org.complitex.keconnection.heatmeter.strategy.ServiceStrategy;
import org.complitex.template.web.template.ResourceTemplateMenu;

/**
 * @author Anatoly Ivanov
 *         Date: 12.11.2014 18:38
 */
public class ServiceMenu extends ResourceTemplateMenu{
    ServiceStrategy serviceStrategy = EjbBeanLocator.getBean(ServiceStrategy.class);

    public ServiceMenu() {
        add("service", serviceStrategy.getListPage(), serviceStrategy.getListPageParams());
    }
}
