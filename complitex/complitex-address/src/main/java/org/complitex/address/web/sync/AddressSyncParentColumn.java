package org.complitex.address.web.sync;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.TextFilter;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.complitex.address.entity.AddressSync;
import org.complitex.address.entity.SyncEntity;
import org.complitex.address.strategy.city.CityStrategy;
import org.complitex.address.strategy.street.StreetStrategy;
import org.complitex.common.util.EjbBeanLocator;
import org.complitex.common.web.component.datatable.column.FilteredColumn;

/**
 * @author Anatoly Ivanov
 *         Date: 031 31.07.14 19:33
 */
public class AddressSyncParentColumn extends FilteredColumn<AddressSync>{

    public AddressSyncParentColumn(IModel<String> displayModel, String id) {
        super(displayModel, id);
    }

    public AddressSyncParentColumn(String id) {
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

        if (addressSync.getParentId() != null){
            if (addressSync.getType().equals(SyncEntity.DISTRICT) || addressSync.getType().equals(SyncEntity.STREET)){
                objectName = EjbBeanLocator.getBean(CityStrategy.class).displayDomainObject(addressSync.getParentId(), cellItem.getLocale());
            }else if (addressSync.getType().equals(SyncEntity.BUILDING) ){
                objectName = EjbBeanLocator.getBean(StreetStrategy.class).displayDomainObject(addressSync.getParentId(), cellItem.getLocale());
            }else {
                objectName = addressSync.getParentId() + "";
            }
        }

        cellItem.add(new Label(componentId, Model.of(objectName)));
    }
}
