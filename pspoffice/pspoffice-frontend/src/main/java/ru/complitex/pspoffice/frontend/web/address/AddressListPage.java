package ru.complitex.pspoffice.frontend.web.address;

import com.google.common.collect.Sets;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapBookmarkablePageLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapLink;
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
import ru.complitex.pspoffice.api.model.AddressModel;
import ru.complitex.pspoffice.frontend.web.BasePage;
import ru.complitex.pspoffice.frontend.web.address.country.CountryPage;
import ru.complitex.pspoffice.frontend.web.component.LinkPanel;
import ru.complitex.pspoffice.frontend.web.component.datatable.TablePanel;
import ru.complitex.pspoffice.frontend.web.component.datatable.column.EditColumn;

import javax.inject.Inject;
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

    @Inject
    private AddressDataProvider addressDataProvider;

    public AddressListPage(PageParameters pageParameters) {
        entity = pageParameters.get("entity").toString();

        if (entity == null || !ENTITIES.contains(entity)){
            throw new RuntimeException("wrong address entity");
        }

        add(new NotificationPanel("feedback").setOutputMarkupId(true));

        addressDataProvider.setEntity(entity);

        add(new TablePanel<AddressModel>("addresses", Arrays.asList("id", "name.ru", "edit"),
                addressDataProvider){
            @Override
            protected IColumn<AddressModel, String> getColumn(String field) {
                if ("id".equals(field)){
                    return new TextFilteredPropertyColumn<AddressModel, String, String>(Model.of("#"), "id", "id"){
                        @Override
                        public String getCssClass() {
                            return "filter-td-id";
                        }
                    };
                }

                if ("edit".equals(field)){
                    return new EditColumn<AddressModel>() {
                        @Override
                        public void populateItem(Item<ICellPopulator<AddressModel>> cellItem, String componentId, IModel<AddressModel> rowModel) {
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

        add(new BootstrapLink<Void>("addAddress", Buttons.Type.Primary) {
            @Override
            public void onClick() {
                if ("country".equals(entity)){
                    setResponsePage(CountryPage.class);
                }
            }
        }.setLabel(new ResourceModel("addAddress")));
    }

    @Override
    protected IModel<String> getTitleModel() {
        return new ResourceModel(entity);
    }
}
