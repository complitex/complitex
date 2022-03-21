package ru.complitex.pspoffice.frontend.web.card;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.TextFilteredPropertyColumn;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.pspoffice.frontend.web.BasePage;
import ru.complitex.pspoffice.frontend.web.person.PersonPage;
import ru.complitex.pspoffice.frontend.web.component.datatable.TablePanel;
import ru.complitex.pspoffice.api.model.ApartmentCardModel;

import javax.inject.Inject;
import java.util.Arrays;

/**
 * @author Anatoly A. Ivanov
 * 26.10.2017 16:58
 */
public class ApartmentCardListPage extends BasePage{
    @Inject
    private ApartmentCardDataProvider apartmentCardDataProvider;

    public ApartmentCardListPage() {
        add(new NotificationPanel("feedback"));

        add(new TablePanel<ApartmentCardModel>("cards",
                Arrays.asList("owner.lastName.ru", "owner.firstName.ru", "owner.middleName.ru", "ownershipForm", "registrations"),
                apartmentCardDataProvider, ApartmentCardListPage.class){
            @Override
            protected void populateEdit(IModel<ApartmentCardModel> rowModel, PageParameters pageParameters) {
//                pageParameters.add("id", rowModel.getObject().getId());
            }

            @Override
            protected IColumn<ApartmentCardModel, String> getColumn(String field) {
                switch (field){
                    case "ownershipForm":
                        return new TextFilteredPropertyColumn<ApartmentCardModel, String, String>(new ResourceModel("ownershipForm"), "ownershipForm","ownershipForm.attributes.0.values.1");
                    case "registrations":
                        return new TextFilteredPropertyColumn<ApartmentCardModel, String, String>(new ResourceModel("registrations"), "registrations", "registrations"){
                            @Override
                            public IModel<?> getDataModel(IModel<ApartmentCardModel> rowModel) {
                                return Model.of(rowModel.getObject().getRegistrations().size());
                            }
                        };
                }

                return super.getColumn(field);
            }
        });

        add(new BootstrapLink<Void>("addApartmentCard", Buttons.Type.Primary) {
            @Override
            public void onClick() {
                setResponsePage(PersonPage.class, new PageParameters().add("id", 0));

            }
        }.setLabel(new ResourceModel("addApartmentCard")));

    }

    @Override
    protected IModel<String> getTitleModel() {
        return new ResourceModel("title");
    }
}
