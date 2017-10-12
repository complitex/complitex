package org.complitex.pspoffice.frontend.web.person;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.pspoffice.frontend.web.BasePage;
import org.complitex.ui.wicket.datatable.TablePanel;
import ru.complitex.pspoffice.api.model.PersonModel;

import javax.inject.Inject;
import java.util.Arrays;

/**
 * @author Anatoly A. Ivanov
 * 27.06.2017 16:26
 */
public class PersonListPage extends BasePage{
    @Inject
    private PersonDataProvider personDataProvider;

    public PersonListPage() {
        add(new NotificationPanel("feedback"));

        add(new TablePanel<PersonModel>("persons",
                Arrays.asList("lastName.ru", "firstName.ru", "middleName.ru", "birthDate"), personDataProvider,
                PersonPage.class){
            @Override
            protected void populateEdit(IModel<PersonModel> rowModel, PageParameters pageParameters) {
                pageParameters.add("id", rowModel.getObject().getId());
            }
        });

        add(new BootstrapLink<Void>("addPerson", Buttons.Type.Primary) {
            @Override
            public void onClick() {
                setResponsePage(PersonPage.class);

            }
        }.setLabel(new ResourceModel("addPerson")));
    }

    @Override
    protected IModel<String> getTitleModel() {
        return new ResourceModel("title");
    }
}
