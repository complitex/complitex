package org.complitex.sync.web;

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
import org.complitex.common.web.component.datatable.column.FilteredColumn;
import org.complitex.sync.entity.DomainSync;

/**
 * @author Anatoly Ivanov
 *         Date: 031 31.07.14 15:44
 */
public class DomainSyncObjectColumn extends FilteredColumn<DomainSync>
        implements IFilteredColumn<DomainSync, String> {
    public DomainSyncObjectColumn(IModel<String> displayModel, String id) {
        super(displayModel, id);
    }

    public DomainSyncObjectColumn(String id) {
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

//        if (domainSync.getObjectId() != null){
//            if (SyncEntity.STREET_TYPE.equals(domainSync.getType())){
//                StreetTypeStrategy strategy = EjbBeanLocator.getBean(StreetTypeStrategy.class);
//
//                DomainObject domainObject = strategy.getDomainObject(domainSync.getObjectId(), true);
//                objectName = strategy.getName(domainObject) + " (" + strategy.getShortName(domainObject) + ")";
//            }else if (SyncEntity.BUILDING.equals(domainSync.getType())){
//                IStrategy strategy = EjbBeanLocator.getBean(StrategyFactory.class).getStrategy(SyncEntity.BUILDING_ADDRESS.getEntityName());
//                objectName = strategy.displayDomainObject(domainSync.getObjectId(), cellItem.getLocale());
//
//            }else {
//                IStrategy strategy = EjbBeanLocator.getBean(StrategyFactory.class).getStrategy(domainSync.getType().getEntityName());
//                objectName = strategy.displayDomainObject(domainSync.getObjectId(), cellItem.getLocale());
//            }
//        }

        cellItem.add(new Label(componentId, Model.of(objectName)));
    }
}
