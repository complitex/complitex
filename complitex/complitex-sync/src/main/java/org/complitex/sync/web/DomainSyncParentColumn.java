package org.complitex.sync.web;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.TextFilter;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.complitex.address.strategy.city.CityStrategy;
import org.complitex.address.strategy.street.StreetStrategy;
import org.complitex.common.util.EjbBeanLocator;
import org.complitex.common.web.component.datatable.column.FilteredColumn;
import org.complitex.sync.entity.DomainSync;
import org.complitex.sync.entity.SyncEntity;

/**
 * @author Anatoly Ivanov
 *         Date: 031 31.07.14 19:33
 */
public class DomainSyncParentColumn extends FilteredColumn<DomainSync>{

    public DomainSyncParentColumn(IModel<String> displayModel, String id) {
        super(displayModel, id);
    }

    public DomainSyncParentColumn(String id) {
        super(new ResourceModel(id), id);
    }

    @Override
    public Component getFilter(String componentId, FilterForm<?> form) {
        return new TextFilter<>(componentId, Model.of(""), form);
    }

    @Override
    public void populateItem(Item<ICellPopulator<DomainSync>> cellItem, String componentId, IModel<DomainSync> rowModel) {
        DomainSync domainSync = rowModel.getObject();

        String objectName = "";

        if (domainSync.getParentObjectId() != null){
            if (domainSync.getType().equals(SyncEntity.DISTRICT) || domainSync.getType().equals(SyncEntity.STREET)){
                objectName = EjbBeanLocator.getBean(CityStrategy.class).displayDomainObject(domainSync.getParentObjectId(), cellItem.getLocale());
            }else if (domainSync.getType().equals(SyncEntity.BUILDING) ){
                objectName = EjbBeanLocator.getBean(StreetStrategy.class).displayDomainObject(domainSync.getParentObjectId(), cellItem.getLocale());
            }else {
                objectName = domainSync.getParentObjectId() + "";
            }
        }

        cellItem.add(new Label(componentId, Model.of(objectName)));
    }
}