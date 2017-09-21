package ru.complitex.pspoffice.backend.resource;

import io.swagger.annotations.Api;
import org.complitex.common.entity.FilterWrapper;
import org.complitex.common.strategy.EntityBean;
import org.complitex.entity.Entity;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

/**
 * @author Anatoly A. Ivanov
 * 23.08.2017 11:52
 */
@Path("entity")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Api(description = "Entity API")
public class EntityResource {
    @EJB
    private EntityBean entityBean;

    @GET
    @Path("ping")
    public Response ping(){
        return Response.ok("pong").build();
    }

    @GET
    @Path("{entity}")
    public Response getEntity(@PathParam("entity") String entity){
        Entity e = entityBean.getEntity(entity);

        if (entity == null){
            return Response.status(NOT_FOUND).build();
        }

        return Response.ok(e).build();
    }

    @GET
    public Response getEntities(@QueryParam("offset")  @DefaultValue("0") Long offset,
                                @QueryParam("limit")  @DefaultValue("10") Long limit){
        return Response.ok(entityBean.getEntities(new FilterWrapper<>(offset, limit))).build();
    }

    @GET
    @Path("size")
    public Response getEntitiesCount(){
        return Response.ok(entityBean.getEntitiesCount(new FilterWrapper<>())).build();
    }

}
