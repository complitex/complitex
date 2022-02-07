package ru.flexpay.eirc.organization_type.web;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import ru.complitex.common.strategy.IStrategy;
import ru.complitex.common.util.EjbBeanLocator;
import ru.complitex.template.web.security.SecurityRole;
import ru.flexpay.eirc.organization_type.strategy.EircOrganizationTypeStrategy;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRole.ORGANIZATION_MODULE_EDIT)
public class OrganizationTypeMenu extends ru.complitex.organization_type.menu.OrganizationTypeMenu {

    @Override
    protected IStrategy getStrategy() {
        return EjbBeanLocator.getBean(EircOrganizationTypeStrategy.class);
    }
}
