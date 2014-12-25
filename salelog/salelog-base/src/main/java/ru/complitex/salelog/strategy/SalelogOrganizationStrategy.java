package ru.complitex.salelog.strategy;

import org.complitex.common.strategy.organization.IOrganizationStrategy;
import org.complitex.organization.strategy.OrganizationStrategy;

import javax.ejb.Stateless;

/**
 * @author Pavel Sknar
 */
@Stateless(name = IOrganizationStrategy.BEAN_NAME)
public class SalelogOrganizationStrategy extends OrganizationStrategy {
    public final static Long MODULE_ID = 10L;

}
