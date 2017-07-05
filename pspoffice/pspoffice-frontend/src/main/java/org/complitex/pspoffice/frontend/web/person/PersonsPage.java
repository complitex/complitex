package org.complitex.pspoffice.frontend.web.person;

import org.complitex.pspoffice.frontend.web.BasePage;
import org.complitex.ui.wicket.datatable.TablePanel;
import ru.complitex.pspoffice.api.model.PersonObject;

import java.util.Arrays;

/**
 * @author Anatoly A. Ivanov
 * 27.06.2017 16:26
 */
public class PersonsPage extends BasePage{
    public PersonsPage() {
       add(new TablePanel<>("persons", PersonObject.class, Arrays.asList("lastName[0].name", "firstName[0].name", "middleName[0].name"),
               new PersonsDataProvider(), 10));
    }
}
