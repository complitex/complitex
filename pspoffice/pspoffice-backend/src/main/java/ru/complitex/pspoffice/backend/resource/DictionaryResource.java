package ru.complitex.pspoffice.backend.resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.complitex.common.entity.DomainObjectFilter;
import org.complitex.pspoffice.document_type.strategy.DocumentTypeStrategy;
import ru.complitex.pspoffice.api.model.NameModel;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.util.stream.Collectors;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * @author Anatoly A. Ivanov
 * 03.08.2017 17:50
 */
@Path("dictionary")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Api(description = "Dictionary API")
public class DictionaryResource {
    @EJB
    private DocumentTypeStrategy documentTypeStrategy;

    @GET
    @Path("ping")
    public Response ping(){
        return Response.ok("pong").build();
    }

    @GET
    @Path("document-type")
    @ApiOperation(value = "Get document types", response = NameModel.class, responseContainer = "List")
    public Response getDocumentTypes(){
        return Response.ok(documentTypeStrategy.getList(new DomainObjectFilter()).stream()
                .map(d -> new NameModel(d.getObjectId(), d.getStringMap(DocumentTypeStrategy.NAME)))
                .collect(Collectors.toList())).build();
    }
}
