package ru.complitex.pspoffice.backend;

import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import ru.complitex.pspoffice.api.json.CharsetRequestFilter;
import ru.complitex.pspoffice.api.json.ObjectMapperProvider;
import ru.complitex.pspoffice.backend.resource.AddressResource;
import ru.complitex.pspoffice.backend.resource.PersonResource;

import javax.ws.rs.ApplicationPath;
import java.util.logging.Logger;

/**
 * @author Anatoly A. Ivanov
 *         27.04.2017 19:54
 */
@ApplicationPath("api")
public class PspOfficeBackendApplication extends ResourceConfig {
    public PspOfficeBackendApplication() {
        //swagger
        register(io.swagger.jaxrs.listing.ApiListingResource.class);
        register(io.swagger.jaxrs.listing.SwaggerSerializers.class);

        //jackson
        register(ObjectMapperProvider.class);
        register(JacksonFeature.class);
        register(CharsetRequestFilter.class);

        register(AddressResource.class);
        register(PersonResource.class);

        //logging
        register(new LoggingFilter(Logger.getLogger(LoggingFilter.class.getName()), true));
    }
}
