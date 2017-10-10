package ru.complitex.pspoffice.backend.resource;

import io.swagger.annotations.Api;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * @author Anatoly A. Ivanov
 * 09.10.2017 15:09
 */
@Path("domain")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Api(description = "Domain API")
public class DomainResource {
    @GET
    @Path("ping")
    public Response ping(){
        return Response.ok("pong").build();
    }
}
