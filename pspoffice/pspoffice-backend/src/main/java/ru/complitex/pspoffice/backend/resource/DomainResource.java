package ru.complitex.pspoffice.backend.resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.entity.DomainObjectFilter;
import ru.complitex.common.strategy.StrategyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.complitex.pspoffice.api.model.DomainModel;
import ru.complitex.pspoffice.backend.adapter.DomainAdapter;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.stream.Collectors;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

/**
 * @author Anatoly A. Ivanov
 * 09.10.2017 15:09
 */
@Path("domain")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Api(description = "Domain API")
public class DomainResource {
    private Logger log = LoggerFactory.getLogger(DomainResource.class);

    @EJB
    private StrategyFactory strategyFactory;

    @GET
    @Path("ping")
    public Response ping(){
        return Response.ok("pong").build();
    }

    @GET
    @Path("{entity}/{id}")
    @ApiOperation(value = "Get domain model by entity name and object id", response = DomainModel.class)
    public Response getDomain(@PathParam("entity") String entity, @PathParam("id") Long id){
        DomainObject domainObject = strategyFactory.getStrategy(entity).getDomainObject(id, true);

        if (domainObject == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(DomainAdapter.adapt(domainObject)).build();
    }

    @GET
    @Path("{entity}")
    @ApiOperation(value = "Get domain model list by entity name and object id", response = DomainModel.class, responseContainer = "List")
    public Response getDomains(@PathParam("entity") String entity,
                               @QueryParam("offset") @DefaultValue("0") Long offset,
                               @QueryParam("limit") @DefaultValue("10") Long limit,
                               @QueryParam("value") String value){
        DomainObjectFilter filter = new DomainObjectFilter();
        filter.setFirst(offset);
        filter.setCount(limit);
        filter.setValue(value);

        return Response.ok(strategyFactory.getStrategy(entity).getList(filter).stream()
                .map(DomainAdapter::adapt)
                .collect(Collectors.toList())).build();
    }

    @GET
    @Path("{entity}/size")
    @ApiOperation(value = "Get domain model count", response = DomainModel.class)
    public Response getDomainsCount(@PathParam("entity") String entity){
        return Response.ok(strategyFactory.getStrategy(entity).getCount(new DomainObjectFilter())).build();
    }

    @PUT
    @Path("{entity}")
    @ApiOperation(value = "Put domain")
    public Response putDomain(@PathParam("entity") String entity, DomainModel domainModel){
        try {
            Long id = domainModel.getId();

            if (id != null){
                strategyFactory.getStrategy(entity).update(DomainAdapter.adapt(domainModel));
            }else {
                strategyFactory.getStrategy(entity).insert(DomainAdapter.adapt(domainModel));
            }

            return Response.ok().build();
        } catch (Exception e) {
            log.error("put domain error", e);

            return Response.status(INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("{entity}/{id}")
    @ApiOperation(value = "Delete domain")
    public Response deleteDomain(@PathParam("entity") String entity, @PathParam("id") Long id){
        try {
            strategyFactory.getStrategy(entity).delete(id);

            return Response.ok().build();
        } catch (Exception e) {
            log.error("delete domain error", e);

            return Response.status(INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }
}
