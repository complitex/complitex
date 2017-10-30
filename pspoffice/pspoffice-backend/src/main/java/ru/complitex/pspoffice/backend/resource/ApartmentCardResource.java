package ru.complitex.pspoffice.backend.resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.entity.EntityAttribute;
import org.complitex.common.entity.ValueType;
import org.complitex.common.strategy.EntityBean;
import org.complitex.common.strategy.StrategyFactory;
import org.complitex.pspoffice.person.strategy.ApartmentCardStrategy;
import org.complitex.pspoffice.person.strategy.service.PersonNameBean;
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

    @EJB
    private EntityBean entityBean;

    @EJB
    private StrategyFactory strategyFactory;

    @EJB
    private PersonNameBean personNameBean;

    @GET
    @Path("ping")
    public Response ping(){
        return Response.ok("pong").build();
    }

    @GET
    @Path("{id}")
    @ApiOperation(value = "Get apartment card by id", response = DomainModel.class)
    public Response getApartmentCard(@PathParam("id") Long id){
        DomainObject domainObject = apartmentCardStrategy.getDomainObject(id);

        if (domainObject == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        DomainModel domainModel = DomainAdapter.adapt(domainObject);

        domainModel.getAttributes().forEach(a -> {
            EntityAttribute ea = entityBean.getAttributeType(a.getEntityAttributeId());

            if (ea.getValueType().equals(ValueType.ENTITY)){
                DomainObject o = strategyFactory.getStrategy(entityBean.getEntity(ea.getReferenceId()).getEntity())
                        .getDomainObject(a.getValueId(), true);

                if (o != null) {
                    a.setReference(DomainAdapter.adapt(o));
                }
            }

            //todo person model

        });

        return Response.ok(domainModel).build();
    }
}

