package org.complitex.keconnection.organization_type.menu;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.complitex.common.strategy.IStrategy;
import org.complitex.common.util.EjbBeanLocator;
import org.complitex.keconnection.organization_type.strategy.KeConnectionOrganizationTypeStrategy;
import org.complitex.organization_type.menu.OrganizationTypeMenu;
import org.complitex.template.web.security.SecurityRole;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRole.ORGANIZATION_MODULE_EDIT)
public class KeConnectionOrganizationTypeMenu extends OrganizationTypeMenu {

    @Override
    protected IStrategy getStrategy() {
        return EjbBeanLocator.getBean(KeConnectionOrganizationTypeStrategy.class);
    }
}
