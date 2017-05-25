package ru.complitex.pspoffice.api.resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.complitex.common.entity.Gender;
import org.complitex.common.util.AttributeUtil;
import org.complitex.pspoffice.document.strategy.DocumentStrategy;
import org.complitex.pspoffice.document.strategy.entity.Document;
import org.complitex.pspoffice.person.strategy.PersonStrategy;
import org.complitex.pspoffice.person.strategy.entity.Person;
import ru.complitex.pspoffice.api.model.DocumentObject;
import ru.complitex.pspoffice.api.model.PersonObject;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

/**
 * @author Anatoly A. Ivanov
 *         24.05.2017 17:41
 */
@Path("person")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Api(description = "Person API")
public class PersonResource extends AbstractResource{

    @EJB
    private PersonStrategy personStrategy;

    @GET
    @Path("ping")
    public Response ping(){
        return Response.ok("ping").build();
    }

    @GET
    @Path("{id}")
    @ApiOperation(value = "Get person by id")
    public Response getPerson(@PathParam("id") Long id){
        Person p = personStrategy.getDomainObject(id);

        if (p == null){
            return Response.status(NOT_FOUND).build();
        }

        PersonObject person = new PersonObject();
        person.setLastName(getNames(p, PersonStrategy.LAST_NAME));
        person.setFirstName(getNames(p, PersonStrategy.FIRST_NAME));
        person.setMiddleName(getNames(p, PersonStrategy.MIDDLE_NAME));
        person.setIdentityCode(p.getIdentityCode());
        person.setBirthDate(p.getStringValue(PersonStrategy.BIRTH_DATE));
        person.setBirthCountry(p.getBirthCountry());
        person.setBirthRegion(p.getBirthRegion());
        person.setBirthCity(p.getBirthCity());
        person.setBirthDistrict(p.getBirthDistrict());
        person.setGender(Gender.MALE.equals(p.getGender()) ? 1 : 0);

        if (p.getDocument() != null){
            List<DocumentObject> documents = new ArrayList<>();
            person.setDocuments(documents);

            Document d = p.getDocument();

            DocumentObject document = new DocumentObject();
            document.setObjectId(d.getObjectId());
            document.setType(d.getValueId(d.getDocumentTypeId()));
            document.setSeries(d.getSeries());
            document.setNumber(d.getNumber());
            document.setOrganizationIssued(d.getOrganizationIssued());
            document.setDateIssued(d.getStringValue(DocumentStrategy.DATE_ISSUED));

            documents.add(document);
        }

        person.setUkraineCitizenship(AttributeUtil.getBooleanValue(p, PersonStrategy.UKRAINE_CITIZENSHIP) ? 1 : 0);
        person.setMilitaryServiceRelationId(p.getValueId(PersonStrategy.MILITARY_SERVICE_RELATION));

        return Response.ok(person).build();
    }

}
