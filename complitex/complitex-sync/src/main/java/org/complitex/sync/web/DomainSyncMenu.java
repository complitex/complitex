package org.complitex.sync.web;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.template.web.security.SecurityRole;
import org.complitex.template.web.template.ResourceTemplateMenu;

/**
 * @author inheaven on 020 20.07.15 17:07
 */
@AuthorizeInstantiation(SecurityRole.ADMIN_MODULE_EDIT)
public class DomainSyncMenu extends ResourceTemplateMenu{
    public DomainSyncMenu() {
        add("organization_sync", DomainSyncPage.class, new PageParameters().add("entity", "organization"));
        add("country_sync", DomainSyncPage.class, new PageParameters().add("entity", "country"));
        add("region_sync", DomainSyncPage.class, new PageParameters().add("entity", "region"));
        add("city_type_sync", DomainSyncPage.class, new PageParameters().add("entity", "city_type"));
        add("city_sync", DomainSyncPage.class, new PageParameters().add("entity", "city"));
        add("district_sync", DomainSyncPage.class, new PageParameters().add("entity", "district"));
        add("street_type_sync", DomainSyncPage.class, new PageParameters().add("entity", "street_type"));
        add("street_sync", DomainSyncPage.class, new PageParameters().add("entity", "street"));
        add("building_sync", DomainSyncPage.class, new PageParameters().add("entity", "building"));
    }
}
