package ru.complitex.keconnection.organization_type.menu;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import ru.complitex.common.strategy.IStrategy;
import ru.complitex.common.util.EjbBeanLocator;
import ru.complitex.keconnection.organization_type.strategy.KeConnectionOrganizationTypeStrategy;
import ru.complitex.organization_type.menu.OrganizationTypeMenu;
import ru.complitex.template.web.security.SecurityRole;

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
