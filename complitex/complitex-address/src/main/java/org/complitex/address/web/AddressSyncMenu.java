package org.complitex.address.web;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.address.web.sync.AddressSyncPage;
import org.complitex.template.web.template.ResourceTemplateMenu;

/**
 * @author inheaven on 020 20.07.15 17:07
 */
public class AddressSyncMenu extends ResourceTemplateMenu{
    public AddressSyncMenu() {
        add("district_sync", AddressSyncPage.class, new PageParameters().add("entity", "district"));
        add("street_type_sync", AddressSyncPage.class, new PageParameters().add("entity", "street_type"));
        add("street_sync", AddressSyncPage.class, new PageParameters().add("entity", "street"));
        add("building_sync", AddressSyncPage.class, new PageParameters().add("entity", "building"));
    }
}
