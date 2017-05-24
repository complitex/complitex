package ru.complitex.pspoffice.api.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * @author Anatoly A. Ivanov
 *         24.05.2017 17:41
 */
@Path("person")
public class PersonResource {

    @GET
    @Path("ping")
    public Response ping(){
        return Response.ok("ping").build();
    }
}
