package ru.complitex.pspoffice.backend.resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.complitex.common.entity.Gender;
import org.complitex.common.util.AttributeUtil;
import org.complitex.pspoffice.document.strategy.entity.Document;
import org.complitex.pspoffice.person.strategy.PersonStrategy;
import org.complitex.pspoffice.person.strategy.entity.Person;
import org.complitex.pspoffice.person.strategy.entity.PersonName;
import org.complitex.pspoffice.person.strategy.service.PersonNameBean;
import ru.complitex.pspoffice.api.model.DocumentObject;
import ru.complitex.pspoffice.api.model.PersonObject;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.*;
import java.util.stream.Collectors;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.*;
import static org.complitex.common.util.Locales.getLanguage;
import static org.complitex.common.util.Locales.getLocale;

/**
 * @author Anatoly A. Ivanov
 *         24.05.2017 17:41
 */
@Path("person")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Api(description = "Person API")
public class PersonResource {

    @EJB
    private PersonStrategy personStrategy;

    @EJB
    private PersonNameBean personNameBean;

    @GET
    @Path("ping")
    public Response ping(){
        return Response.ok("pong").build();
    }

    private Map<String, String> getPersonNames(Map<Locale, String> map){
        return map.entrySet().stream().collect(Collectors.toMap(e -> e.getKey().getLanguage(), Map.Entry::getValue));
    }

    private PersonObject getPersonObject(Person p){
        PersonObject person = new PersonObject();

        person.setObjectId(p.getObjectId());
        person.setLastName(getPersonNames(p.getLastNames()));
        person.setFirstName(getPersonNames(p.getFirstNames()));
        person.setMiddleName(getPersonNames(p.getMiddleNames()));
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
        return Response.ok(personStrategy.getPersons(lastName, firstName, middleName).stream() //todo order
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

    @PUT
    @ApiOperation(value = "Put person")
    public Response putPerson(PersonObject personObject){
        Long objectId = personObject.getObjectId();

        if (objectId != null){
            Person person = personStrategy.getDomainObject(objectId);

            if (person != null){

                return Response.status(NOT_IMPLEMENTED).entity("Not implemented").build();
            }else {
                return Response.status(NOT_FOUND).build();
            }
        }else{
            Person person = personStrategy.newInstance();

            updateNames(personObject, person);

            personStrategy.insert(person, new Date());

            return Response.status(CREATED).build();
        }
    }

    private void updateNames(PersonObject personObject, Person person){
        person.getAttribute(PersonStrategy.LAST_NAME, 1L)
                .setValueId(personNameBean.findOrSave(PersonName.PersonNameType.LAST_NAME,
                        personObject.getLastName().get(getLanguage(1L)), getLocale(1L), true).getId());

        if (personObject.getLastName().get(getLanguage(2L)) != null) {
            person.getAttribute(PersonStrategy.LAST_NAME, 2L)
                    .setValueId(personNameBean.findOrSave(PersonName.PersonNameType.LAST_NAME,
                            personObject.getLastName().get(getLanguage(2L)), getLocale(2L), true).getId());
        }else{
            person.getAttribute(PersonStrategy.LAST_NAME, 2L).setValueId(null);
        }

        person.getAttribute(PersonStrategy.FIRST_NAME, 1L)
                .setValueId(personNameBean.findOrSave(PersonName.PersonNameType.FIRST_NAME,
                        personObject.getFirstName().get(getLanguage(1L)), getLocale(1L), true).getId());


        if (personObject.getFirstName().get(getLanguage(2L)) != null) {
            person.getAttribute(PersonStrategy.FIRST_NAME, 2L)
                    .setValueId(personNameBean.findOrSave(PersonName.PersonNameType.FIRST_NAME,
                            personObject.getFirstName().get(getLanguage(2L)), getLocale(2L), true).getId());
        }else{
            person.getAttribute(PersonStrategy.FIRST_NAME, 2L).setValueId(null);
        }

        person.getAttribute(PersonStrategy.MIDDLE_NAME, 1L)
                .setValueId(personNameBean.findOrSave(PersonName.PersonNameType.MIDDLE_NAME,
                        personObject.getMiddleName().get(getLanguage(1L)), getLocale(1L), true).getId());

        if (personObject.getMiddleName().get(getLanguage(2L)) != null) {
            person.getAttribute(PersonStrategy.MIDDLE_NAME, 2L)
                    .setValueId(personNameBean.findOrSave(PersonName.PersonNameType.MIDDLE_NAME,
                            personObject.getMiddleName().get(getLanguage(2L)), getLocale(2L), true).getId());
        }else{
            person.getAttribute(PersonStrategy.MIDDLE_NAME, 2L).setValueId(null);
        }
    }


}
