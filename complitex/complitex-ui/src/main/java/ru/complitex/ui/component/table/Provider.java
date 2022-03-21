package ru.complitex.ui.component.table;

import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import ru.complitex.ui.entity.Filter;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

/**
 * @author Ivanov Anatoliy
 */
public abstract class Provider<T extends Serializable> implements IDataProvider<T> {
    private final Filter<T> filter;

    public Provider(Filter<T> filter) {
        this.filter = filter;
    }

    public Filter<T> getFilter() {
        return filter;
    }

    @Override
    public Iterator<? extends T> iterator(long first, long count) {
        filter.setOffset(first);
        filter.setLimit(count);

        return list().iterator();
    }

    @Override
    public abstract long size();

    @Override
    public IModel<T> model(T object) {
        return Model.of(object);
    }

    protected abstract List<T> list();
}
