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
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.visit.IVisitor;
import org.complitex.pspoffice.frontend.web.BasePage;
import ru.complitex.pspoffice.api.model.Name;
import ru.complitex.pspoffice.api.model.PersonObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 17.07.2017 14:22
 */
public class PersonPage extends BasePage{
    private static final MetaDataKey<Boolean> ERROR = new MetaDataKey<Boolean>() {};

    public PersonPage(PageParameters pageParameters) {
        IModel<PersonObject> personModel = newPersonModel();

        Form<PersonObject> form = new Form<>("form", personModel);
        form.setOutputMarkupId(true);
        add(form);

        form.add(new TextField<String>("lastNameRu", new PropertyModel<>(personModel, "lastNames[0].name")));
        form.add(new TextField<String>("firstNameRu", new PropertyModel<>(personModel, "firstNames[0].name")));
        form.add(new TextField<String>("middleNameRu", new PropertyModel<>(personModel, "middleNames[0].name")));

        form.add(new TextField<String>("lastNameUk", new PropertyModel<>(personModel, "lastNames[1].name")));
        form.add(new TextField<String>("firstNameUk", new PropertyModel<>(personModel, "firstNames[1].name")));
        form.add(new TextField<String>("middleNameUk", new PropertyModel<>(personModel, "middleNames[1].name")));

        form.add(new DateTextField("birthDate", new PropertyModel<>(personModel, "birthDate")));

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

    private IModel<PersonObject> newPersonModel(){
        PersonObject personObject = new PersonObject();

        personObject.setLastNames(newNames());
        personObject.setFirstNames(newNames());
        personObject.setMiddleNames(newNames());

        personObject.setGender(0);

        return Model.of(personObject);
    }

    private List<Name> newNames(){
        List<Name> names = new ArrayList<>(2);

        names.add(new Name(1L, ""));
        names.add(new Name(2L, ""));

        return names;
    }
}
