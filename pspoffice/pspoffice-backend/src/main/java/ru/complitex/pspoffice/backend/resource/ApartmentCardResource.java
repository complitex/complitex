package ru.complitex.pspoffice.backend.resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.complitex.common.entity.DomainObject;
import org.complitex.pspoffice.person.strategy.ApartmentCardStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.complitex.pspoffice.api.model.DomainModel;
import ru.complitex.pspoffice.backend.adapter.DomainAdapter;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * @author Anatoly A. Ivanov
 * 26.10.2017 17:03
 */
@Path("apartment_card")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Api(description = "Apartment Card API")
public class ApartmentCardResource {
    private Logger log = LoggerFactory.getLogger(ApartmentCardResource.class);

    @EJB
    private ApartmentCardStrategy apartmentCardStrategy;

    @GET
    @Path("ping")
    public Response ping(){
        return Response.ok("pong").build();
    }

    @GET
    @Path("{id}")
    @ApiOperation(value = "Get apartment card by id", response = DomainModel.class)
    public Response getApartmentCard(@PathParam("id") Long id){
        DomainObject o = apartmentCardStrategy.getDomainObject(id);

        if (o == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(DomainAdapter.adapt(o)).build();
    }
}
