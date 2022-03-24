package ru.complitex.eirc.organization.web;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import ru.complitex.common.strategy.IStrategy;
import ru.complitex.common.strategy.organization.IOrganizationStrategy;
import ru.complitex.common.util.EjbBeanLocator;
import ru.complitex.organization.web.OrganizationMenu;
import ru.complitex.template.web.security.SecurityRole;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRole.AUTHORIZED)
public class EircOrganizationTemplateMenu extends OrganizationMenu {

    @Override
    protected IStrategy getStrategy() {
        return EjbBeanLocator.getBean(IOrganizationStrategy.BEAN_NAME);
    }
}
