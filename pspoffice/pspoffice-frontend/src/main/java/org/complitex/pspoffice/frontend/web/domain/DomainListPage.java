package org.complitex.pspoffice.frontend.web.domain;

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
import org.complitex.pspoffice.frontend.service.PspOfficeClient;
import org.complitex.pspoffice.frontend.web.BasePage;
import org.complitex.ui.wicket.datatable.TablePanel;
import org.complitex.ui.wicket.datatable.column.EditColumn;
import org.complitex.ui.wicket.link.LinkPanel;
import ru.complitex.pspoffice.api.model.DomainAttributeModel;
import ru.complitex.pspoffice.api.model.DomainModel;
import ru.complitex.pspoffice.api.model.EntityAttributeModel;
import ru.complitex.pspoffice.api.model.EntityModel;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 27.09.2017 15:53
 */
public class DomainListPage extends BasePage{
    @Inject
    private PspOfficeClient pspOfficeClient;

    public DomainListPage(PageParameters pageParameters) {
        String entity = pageParameters.get("entity").toString();

        add(new NotificationPanel("feedback"));

        EntityModel entityModel = pspOfficeClient.request("entity/" + entity).get(EntityModel.class);

        add(new TablePanel<DomainModel>("domains", new DomainDataProvider(pspOfficeClient, entityModel.getEntity())){
            @Override
            protected List<IColumn<DomainModel, String>> getColumns() {
                List<IColumn<DomainModel, String>> columns = new ArrayList<>();

                columns.add(new TextFilteredPropertyColumn<DomainModel, String, String>(
                        new ResourceModel("id"), "id", "id"){
                    @Override
                    public String getCssClass() {
                        return "filter-td-id";
                    }
                });

                entityModel.getAttributes().stream()
                        .filter(EntityAttributeModel::getRequired)
                        .forEach(ea -> {
                            if (ea.getValueTypeId() == 0 || ea.getValueTypeId() == 1){
                                columns.add(new TextFilteredPropertyColumn<DomainModel, String, String>(
                                        Model.of(ea.getNames().get("1")), ea.getId().toString(), ""){
                                    @Override
                                    public IModel<?> getDataModel(IModel<DomainModel> rowModel) {
                                        return rowModel.getObject().getAttributes().stream()
                                                .filter(a -> a.getEntityAttributeId().equals(ea.getId()))
                                                .filter(a -> a.getValues() != null)
                                                .findAny()
                                                .map(a -> a.getValues().get("1"))
                                                .map(Model::of)
                                                .orElse(new Model<>());
                                    }
                                });
                            }

                            if (ea.getValueTypeId() == 0){
                                columns.add(new TextFilteredPropertyColumn<DomainModel, String, String>(
                                        Model.of(ea.getNames().get("2")), ea.getId().toString(), ""){
                                    @Override
                                    public IModel<?> getDataModel(IModel<DomainModel> rowModel) {
                                        return rowModel.getObject().getAttributes().stream()
                                                .filter(a -> a.getEntityAttributeId().equals(ea.getId()))
                                                .filter(a -> a.getValues() != null)
                                                .findAny()
                                                .map(a -> a.getValues().get("2"))
                                                .map(Model::of)
                                                .orElse(new Model<>());
                                    }
                                });
                            }

                            if (ea.getValueTypeId() == 4){
                                columns.add(new TextFilteredPropertyColumn<DomainModel, String, String>(
                                        Model.of(ea.getNames().get("1")), ea.getId().toString(), ""){
                                    @Override
                                    public IModel<?> getDataModel(IModel<DomainModel> rowModel) {
                                        return rowModel.getObject().getAttributes().stream()
                                                .filter(a -> a.getEntityAttributeId().equals(ea.getId()))
                                                .filter(a -> a.getValues() != null)
                                                .findAny()
                                                .map(DomainAttributeModel::getValueId)
                                                .map(Model::of)
                                                .orElse(new Model<>());
                                    }
                                });
                            }
                        });

                columns.add(new EditColumn<DomainModel>(){
                    @Override
                    public void populateItem(Item<ICellPopulator<DomainModel>> cellItem, String componentId, IModel<DomainModel> rowModel) {
                        PageParameters pageParameters = new PageParameters().add("entity", entity).add("id", rowModel.getObject().getId());

                        cellItem.add(new LinkPanel(componentId, new BootstrapBookmarkablePageLink(LinkPanel.LINK_COMPONENT_ID,
                                DomainPage.class, pageParameters, Buttons.Type.Menu) .setIconType(GlyphIconType.edit)
                                .setSize(Buttons.Size.Small)));
                    }
                });

                return columns;
            }
        });

        add(new BootstrapLink<Void>("addDomain", Buttons.Type.Primary) {
            @Override
            public void onClick() {
                setResponsePage(DomainPage.class, new PageParameters().add("entity", entity).add("id", 0));

            }
        }.setLabel(new ResourceModel("addDomain")));
    }
}
