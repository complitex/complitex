package org.complitex.pspoffice.frontend.web.person;

import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.Strings;
import org.complitex.pspoffice.frontend.service.PspOfficeClient;
import org.complitex.pspoffice.frontend.web.FormPage;
import ru.complitex.pspoffice.api.model.DocumentModel;
import ru.complitex.pspoffice.api.model.NameModel;
import ru.complitex.pspoffice.api.model.PersonModel;

import javax.inject.Inject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.util.*;

/**
 * @author Anatoly A. Ivanov
 * 17.07.2017 14:22
 */
public class PersonPage extends FormPage{
    @Inject
    private PspOfficeClient pspOfficeClient;

    private IModel<PersonModel> personModel;

    public PersonPage(PageParameters pageParameters) {
        Long id = pageParameters.get("id").toLongObject();

        personModel = Model.of(id > 0 ? getPersonModel(id) : newPersonModel());

        //Основные

        getForm().add(new TextField<String>("lastNameRu", new PropertyModel<>(personModel, "lastName.ru")).setRequired(true));
        getForm().add(new TextField<String>("firstNameRu", new PropertyModel<>(personModel, "firstName.ru")).setRequired(true));
        getForm().add(new TextField<String>("middleNameRu", new PropertyModel<>(personModel, "middleName.ru")).setRequired(true));

        getForm().add(new TextField<String>("lastNameUk", new PropertyModel<>(personModel, "lastName.uk")));
        getForm().add(new TextField<String>("firstNameUk", new PropertyModel<>(personModel, "firstName.uk")));
        getForm().add(new TextField<String>("middleNameUk", new PropertyModel<>(personModel, "middleName.uk")));

        getForm().add(new DateTextField("birthDate", new PropertyModel<>(personModel, "birthDate"), "dd.MM.yyyy").setRequired(true));

        getForm().add(new DropDownChoice<>("gender", new PropertyModel<>(personModel, "gender"),
                Arrays.asList(0, 1), new IChoiceRenderer<Integer>() {
            @Override
            public Object getDisplayValue(Integer object) {
                return object == 0 ? "Женский" : "Мужской";
            }

            @Override
            public String getIdValue(Integer object, int index) {
                return object.toString();
            }

            @Override
            public Integer getObject(String id, IModel<? extends List<? extends Integer>> choices) {
                return Integer.valueOf(id);
            }
        }).setRequired(true));

        getForm().add(new DropDownChoice<>("citizenship", new PropertyModel<>(personModel, "citizenshipId"),
                Collections.singletonList(2L), new IChoiceRenderer<Long>() {
            @Override
            public Object getDisplayValue(Long object) {
                return object == 1 ? "Российское" : object == 2 ? "Украинское" : "Другое";
            }

            @Override
            public String getIdValue(Long object, int index) {
                return object.toString();
            }

            @Override
            public Long getObject(String id, IModel<? extends List<? extends Long>> choices) {
                return !Strings.isEmpty(id) ? Long.valueOf(id) : null;
            }
        }).setNullValid(true));

        //Место рождения

        getForm().add(new TextField<>("birthCountry", new PropertyModel<>(personModel, "birthCountry")));
        getForm().add(new TextField<>("birthRegion", new PropertyModel<>(personModel, "birthRegion")));
        getForm().add(new TextField<>("birthCity", new PropertyModel<>(personModel, "birthCity")));
        getForm().add(new TextField<>("birthDistrict", new PropertyModel<>(personModel, "birthDistrict")));

        //Документ

        List<NameModel> documentTypes = getDocumentTypes();

        getForm().add(new DropDownChoice<>("documentTypeId",
                new IModel<NameModel>() {
                    @Override
                    public NameModel getObject() {
                        return documentTypes.stream()
                                .filter(o -> o.getId().equals(getDocumentObject().getTypeId()))
                                .findAny()
                                .orElse(null);
                    }

                    @Override
                    public void setObject(NameModel nameModel) {
                        getDocumentObject().setTypeId(nameModel.getId());
                    }

                    private DocumentModel getDocumentObject(){
                        return personModel.getObject().getDocuments().get(0);
                    }
                },

                documentTypes,
                new ChoiceRenderer<>("name.ru", "id")
        ).setNullValid(true).setRequired(true));

        getForm().add(new TextField<>("documentSeries", new PropertyModel<>(personModel, "documents[0].series")).setRequired(true));
        getForm().add(new TextField<>("documentNumbers", new PropertyModel<>(personModel, "documents[0].number")).setRequired(true));
        getForm().add(new TextField<>("documentOrganization", new PropertyModel<>(personModel, "documents[0].organization")));
        getForm().add(new DateTextField("documentDate", new PropertyModel<>(personModel, "documents[0].date"), "dd.MM.yyyy"));

        getForm().add(new TextField<>("identityCode", new PropertyModel<>(personModel, "identityCode")));

        setReturnPage(PersonListPage.class);
    }

    @Override
    protected Response put() {
        return pspOfficeClient.request("person").put(Entity.json(personModel.getObject()));
    }

    @Override
    protected IModel<String> getTitleModel() {
        return new ResourceModel(personModel.getObject().getId() == null ? "titleNew" : "titleEdit");
    }

    private PersonModel newPersonModel(){
        PersonModel personModel = new PersonModel();

        personModel.setLastName(new HashMap<>());
        personModel.setFirstName(new HashMap<>());
        personModel.setMiddleName(new HashMap<>());

        personModel.setGender(1);

        personModel.setDocuments(new ArrayList<>());
        personModel.getDocuments().add(new DocumentModel());

        return personModel;
    }


    private PersonModel getPersonModel(Long objectId){
        PersonModel personModel = pspOfficeClient.request("person/" + objectId).get(PersonModel.class);

        if (personModel.getDocuments() == null){
            personModel.setDocuments(new ArrayList<>());
            personModel.getDocuments().add(new DocumentModel());
        }

        return personModel;
    }

    private List<NameModel> getDocumentTypes(){
        return pspOfficeClient.request("dictionary/document-type").get(new GenericType<List<NameModel>>(){});
    }
}
