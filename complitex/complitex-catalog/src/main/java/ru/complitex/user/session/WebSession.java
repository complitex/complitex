package ru.complitex.user.session;

import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.request.Request;
import ru.complitex.catalog.util.Strings;
import ru.complitex.user.service.UserService;

import javax.inject.Inject;
import java.util.Locale;

/**
 * @author Ivanov Anatoliy
 */
public class WebSession extends AuthenticatedWebSession {
    @Inject
    private UserService userService;

    private String username;

    public WebSession(Request request) {
        super(request);

        setLocale(new Locale("ru-RU"));
    }

    @Override
    protected boolean authenticate(String username, String password) {
        boolean authenticated = userService.authenticate(username, Strings.sha256(password));

        if (authenticated) {
            this.username = username;
        }

        return authenticated;
    }

    @Override
    public Roles getRoles() {
        Roles roles = new Roles();

        if (username != null) {
            roles.addAll(userService.roles(username));
        }

        return roles;
    }
}
