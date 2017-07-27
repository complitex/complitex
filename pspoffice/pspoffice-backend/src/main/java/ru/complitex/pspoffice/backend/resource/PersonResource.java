package ru.complitex.pspoffice.backend.resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.complitex.common.entity.Gender;
import org.complitex.common.util.AttributeUtil;
import org.complitex.common.util.Locales;
import org.complitex.pspoffice.document.strategy.entity.Document;
import org.complitex.pspoffice.person.strategy.PersonStrategy;
import org.complitex.pspoffice.person.strategy.entity.Person;
import ru.complitex.pspoffice.api.model.DocumentObject;
import ru.complitex.pspoffice.api.model.Name;
import ru.complitex.pspoffice.api.model.PersonObject;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

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

    private List<Name> getPersonNames(Map<Locale, String> map){
        return map.entrySet().stream().map(e -> new Name(Locales.getLocaleId(e.getKey()), e.getValue())).collect(Collectors.toList());
    }

    private PersonObject getPersonObject(Person p){
        PersonObject person = new PersonObject();

        person.setObjectId(p.getObjectId());
        person.setLastNames(getPersonNames(p.getLastNames()));
        person.setFirstNames(getPersonNames(p.getFirstNames()));
        person.setMiddleNames(getPersonNames(p.getMiddleNames()));
        person.setIdentityCode(p.getIdentityCode());
        person.setBirthDate(p.getBirthDate());
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
            document.setTypeId(d.getDocumentTypeId());
            document.setSeries(d.getSeries());
            document.setNumber(d.getNumber());
            document.setOrganization(d.getOrganizationIssued());
            document.setDate(d.getDateIssued());

            documents.add(document);
        }

        person.setCitizenshipId(AttributeUtil.getBooleanValue(p, PersonStrategy.UKRAINE_CITIZENSHIP) ? 2L : null);
        person.setMilitaryServiceRelationId(p.getValueId(PersonStrategy.MILITARY_SERVICE_RELATION));

        return person;
    }

    @GET
    @Path("{id}")
    @ApiOperation(value = "Get person by id", response = PersonObject.class)
    public Response getPerson(@PathParam("id") Long id){
        Person p = personStrategy.getDomainObject(id);

        if (p == null){
            return Response.status(NOT_FOUND).build();
        }

        return Response.ok(getPersonObject(p)).build();
    }

    @GET
    @ApiOperation(value = "Get persons by query", response = PersonObject.class, responseContainer = "List")
    public Response getPersons(@QueryParam("firstName") String firstName,
                               @QueryParam("lastName") String lastName,
                               @QueryParam("middleName") String middleName,
                               @QueryParam("offset") Long offset,
                               @QueryParam("count") Long count){
        return Response.ok(personStrategy.getPersons(lastName, firstName, middleName).stream()
                .map(this::getPersonObject).collect(Collectors.toList())).build();
    }

    @GET
    @Path("size")
    @ApiOperation(value = "Get persons count by query", response = Long.class)
    public Response getPersonsCount(@QueryParam("firstName") String firstName,
                               @QueryParam("lastName") String lastName,
                               @QueryParam("middleName") String middleName){
        return Response.ok(10).build(); //todo count
    }


}
