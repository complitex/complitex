package ru.complitex.common.web.component.domain;

import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.IFilterStateLocator;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.entity.DomainObjectFilter;
import ru.complitex.common.strategy.StrategyFactory;
import ru.complitex.common.util.EjbBeanLocator;

import java.util.Iterator;

/**
 * @author inheaven on 005 05.12.14 19:50.
 */
public class DomainDataProvider extends SortableDataProvider<DomainObject, Long>
        implements IFilterStateLocator<DomainObjectFilter>{
    private DomainObjectFilter example;

    public DomainDataProvider(String entityName) {
        example = new DomainObjectFilter();
        example.setEntityName(entityName);
    }

    @Override
    public Iterator<? extends DomainObject> iterator(long first, long count) {
        example.setFirst(first);
        example.setCount(count);

        if (getSort() != null){
            example.setOrderByAttributeTypeId(getSort().getProperty());
            example.setAsc(getSort().isAscending());
        }

        return EjbBeanLocator.getBean(StrategyFactory.class).getStrategy(example.getEntityName()).getList(example).iterator();
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
