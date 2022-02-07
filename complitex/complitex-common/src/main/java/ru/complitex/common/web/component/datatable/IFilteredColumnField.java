package ru.complitex.common.web.component.datatable;

import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.IFilteredColumn;

/**
 * inheaven on 14.11.2014 17:52.
 */
public interface IFilteredColumnField<T> extends IFilteredColumn<T, String> {
    String getId();
}
