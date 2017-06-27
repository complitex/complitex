package org.complitex.pspoffice.frontend.web.person;

import org.apache.wicket.markup.html.basic.Label;
import org.complitex.pspoffice.frontend.service.PersonService;
import org.complitex.pspoffice.frontend.web.BasePage;

import javax.inject.Inject;

/**
 * @author Anatoly A. Ivanov
 * 27.06.2017 16:26
 */
public class PersonsPage extends BasePage{
    @Inject
    private PersonService personService;

    public PersonsPage() {
        add(new Label("hello", personService.getPersonObjectsJson()));

    }
}
