package ru.complitex.common.web.component.datatable;

import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.IFilterStateLocator;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import ru.complitex.common.entity.FilterWrapper;

import java.io.Serializable;
import java.util.Iterator;

/**
 * @author Anatoly Ivanov
 *         Date: 001 01.07.14 19:26
 */
public class FilteredDataProvider<T extends Serializable> extends SortableDataProvider<T, String> implements IFilterStateLocator<T> {
    private FilterWrapper<T> filterWrapper;
    private IFilterBean<T> filterBean;
    private Class<T> objectClass;

    public FilteredDataProvider(IFilterBean<T> filterBean, Class<T> objectClass) {
        this.filterBean = filterBean;
        this.objectClass = objectClass;

        init();
    }

    public void init(){
        try {
            filterWrapper = FilterWrapper.of(objectClass.newInstance());

            onInit(filterWrapper);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    protected void onInit(FilterWrapper<T> filterWrapper){
    }

    @Override
    public Iterator<? extends T> iterator(long first, long count) {
        filterWrapper.setFirst(first);
        filterWrapper.setCount(count);

        if (getSort() != null) {
            filterWrapper.setAscending(getSort().isAscending());
            filterWrapper.setSortProperty(getSort().getProperty());
        }

        return filterBean.getList(filterWrapper).iterator();
    }

    @Override
    public long size() {
        return filterBean.getCount(filterWrapper);
    }

    @Override
    public IModel<T> model(T object) {
        return new CompoundPropertyModel<>(object);
    }

    @Override
    public T getFilterState() {
        return filterWrapper.getObject();
    }

    @Override
    public void setFilterState(T state) {
        filterWrapper.setObject(state);
    }

    public FilterWrapper<T> getFilterWrapper(){
        return filterWrapper;
    }
}
