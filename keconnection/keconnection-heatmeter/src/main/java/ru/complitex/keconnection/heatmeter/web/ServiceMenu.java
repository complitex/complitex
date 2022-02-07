package ru.complitex.keconnection.heatmeter.web;

import ru.complitex.common.util.EjbBeanLocator;
import ru.complitex.keconnection.heatmeter.web.service.ServiceContractList;
import ru.complitex.organization.strategy.ServiceStrategy;
import ru.complitex.template.web.template.ResourceTemplateMenu;

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
