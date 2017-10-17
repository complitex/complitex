package ru.complitex.pspoffice.backend.resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.complitex.common.entity.DomainObjectFilter;
import org.complitex.common.entity.Gender;
import org.complitex.common.util.AttributeUtil;
import org.complitex.pspoffice.document.strategy.DocumentStrategy;
import org.complitex.pspoffice.document.strategy.entity.Document;
import org.complitex.pspoffice.person.strategy.PersonStrategy;
import org.complitex.pspoffice.person.strategy.entity.Person;
import org.complitex.pspoffice.person.strategy.entity.PersonName;
import org.complitex.pspoffice.person.strategy.service.PersonNameBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.complitex.pspoffice.api.model.DocumentModel;
import ru.complitex.pspoffice.api.model.PersonModel;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.*;
import static org.complitex.common.util.Locales.RU;
import static org.complitex.common.util.Locales.UA;

/**
 * @author Anatoly A. Ivanov
 *         24.05.2017 17:41
 */
@Path("person")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Api(description = "Person API")
public class PersonResource {
    private Logger log = LoggerFactory.getLogger(PersonResource.class);

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

    private PersonModel getPersonObject(Person p){
        PersonModel person = new PersonModel();

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
            List<DocumentModel> documents = new ArrayList<>();
            person.setDocuments(documents);

            Document d = p.getDocument();

            DocumentModel document = new DocumentModel();
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
    @ApiOperation(value = "Get person by id", response = PersonModel.class)
    public Response getPerson(@PathParam("id") Long id){
        Person p = personStrategy.getDomainObject(id);

        if (p == null){
            return Response.status(NOT_FOUND).build();
        }

        return Response.ok(getPersonObject(p)).build();
    }

    @GET
    @ApiOperation(value = "Get persons by query", response = PersonModel.class, responseContainer = "List")
    public Response getPersons(@QueryParam("firstName") String firstName,
                               @QueryParam("lastName") String lastName,
                               @QueryParam("middleName") String middleName,
                               @QueryParam("offset") Long offset,
                               @QueryParam("limit") Long limit){
        return Response.ok(personStrategy.getList(new DomainObjectFilter()).stream()
                .map(this::getPersonObject).collect(Collectors.toList())).build();
    }

    @GET
    @Path("size")
    @ApiOperation(value = "Get persons count by query", response = Long.class)
    public Response getPersonsCount(@QueryParam("firstName") String firstName,
                               @QueryParam("lastName") String lastName,
                               @QueryParam("middleName") String middleName){
        return Response.ok(personStrategy.getCount(new DomainObjectFilter())).build();
    }

    @PUT
    @ApiOperation(value = "Put person")
    public Response putPerson(PersonModel personModel){
        try {
            Long id = personModel.getId();

            if (id != null){
                Person person = personStrategy.getDomainObject(id);

                if (person != null){
                    updateNames(person, personModel);
                    updateInfo(person, personModel);
                    updateDocument(person, personModel);

                    personStrategy.update(person);

                    return Response.ok().build();
                }else {
                    return Response.status(NOT_FOUND).build();
                }
            }else{
                Person person = personStrategy.newInstance();

                updateNames(person, personModel);
                updateInfo(person, personModel);
                updateDocument(person, personModel);

                personStrategy.insert(person);

                return Response.status(CREATED).build();
            }
        } catch (Exception e) {
            log.error("put person error", e);

            return Response.status(INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    private void updateNames(Person person, PersonModel personModel){
        person.getAttribute(PersonStrategy.LAST_NAME, 1L)
                .setValueId(personNameBean.findOrSave(PersonName.PersonNameType.LAST_NAME,
                        personModel.getLastName().get(RU.getLanguage()), RU, true).getId());

        if (personModel.getLastName().get(UA.getLanguage()) != null) {
            person.getAttribute(PersonStrategy.LAST_NAME, 2L)
                    .setValueId(personNameBean.findOrSave(PersonName.PersonNameType.LAST_NAME,
                            personModel.getLastName().get(UA.getLanguage()), UA, true).getId());
        }else{
            person.getAttribute(PersonStrategy.LAST_NAME, 2L).setValueId(null);
        }

        person.getAttribute(PersonStrategy.FIRST_NAME, 1L)
                .setValueId(personNameBean.findOrSave(PersonName.PersonNameType.FIRST_NAME,
                        personModel.getFirstName().get(RU.getLanguage()), RU, true).getId());


        if (personModel.getFirstName().get(UA.getLanguage()) != null) {
            person.getAttribute(PersonStrategy.FIRST_NAME, 2L)
                    .setValueId(personNameBean.findOrSave(PersonName.PersonNameType.FIRST_NAME,
                            personModel.getFirstName().get(UA.getLanguage()), UA, true).getId());
        }else{
            person.getAttribute(PersonStrategy.FIRST_NAME, 2L).setValueId(null);
        }

        person.getAttribute(PersonStrategy.MIDDLE_NAME, 1L)
                .setValueId(personNameBean.findOrSave(PersonName.PersonNameType.MIDDLE_NAME,
                        personModel.getMiddleName().get(RU.getLanguage()), RU, true).getId());

        if (personModel.getMiddleName().get(UA.getLanguage()) != null) {
            person.getAttribute(PersonStrategy.MIDDLE_NAME, 2L)
                    .setValueId(personNameBean.findOrSave(PersonName.PersonNameType.MIDDLE_NAME,
                            personModel.getMiddleName().get(UA.getLanguage()), UA, true).getId());
        }else{
            person.getAttribute(PersonStrategy.MIDDLE_NAME, 2L).setValueId(null);
        }
    }

    private void updateInfo(Person person, PersonModel personModel){
        person.setDateValue(PersonStrategy.BIRTH_DATE, personModel.getBirthDate());
        person.setStringValue(PersonStrategy.GENDER, personModel.getGender() == 1 ? Gender.MALE.name() : Gender.FEMALE.name());
        person.setBooleanValue(PersonStrategy.UKRAINE_CITIZENSHIP, personModel.getCitizenshipId() != null && personModel.getCitizenshipId() == 2);
        person.setStringValue(PersonStrategy.IDENTITY_CODE, person.getIdentityCode());

        person.setStringValue(PersonStrategy.BIRTH_COUNTRY, personModel.getBirthCountry());
        person.setStringValue(PersonStrategy.BIRTH_REGION, personModel.getBirthRegion());
        person.setStringValue(PersonStrategy.BIRTH_CITY, personModel.getBirthCity());
        person.setStringValue(PersonStrategy.BIRTH_DISTRICT, personModel.getBirthDistrict());
    }

    private void updateDocument(Person person, PersonModel personModel){
        if (personModel.getDocuments() == null){
            return;
        }

        Document document = person.getDocument();
        DocumentModel documentModel = personModel.getDocuments().get(0);

        if (document == null){
            document = documentStrategy.newInstance(documentModel.getTypeId());
            person.setDocument(document);
        }

        document.setValue(DocumentStrategy.DOCUMENT_TYPE, documentModel.getTypeId());
        document.setStringValue(DocumentStrategy.DOCUMENT_SERIES, documentModel.getSeries());
        document.setStringValue(DocumentStrategy.DOCUMENT_NUMBER, documentModel.getNumber());
        document.setStringValue(DocumentStrategy.ORGANIZATION_ISSUED, documentModel.getOrganization());
        document.setDateValue(DocumentStrategy.DATE_ISSUED, documentModel.getDate());
    }


}
