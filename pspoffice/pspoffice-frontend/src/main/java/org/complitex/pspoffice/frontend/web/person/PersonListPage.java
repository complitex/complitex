package org.complitex.pspoffice.frontend.web.person;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.complitex.pspoffice.frontend.web.BasePage;
import org.complitex.ui.wicket.datatable.TablePanel;
import ru.complitex.pspoffice.api.model.PersonObject;

import java.util.Arrays;

/**
 * @author Anatoly A. Ivanov
 * 27.06.2017 16:26
 */
public class PersonListPage extends BasePage{
    public PersonListPage() {
       add(new TablePanel<>("persons", PersonObject.class,
               Arrays.asList("lastNames[0].name", "firstNames[0].name", "middleNames[0].name"),
               new PersonDataProvider(), 10));
    }

    @Override
    protected IModel<String> getTitleModel() {
        return new ResourceModel("title");
    }
}
