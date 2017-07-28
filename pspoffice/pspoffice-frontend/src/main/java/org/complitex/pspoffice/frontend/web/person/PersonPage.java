package org.complitex.pspoffice.frontend.web.person;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.agilecoders.wicket.jquery.JQuery;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.markup.html.form.*;
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
import ru.complitex.pspoffice.api.model.Name;
import ru.complitex.pspoffice.api.model.PersonObject;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 17.07.2017 14:22
 */
public class PersonPage extends BasePage{
    private static final MetaDataKey<Boolean> ERROR = new MetaDataKey<Boolean>() {};

    @Inject
    private PspOfficeClient pspOfficeClient;

    private IModel<PersonObject> personModel;

    public PersonPage(PageParameters pageParameters) {
        Long personObjectId = pageParameters.get("id").toOptionalLong();

        personModel = Model.of(personObjectId != null ? getPersonObject(personObjectId) : newPersonObject());

        Form<PersonObject> form = new Form<>("form");
        form.setOutputMarkupId(true);
        add(form);

        //Основные

        form.add(new TextField<String>("lastNameRu", new PropertyModel<>(personModel, "lastNames[0].name")));
        form.add(new TextField<String>("firstNameRu", new PropertyModel<>(personModel, "firstNames[0].name")));
        form.add(new TextField<String>("middleNameRu", new PropertyModel<>(personModel, "middleNames[0].name")));

        form.add(new TextField<String>("lastNameUk", new PropertyModel<>(personModel, "lastNames[1].name")));
        form.add(new TextField<String>("firstNameUk", new PropertyModel<>(personModel, "firstNames[1].name")));
        form.add(new TextField<String>("middleNameUk", new PropertyModel<>(personModel, "middleNames[1].name")));

        form.add(new DateTextField("birthDate", new PropertyModel<>(personModel, "birthDate"), "dd.MM.yyyy"));

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
        }).setNullValid(true));

        //Место рождения

        form.add(new TextField<>("birthCountry", new PropertyModel<>(personModel, "birthCountry")));
        form.add(new TextField<>("birthRegion", new PropertyModel<>(personModel, "birthRegion")));
        form.add(new TextField<>("birthCity", new PropertyModel<>(personModel, "birthCity")));
        form.add(new TextField<>("birthDistrict", new PropertyModel<>(personModel, "birthDistrict")));

        //Документ

        form.add(new DropDownChoice<>("documentTypeId", new PropertyModel<>(personModel, "documents[0].typeId"),
                Arrays.asList(1L, 2L, 3L, 4L), new IChoiceRenderer<Long>() {
            @Override
            public Object getDisplayValue(Long object) {
                return object == 1 ? "Паспорт"
                        : object == 2 ? "Свидетельство о рождении"
                        : object == 3 ? "Военный билет"
                        : object == 4 ? "Водительское удостоверение"
                        : null;
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

        form.add(new TextField<>("documentSeries", new PropertyModel<>(personModel, "documents[0].series")));
        form.add(new TextField<>("documentNumbers", new PropertyModel<>(personModel, "documents[0].number")));
        form.add(new TextField<>("documentOrganization", new PropertyModel<>(personModel, "documents[0].organization")));
        form.add(new DateTextField("documentDate", new PropertyModel<>(personModel, "documents[0].date"), "dd.MM.yyyy"));

        form.add(new TextField<>("identityCode", new PropertyModel<>(personModel, "identityCode")));

        form.add(new AjaxSubmitLink("save") {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
               validate(form, target);

                ObjectMapper mapper = new ObjectMapper();

                try {
                    System.out.println(mapper.writeValueAsString(personModel.getObject()));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target) {
                validate(form, target);
            }
        });
    }

    @Override
    protected IModel<String> getTitleModel() {
        return new ResourceModel(personModel.getObject().getObjectId() == null ? "titleNew" : "titleEdit");
    }

    private void validate(Form<?> form, AjaxRequestTarget target){
        form.visitFormComponents((IVisitor<FormComponent<?>, Void>) (component, iVisit) -> {
            if (component.hasErrorMessage()){
                component.setMetaData(ERROR, true);
                target.appendJavaScript(JQuery.$(component).closest(".form-group").chain("addClass('has-error')").build());
            } else if (component.getMetaData(ERROR) != null && component.getMetaData(ERROR)){
                component.setMetaData(ERROR, false);
                target.appendJavaScript(JQuery.$(component).closest(".form-group").chain("removeClass('has-error')").build());
            }
        });
    }

    private PersonObject newPersonObject(){
        PersonObject personObject = new PersonObject();

        personObject.setLastNames(newNames());
        personObject.setFirstNames(newNames());
        personObject.setMiddleNames(newNames());

        personObject.setGender(0);

        personObject.setDocuments(new ArrayList<>());
        personObject.getDocuments().add(new DocumentObject());

        return personObject;
    }

    private PersonObject getPersonObject(Long objectId){
        return pspOfficeClient.request("person/" + objectId).get(PersonObject.class);
    }

    private List<Name> newNames(){
        List<Name> names = new ArrayList<>(2);

        names.add(new Name(1L, ""));
        names.add(new Name(2L, ""));

        return names;
    }
}
