package ru.complitex.pspoffice.api.resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.complitex.address.strategy.country.CountryStrategy;
import org.complitex.common.entity.DomainObject;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * @author Anatoly A. Ivanov
 *         27.04.2017 19:07
 */
@Path("/address")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Api(description = "Address Database API")
public class AddressResource {
    @EJB
    private CountryStrategy countryStrategy;

    @GET
    @Path("/country/{id}")
    @ApiOperation(value = "Get country by id", response = DomainObject.class)
    public Response getCountry(@PathParam("id") Long id){
        DomainObject domainObject = countryStrategy.getDomainObject(id);

        return domainObject != null
                ? Response.ok(domainObject).build()
                : Response.status(Response.Status.NOT_FOUND).build();
    }

    @GET
    @Path("/country")
    @ApiOperation("Get county list by query string")
    public Response getCountries(@QueryParam("query") String query, @QueryParam("limit") Integer limit){
        return Response.ok().build();
    }

    @GET
    @Path("ping")
    public Response ping(){
        return Response.ok("ping").build();
    }
}
