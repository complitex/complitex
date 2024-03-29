package ru.complitex.common.web.component.factory;

import org.apache.wicket.Component;
import org.apache.wicket.protocol.http.WebApplication;
import ru.complitex.common.web.IWebComponentResolvableApplication;
import ru.complitex.common.web.IWebComponentResolver;

/**
 *
 * @author Artem
 */
public class WebComponentFactoryUtil {

    public static Class<? extends Component> getComponentClass(String componentName) {
        IWebComponentResolvableApplication application =
                (IWebComponentResolvableApplication) WebApplication.get();
        IWebComponentResolver componentResolver = application.getWebComponentResolver();
        return componentResolver.getComponentClass(componentName);
    }

    private WebComponentFactoryUtil() {
    }
}
