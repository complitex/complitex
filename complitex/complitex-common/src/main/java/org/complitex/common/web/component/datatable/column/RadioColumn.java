package org.complitex.common.web.component.datatable.column;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.IFilteredColumn;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.complitex.common.web.component.form.RadioPanel;

/**
 * @author inheaven on 04.06.2015 20:34.
 */
public class RadioColumn<T> implements IColumn<T, String>, IFilteredColumn<T, String> {
    @Override
    public Component getFilter(String componentId, FilterForm<?> form) {
        return new EmptyPanel(componentId);
    }

    @Override
    public Component getHeader(String componentId) {
        return new EmptyPanel(componentId);
    }

    @Override
    public String getSortProperty() {
        return null;
    }

    @Override
    public boolean isSortable() {
        return false;
    }

    @Override
    public void populateItem(Item<ICellPopulator<T>> cellItem, String componentId, IModel<T> rowModel) {
        cellItem.add(new RadioPanel<T>(componentId, rowModel));
    }

    @Override
    public void detach() {

    }
}
