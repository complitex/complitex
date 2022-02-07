package ru.complitex.common.web.component.datatable.column;

import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.model.IModel;
import ru.complitex.common.web.component.datatable.IFilteredColumnField;

/**
 * inheaven on 14.11.2014 17:42.
 */
public abstract class FilteredColumn<T> extends AbstractColumn<T, String> implements IFilteredColumnField<T> {
    private String id;

    public FilteredColumn(IModel<String> displayModel, String id) {
        super(displayModel);
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
