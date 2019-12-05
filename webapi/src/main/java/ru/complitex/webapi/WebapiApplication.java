package ru.complitex.webapi;

import io.helidon.common.CollectionsHelper;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.Set;

/**
 * @author Anatoly A. Ivanov
 * 05.12.2019 7:52 PM
 */
@ApplicationPath("/")
@ApplicationScoped
public class WebapiApplication extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        return CollectionsHelper.setOf(WebapiResource.class);
    }
}
