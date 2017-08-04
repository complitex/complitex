package org.complitex.pspoffice.frontend.web.person;

import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.agilecoders.wicket.jquery.JQuery;
import de.agilecoders.wicket.jquery.function.Function;
import org.apache.wicket.Component;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.util.visit.IVisitor;
import org.complitex.pspoffice.frontend.service.PspOfficeClient;
import org.complitex.pspoffice.frontend.web.BasePage;
import ru.complitex.pspoffice.api.model.DocumentObject;
import ru.complitex.pspoffice.api.model.NameObject;
import ru.complitex.pspoffice.api.model.PersonObject;

import javax.inject.Inject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.util.*;

import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.OK;

/**
 * @author Anatoly A. Ivanov
 * 17.07.2017 14:22
 */
public class PersonPage extends BasePage{
    private static final MetaDataKey<Boolean> ERROR = new MetaDataKey<Boolean>() {};

    @Inject
    private PspOfficeClient pspOfficeClient;

    private IModel<PersonObject> personModel;

    private Component feedback;

    public PersonPage(PageParameters pageParameters) {
        Long personObjectId = pageParameters.get("id").toOptionalLong();

        personModel = Model.of(personObjectId != null ? getPersonObject(personObjectId) : newPersonObject());

        add(feedback = new NotificationPanel("feedback").setOutputMarkupId(true));

        Form<PersonObject> form = new Form<>("form");
        form.setOutputMarkupId(true);
        add(form);

        //Основные

        form.add(new TextField<String>("lastNameRu", new PropertyModel<>(personModel, "lastName.ru")).setRequired(true));
        form.add(new TextField<String>("firstNameRu", new PropertyModel<>(personModel, "firstName.ru")).setRequired(true));
        form.add(new TextField<String>("middleNameRu", new PropertyModel<>(personModel, "middleName.ru")).setRequired(true));

        form.add(new TextField<String>("lastNameUk", new PropertyModel<>(personModel, "lastName.uk")));
        form.add(new TextField<String>("firstNameUk", new PropertyModel<>(personModel, "firstName.uk")));
        form.add(new TextField<String>("middleNameUk", new PropertyModel<>(personModel, "middleName.uk")));

        form.add(new DateTextField("birthDate", new PropertyModel<>(personModel, "birthDate"), "dd.MM.yyyy").setRequired(true));

        form.add(new DropDownChoice<>("gender", new PropertyModel<>(personModel, "gender"),
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

        form.add(new DropDownChoice<>("citizenship", new PropertyModel<>(personModel, "citizenshipId"),
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
        }).setNullValid(true).setRequired(true));

        //Место рождения

        form.add(new TextField<>("birthCountry", new PropertyModel<>(personModel, "birthCountry")));
        form.add(new TextField<>("birthRegion", new PropertyModel<>(personModel, "birthRegion")));
        form.add(new TextField<>("birthCity", new PropertyModel<>(personModel, "birthCity")));
        form.add(new TextField<>("birthDistrict", new PropertyModel<>(personModel, "birthDistrict")));

        //Документ

        List<NameObject> documentTypes = getDocumentTypes();

        form.add(new DropDownChoice<>("documentTypeId",
                new IModel<NameObject>() {
                    @Override
                    public NameObject getObject() {
                        return documentTypes.stream()
                                .filter(o -> o.getId().equals(getDocumentObject().getTypeId()))
                                .findAny()
                                .orElse(null);
                    }

                    @Override
                    public void setObject(NameObject nameObject) {
                        getDocumentObject().setTypeId(nameObject.getId());
                    }

                    private DocumentObject getDocumentObject(){
                        return personModel.getObject().getDocuments().get(0);
                    }
                },

                documentTypes,
                new ChoiceRenderer<>("name.ru", "id")
        ).setNullValid(true).setRequired(true));

        form.add(new TextField<>("documentSeries", new PropertyModel<>(personModel, "documents[0].series")).setRequired(true));
        form.add(new TextField<>("documentNumbers", new PropertyModel<>(personModel, "documents[0].number")).setRequired(true));
        form.add(new TextField<>("documentOrganization", new PropertyModel<>(personModel, "documents[0].organization")));
        form.add(new DateTextField("documentDate", new PropertyModel<>(personModel, "documents[0].date"), "dd.MM.yyyy"));

        form.add(new TextField<>("identityCode", new PropertyModel<>(personModel, "identityCode")));

        form.add(new AjaxSubmitLink("save") {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
               validate(form, target);

               Response response = PspOfficeClient.get()
                       .request("person")
                       .put(Entity.json(personModel.getObject()));

               if (response.getStatus() == CREATED.getStatusCode()){
                   getSession().info("Запись добавлена");
                   setResponsePage(PersonListPage.class);
               } else if (response.getStatus() == OK.getStatusCode()){
                   getSession().info("Запись обновлена");
                   setResponsePage(PersonListPage.class);
               }else {
                   error(response.readEntity(String.class));
                   target.add(feedback);
               }
            }

            @Override
            protected void onError(AjaxRequestTarget target) {
                validate(form, target);
            }
        });

        form.add(new Link<Void>("cancel") {
            @Override
            public void onClick() {
                setResponsePage(PersonListPage.class);
            }
        });
    }

    @Override
    protected IModel<String> getTitleModel() {
        return new ResourceModel(personModel.getObject().getId() == null ? "titleNew" : "titleEdit");
    }

    private void validate(Form<?> form, AjaxRequestTarget target){
        form.visitFormComponents((IVisitor<FormComponent<?>, Void>) (component, iVisit) -> {
            if (!component.isValid()){
                component.setMetaData(ERROR, true);
                target.appendJavaScript(JQuery.$(component).closest(".form-group").chain(new Function("addClass", "has-error")).build());
            } else if (component.getMetaData(ERROR) != null && component.getMetaData(ERROR)){
                component.setMetaData(ERROR, false);
                target.appendJavaScript(JQuery.$(component).closest(".form-group").chain(new Function("addClass", "has-error")).build());
            }
        });
    }

    private PersonObject newPersonObject(){
        PersonObject personObject = new PersonObject();

        personObject.setLastName(new HashMap<>());
        personObject.setFirstName(new HashMap<>());
        personObject.setMiddleName(new HashMap<>());

        personObject.setGender(0);

        personObject.setDocuments(new ArrayList<>());
        personObject.getDocuments().add(new DocumentObject());

        return personObject;
    }


    private PersonObject getPersonObject(Long objectId){
        PersonObject personObject = pspOfficeClient.request("person/" + objectId).get(PersonObject.class);

        if (personObject.getDocuments() == null){
            personObject.setDocuments(new ArrayList<>());
            personObject.getDocuments().add(new DocumentObject());
        }

        return personObject;
    }

    private List<NameObject> getDocumentTypes(){
        return pspOfficeClient.request("dictionary/document-type").get(new GenericType<List<NameObject>>(){});
    }
}
