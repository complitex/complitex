package ru.complitex.osznconnection.organization_type.menu;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import ru.complitex.common.strategy.IStrategy;
import ru.complitex.common.util.EjbBeanLocator;
import ru.complitex.organization_type.menu.OrganizationTypeMenu;
import ru.complitex.osznconnection.organization_type.strategy.OsznOrganizationTypeStrategy;
import ru.complitex.template.web.security.SecurityRole;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRole.ORGANIZATION_MODULE_EDIT)
public class OsznOrganizationTypeMenu extends OrganizationTypeMenu {

    @Override
    protected IStrategy getStrategy() {
        return EjbBeanLocator.getBean(OsznOrganizationTypeStrategy.class);
    }
}
