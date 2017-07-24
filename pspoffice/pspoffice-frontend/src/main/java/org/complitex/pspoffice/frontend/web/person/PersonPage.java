package org.complitex.pspoffice.frontend.web.person;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.pspoffice.frontend.web.BasePage;
import ru.complitex.pspoffice.api.model.PersonObject;

/**
 * @author Anatoly A. Ivanov
 * 17.07.2017 14:22
 */
public class PersonPage extends BasePage{
    public PersonPage(PageParameters pageParameters) {
        PersonObject personObject = newPersonObject();

        Form form = new Form("form");
        add(form);

    }

    private PersonObject newPersonObject(){
        PersonObject personObject = new PersonObject();

        return personObject;
    }
}
