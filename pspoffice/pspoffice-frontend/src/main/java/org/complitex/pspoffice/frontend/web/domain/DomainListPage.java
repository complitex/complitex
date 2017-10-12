package org.complitex.pspoffice.frontend.web.domain;

import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.TextFilteredPropertyColumn;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.pspoffice.frontend.service.PspOfficeClient;
import org.complitex.pspoffice.frontend.web.BasePage;
import org.complitex.ui.wicket.datatable.TablePanel;
import ru.complitex.pspoffice.api.model.DomainModel;
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
        Long entityId = pageParameters.get("id").toLongObject();

        add(new NotificationPanel("feedback"));

        EntityModel entity = pspOfficeClient.request("entity/" + entityId).get(EntityModel.class);

        add(new TablePanel<DomainModel>("domains", new DomainDataProvider(pspOfficeClient, entity.getEntity())){
            @Override
            protected List<IColumn<DomainModel, String>> getColumns() {
                List<IColumn<DomainModel, String>> columns = new ArrayList<>();

                columns.add(new TextFilteredPropertyColumn<DomainModel, String, String>(
                        new ResourceModel("objectId"), "objectId", "objectId"));

                entity.getAttributes().forEach(ea -> {
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
                });

                return columns;
            }
        });
    }
}
