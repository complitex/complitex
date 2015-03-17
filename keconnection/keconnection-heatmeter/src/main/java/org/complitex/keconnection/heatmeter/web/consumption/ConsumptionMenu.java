package org.complitex.keconnection.heatmeter.web.consumption;

import org.complitex.template.web.template.ResourceTemplateMenu;

/**
 * @author inheaven on 016 16.03.15 18:45
 */
public class ConsumptionMenu extends ResourceTemplateMenu{
    public ConsumptionMenu() {
        add(CentralHeatingConsumptionList.class);
    }
}
