package org.complitex.ui.wicket.datatable;

import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.IFilterStateLocator;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;

/**
 * @author Anatoly A. Ivanov
 * 05.07.2017 15:25
 */
public abstract class TableDataProvider<T> extends SortableDataProvider<T, String> implements IFilterStateLocator<T>{

}
