package ru.complitex.pspoffice.api;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import ru.complitex.pspoffice.api.resource.AddressResource;

import javax.ws.rs.ApplicationPath;

/**
 * @author Anatoly A. Ivanov
 *         27.04.2017 19:54
 */
@ApplicationPath("api")
public class PspApiApplication extends ResourceConfig {
    public PspApiApplication() {
        //swagger
        register(io.swagger.jaxrs.listing.ApiListingResource.class);
        register(io.swagger.jaxrs.listing.SwaggerSerializers.class);

        //jackson
        register(ObjectMapperProvider.class);
        register(JacksonFeature.class);
        register(CharsetRequestFilter.class);

        register(AddressResource.class);
    }
}
