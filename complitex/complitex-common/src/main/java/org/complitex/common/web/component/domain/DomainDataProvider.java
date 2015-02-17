package org.complitex.common.web.component.domain;

import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.IFilterStateLocator;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.entity.DomainObjectFilter;
import org.complitex.common.strategy.StrategyFactory;
import org.complitex.common.util.EjbBeanLocator;

import java.util.Iterator;

/**
 * @author inheaven on 005 05.12.14 19:50.
 */
public class DomainDataProvider extends SortableDataProvider<DomainObject, Long>
        implements IFilterStateLocator<DomainObjectFilter>{
    private DomainObjectFilter example;

    public DomainDataProvider(String entityTable) {
        example = new DomainObjectFilter();
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
    public DomainObjectFilter getFilterState() {
        return null;
    }

    @Override
    public void setFilterState(DomainObjectFilter state) {

    }
}