package org.complitex.keconnection.heatmeter.web;

import org.complitex.common.util.EjbBeanLocator;
import org.complitex.keconnection.heatmeter.web.service.ServiceContractList;
import org.complitex.organization.strategy.ServiceStrategy;
import org.complitex.template.web.template.ResourceTemplateMenu;

/**
 * @author Anatoly Ivanov
 *         Date: 12.11.2014 18:38
 */
public class ServiceMenu extends ResourceTemplateMenu{
    public ServiceMenu() {
        ServiceStrategy serviceStrategy = EjbBeanLocator.getBean(ServiceStrategy.class);

        add("service", serviceStrategy.getListPage(), serviceStrategy.getListPageParams());
        add("service_contract", ServiceContractList.class);
    }
}
