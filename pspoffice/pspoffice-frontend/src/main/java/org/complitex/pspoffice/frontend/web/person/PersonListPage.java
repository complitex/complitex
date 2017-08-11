package org.complitex.pspoffice.frontend.web.person;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapBookmarkablePageLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.GlyphIconType;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.pspoffice.frontend.web.BasePage;
import org.complitex.ui.wicket.datatable.TablePanel;
import org.complitex.ui.wicket.datatable.column.EditColumn;
import org.complitex.ui.wicket.link.LinkPanel;
import ru.complitex.pspoffice.api.model.PersonObject;

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

        add(new TablePanel<PersonObject>("persons", PersonObject.class,
                Arrays.asList("lastName.ru", "firstName.ru", "middleName.ru", "birthDate", "edit"), personDataProvider){
            @Override
            protected IColumn<PersonObject, String> getColumn(String field) {
                if ("edit".equals(field)){
                    return new EditColumn<PersonObject>(){
                        @Override
                        public void populateItem(Item<ICellPopulator<PersonObject>> cellItem, String componentId, IModel<PersonObject> rowModel) {
                            cellItem.add(new LinkPanel(componentId, new BootstrapBookmarkablePageLink(LinkPanel.LINK_COMPONENT_ID, PersonPage.class,
                                    new PageParameters().add("id", rowModel.getObject().getId()), Buttons.Type.Menu)
                                    .setIconType(GlyphIconType.edit).setSize(Buttons.Size.Small)));
                        }
                    };
                }

                return super.getColumn(field);
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
