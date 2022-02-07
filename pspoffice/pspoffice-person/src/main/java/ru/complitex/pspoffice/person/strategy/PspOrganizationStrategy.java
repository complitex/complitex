package ru.complitex.pspoffice.person.strategy;

import ru.complitex.common.strategy.organization.IOrganizationStrategy;
import ru.complitex.organization.strategy.OrganizationStrategy;

import javax.ejb.Stateless;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 02.07.13 17:56
 */
@Stateless(name = IOrganizationStrategy.BEAN_NAME)
public class PspOrganizationStrategy extends OrganizationStrategy {
//    todo move to common module
}
