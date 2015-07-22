package org.complitex.address.web.sync;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.IFilteredColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.TextFilter;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.complitex.address.entity.AddressEntity;
import org.complitex.address.entity.AddressSync;
import org.complitex.address.strategy.street_type.StreetTypeStrategy;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.strategy.IStrategy;
import org.complitex.common.strategy.StrategyFactory;
import org.complitex.common.util.EjbBeanLocator;
import org.complitex.common.web.component.datatable.column.FilteredColumn;

/**
 * @author Anatoly Ivanov
 *         Date: 031 31.07.14 15:44
 */
public class AddressSyncObjectColumn extends FilteredColumn<AddressSync>
        implements IFilteredColumn<AddressSync, String> {
    public AddressSyncObjectColumn(IModel<String> displayModel, String id) {
        super(displayModel, id);
    }

    public AddressSyncObjectColumn(String id) {
        super(new ResourceModel(id), id);
    }

    @Override
    public Component getFilter(String componentId, FilterForm<?> form) {
        return new TextFilter<>(componentId, Model.of(""), form);
    }

    @Override
    public void populateItem(Item<ICellPopulator<AddressSync>> cellItem, String componentId, IModel<AddressSync> rowModel) {
        AddressSync addressSync = rowModel.getObject();

        String objectName = "";

        if (addressSync.getObjectId() != null){
            if (addressSync.getType().equals(AddressEntity.STREET_TYPE)){
                StreetTypeStrategy strategy = EjbBeanLocator.getBean(StreetTypeStrategy.class);

                DomainObject domainObject = strategy.getDomainObject(addressSync.getObjectId(), true);
                objectName = strategy.getName(domainObject) + " (" + strategy.getShortName(domainObject) + ")";
            }else {
                IStrategy strategy = EjbBeanLocator.getBean(StrategyFactory.class).getStrategy(addressSync.getType().getEntityName());

                objectName = strategy.displayDomainObject(addressSync.getObjectId(), cellItem.getLocale());
            }
        }

        cellItem.add(new Label(componentId, Model.of(objectName)));
    }
}
