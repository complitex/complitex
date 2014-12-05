package org.complitex.common.strategy;

import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.IFilterStateLocator;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.entity.example.DomainObjectExample;
import org.complitex.common.util.EjbBeanLocator;

import java.util.Iterator;

/**
 * @author inheaven on 005 05.12.14 19:50.
 */
public class DomainObjectDataProvider extends SortableDataProvider<DomainObject, Long>
        implements IFilterStateLocator<DomainObjectExample>{
    private DomainObjectExample example;

    public DomainObjectDataProvider(String entityTable) {
        example = new DomainObjectExample();
        example.setEntityTable(entityTable);
    }

    @Override
    public Iterator<? extends DomainObject> iterator(long first, long count) {
        example.setFirst(first);
        example.setCount(count);

        if (getSort() != null){
            example.setOrderByAttributeTypeId(getSort().getProperty());
            example.setAsc(getSort().isAscending());
        }

        return EjbBeanLocator.getBean(StrategyFactory.class).getStrategy(example.getEntityTable()).getList(example).iterator();
    }

    @Override
    public long size() {
        return 0;
    }

    @Override
    public IModel<DomainObject> model(DomainObject object) {
        return null;
    }

    @Override
    public DomainObjectExample getFilterState() {
        return null;
    }

    @Override
    public void setFilterState(DomainObjectExample state) {

    }
}
