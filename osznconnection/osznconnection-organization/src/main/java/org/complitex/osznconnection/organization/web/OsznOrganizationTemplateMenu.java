package org.complitex.osznconnection.organization.web;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.complitex.common.strategy.IStrategy;
import org.complitex.common.strategy.organization.IOrganizationStrategy;
import org.complitex.common.util.EjbBeanLocator;
import org.complitex.organization.web.OrganizationMenu;
import org.complitex.template.web.security.SecurityRole;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRole.AUTHORIZED)
public class OsznOrganizationTemplateMenu extends OrganizationMenu {

    @Override
    protected IStrategy getStrategy() {
        return EjbBeanLocator.getBean(IOrganizationStrategy.BEAN_NAME);
    }
}
