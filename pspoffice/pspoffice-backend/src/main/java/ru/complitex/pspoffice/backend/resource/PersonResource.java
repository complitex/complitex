package ru.complitex.pspoffice.backend.resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.complitex.common.entity.Gender;
import org.complitex.common.util.AttributeUtil;
import org.complitex.common.util.DateUtil;
import org.complitex.pspoffice.document.strategy.DocumentStrategy;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
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
    private DocumentStrategy documentStrategy;

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

        person.setId(p.getObjectId());
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
            document.setId(d.getObjectId());
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
        return Response.ok(personStrategy.getPersonsCount(lastName, firstName, middleName)).build();
    }

    @PUT
    @ApiOperation(value = "Put person")
    public Response putPerson(PersonObject personObject){
        Long id = personObject.getId();

        if (id != null){
            Person person = personStrategy.getDomainObject(id);

            if (person != null){
                updateNames(person, personObject);
                updateInfo(person, personObject);
                updateDocument(person, personObject);

                personStrategy.update(personStrategy.getDomainObject(id), person, DateUtil.getCurrentDate());

                return Response.ok().build();
            }else {
                return Response.status(NOT_FOUND).build();
            }
        }else{
            Person person = personStrategy.newInstance();

            updateNames(person, personObject);
            updateInfo(person, personObject);
            updateDocument(person, personObject);

            personStrategy.insert(person, DateUtil.getCurrentDate());

            return Response.status(CREATED).build();
        }
    }

    private void updateNames(Person person, PersonObject personObject){
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

    private void updateInfo(Person person, PersonObject personObject){
        person.setDateValue(PersonStrategy.BIRTH_DATE, personObject.getBirthDate());
        person.setStringValue(PersonStrategy.GENDER, personObject.getGender() == 1 ? Gender.MALE.name() : Gender.FEMALE.name());
        person.setBooleanValue(PersonStrategy.UKRAINE_CITIZENSHIP, personObject.getCitizenshipId() == 2);
        person.setStringValue(PersonStrategy.IDENTITY_CODE, person.getIdentityCode());

        person.setStringValue(PersonStrategy.BIRTH_COUNTRY, personObject.getBirthCountry());
        person.setStringValue(PersonStrategy.BIRTH_REGION, personObject.getBirthRegion());
        person.setStringValue(PersonStrategy.BIRTH_CITY, personObject.getBirthCity());
        person.setStringValue(PersonStrategy.BIRTH_DISTRICT, personObject.getBirthDistrict());
    }

    private void updateDocument(Person person, PersonObject personObject){
        if (personObject.getDocuments() == null){
            return;
        }

        Document document = person.getDocument();
        DocumentObject documentObject = personObject.getDocuments().get(0);

        if (document == null){
            document = documentStrategy.newInstance(documentObject.getTypeId());
            person.setDocument(document);
        }

        document.setValue(DocumentStrategy.DOCUMENT_TYPE, documentObject.getTypeId());
        document.setStringValue(DocumentStrategy.DOCUMENT_SERIES, documentObject.getSeries());
        document.setStringValue(DocumentStrategy.DOCUMENT_NUMBER, documentObject.getNumber());
        document.setStringValue(DocumentStrategy.ORGANIZATION_ISSUED, documentObject.getOrganization());
        document.setDateValue(DocumentStrategy.DATE_ISSUED, documentObject.getDate());
    }


}
