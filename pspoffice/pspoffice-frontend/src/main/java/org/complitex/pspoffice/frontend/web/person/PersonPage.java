package org.complitex.pspoffice.frontend.web.person;

import de.agilecoders.wicket.core.markup.html.bootstrap.form.BootstrapForm;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.pspoffice.frontend.web.BasePage;
import ru.complitex.pspoffice.api.model.Name;
import ru.complitex.pspoffice.api.model.PersonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 17.07.2017 14:22
 */
public class PersonPage extends BasePage{
    public PersonPage(PageParameters pageParameters) {
        IModel<PersonObject> personModel = newPersonModel();

        BootstrapForm form = new BootstrapForm("form");
        add(form);

        form.add(new TextField<String>("lastNameRu", new PropertyModel<>(personModel, "lastNames[0].name")));
        form.add(new TextField<String>("firstNameRu", new PropertyModel<>(personModel, "firstNames[0].name")));
        form.add(new TextField<String>("middleNameRu", new PropertyModel<>(personModel, "middleNames[0].name")));

        form.add(new TextField<String>("lastNameUk", new PropertyModel<>(personModel, "lastNames[1].name")));
        form.add(new TextField<String>("firstNameUk", new PropertyModel<>(personModel, "firstNames[1].name")));
        form.add(new TextField<String>("middleNameUk", new PropertyModel<>(personModel, "middleNames[1].name")));
    }

    private IModel<PersonObject> newPersonModel(){
        PersonObject personObject = new PersonObject();

        personObject.setLastNames(newNames());
        personObject.setFirstNames(newNames());
        personObject.setMiddleNames(newNames());

        return Model.of(personObject);
    }

    private List<Name> newNames(){
        List<Name> names = new ArrayList<>(2);

        names.add(new Name(1L, ""));
        names.add(new Name(2L, ""));

        return names;
    }
}
