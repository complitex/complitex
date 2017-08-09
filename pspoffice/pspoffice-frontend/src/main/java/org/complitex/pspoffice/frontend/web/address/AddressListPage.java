package org.complitex.pspoffice.frontend.web.address;

import com.google.common.collect.Sets;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapBookmarkablePageLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.GlyphIconType;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.TextFilteredPropertyColumn;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.pspoffice.frontend.web.BasePage;
import org.complitex.pspoffice.frontend.web.address.country.CountryPage;
import org.complitex.ui.wicket.datatable.TablePanel;
import org.complitex.ui.wicket.datatable.column.EditColumn;
import org.complitex.ui.wicket.link.LinkPanel;
import ru.complitex.pspoffice.api.model.AddressObject;

import java.util.Arrays;
import java.util.Set;

/**
 * @author Anatoly A. Ivanov
 * 20.07.2017 12:37
 */
public class AddressListPage extends BasePage{
    private static final Set<String> ENTITIES = Sets.newHashSet(
            "country", "region", "city", "district","street", "building", "apartment"
    );

    private String entity;

    public AddressListPage(PageParameters pageParameters) {
        entity = pageParameters.get("entity").toString();

        if (entity == null || !ENTITIES.contains(entity)){
            throw new RuntimeException("wrong address entity");
        }

        add(new NotificationPanel("feedback").setOutputMarkupId(true));

        add(new TablePanel<AddressObject>("addresses", AddressObject.class, Arrays.asList("id", "name.ru", "edit"),
                new AddressDataProvider(entity)){
            @Override
            protected IColumn<AddressObject, String> getColumn(String field) {
                if ("id".equals(field)){
                    return new TextFilteredPropertyColumn<AddressObject, String, String>(Model.of("#"), "id", "id"){
                        @Override
                        public String getCssClass() {
                            return "filter-td-id";
                        }
                    };
                }

                if ("edit".equals(field)){
                    return new EditColumn<AddressObject>() {
                        @Override
                        public void populateItem(Item<ICellPopulator<AddressObject>> cellItem, String componentId, IModel<AddressObject> rowModel) {
                            if ("country".equals(entity)) {
                                cellItem.add(new LinkPanel(componentId, new BootstrapBookmarkablePageLink(LinkPanel.LINK_COMPONENT_ID, CountryPage.class,
                                        new PageParameters().add("id", rowModel.getObject().getId()),
                                        Buttons.Type.Menu).setIconType(GlyphIconType.edit).setSize(Buttons.Size.Small)));
                            }else {
                                cellItem.add(new LinkPanel(componentId, new BootstrapBookmarkablePageLink(LinkPanel.LINK_COMPONENT_ID, AddressListPage.class,
                                        new PageParameters().add("entity", entity),
                                        Buttons.Type.Menu).setIconType(GlyphIconType.edit).setSize(Buttons.Size.Small)));
                            }
                        }

                        @Override
                        public String getCssClass() {
                            return "filter-td-edit";
                        }
                    };
                }

                return super.getColumn(field);
            }
        });
    }

    @Override
    protected IModel<String> getTitleModel() {
        return new ResourceModel(entity);
    }
}
