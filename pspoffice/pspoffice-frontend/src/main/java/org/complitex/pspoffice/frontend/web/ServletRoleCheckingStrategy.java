package org.complitex.pspoffice.frontend.web;

import org.apache.wicket.authroles.authorization.strategies.role.IRoleCheckingStrategy;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.request.cycle.RequestCycle;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Anatoly A. Ivanov
 * 26.06.2017 14:44
 */
public class ServletRoleCheckingStrategy implements IRoleCheckingStrategy{

    @Override
    public boolean hasAnyRole(Roles roles) {
        HttpServletRequest request = (HttpServletRequest) RequestCycle.get().getRequest().getContainerRequest();

        for (String role : roles) {
            if (request.isUserInRole(role)) {
                return true;
            }
        }

        return false;
    }
}
