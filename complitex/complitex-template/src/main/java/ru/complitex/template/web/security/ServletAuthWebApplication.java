package ru.complitex.template.web.security;

import org.apache.wicket.Component;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.Session;
import org.apache.wicket.authorization.IUnauthorizedComponentInstantiationListener;
import org.apache.wicket.authorization.UnauthorizedInstantiationException;
import org.apache.wicket.authroles.authorization.strategies.role.IRoleCheckingStrategy;
import org.apache.wicket.authroles.authorization.strategies.role.RoleAuthorizationStrategy;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.cycle.RequestCycle;
import ru.complitex.common.entity.Log.EVENT;
import ru.complitex.common.service.LogBean;
import ru.complitex.common.util.EjbBeanLocator;
import ru.complitex.template.Module;
import ru.complitex.template.web.pages.login.Login;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 22.07.2010 17:53:29
 *
 * Приложение Wicket, которое использует авторизацию сервлет контейнера.
 */
public abstract class ServletAuthWebApplication extends WebApplication implements IRoleCheckingStrategy,
        IUnauthorizedComponentInstantiationListener {

    private final Logger log = LoggerFactory.getLogger(ServletAuthWebApplication.class);

    @Override
    protected void init() {
        super.init();
        getSecuritySettings().setAuthorizationStrategy(new RoleAuthorizationStrategy(this));
        getSecuritySettings().setUnauthorizedComponentInstantiationListener(this);
    }

    @Override
    public boolean hasAnyRole(Roles roles) {
        HttpServletRequest request = (HttpServletRequest) RequestCycle.get().getRequest().getContainerRequest();
        if (roles != null) {
            for (String role : roles) {
                if (request.isUserInRole(role)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasAnyRole(String... roles) {
        return hasAnyRole(new Roles(roles));
    }

    @Override
    public void onUnauthorizedInstantiation(Component component) {
        HttpServletRequest servletRequest = (HttpServletRequest) RequestCycle.get().getRequest().getContainerRequest();

        boolean sessionNotExist = servletRequest.getSession(false) == null;
        if (sessionNotExist) { // user session has expired.
            Session.get().invalidate();
            throw new RestartResponseException(getApplicationSettings().getPageExpiredErrorPage());
        } else { // user session is active yet.
            Principal userPrincipal = servletRequest.getUserPrincipal();
            if (userPrincipal == null) { // user is unauthorized.
                Session.get().invalidate();
                throw new RestartResponseException(Login.class);
            } else { // user is authorized but access to resource forbidden.
                try {
                    LogBean logBean = EjbBeanLocator.getBean(LogBean.class);
                    logBean.error(Module.NAME, SecurityWebListener.class, null, null, EVENT.ACCESS_DENIED, "Ресурс: {0}",
                            component.getClass().getName());
                } catch (Exception e) {
                    log.error("Couldn't to log unauthorized access.", e);
                }
                throw new UnauthorizedInstantiationException(component.getClass());
            }
        }
    }
}
