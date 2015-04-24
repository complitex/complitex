package org.complitex.common.web.component.datatable;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.IFilteredColumn;
import org.apache.wicket.markup.html.form.Check;
import org.apache.wicket.markup.html.form.CheckGroupSelector;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.complitex.common.web.component.PanelWrapper;

/**
 * @author inheaven on 001 01.04.15 18:35
 */
public class CheckColumn<T> implements IColumn<T, String>, IFilteredColumn<T, String> {
    @Override
    public Component getFilter(String componentId, FilterForm<?> form) {
        return new EmptyPanel(componentId);
    }

    @Override
    public Component getHeader(String componentId) {
        return PanelWrapper.of(componentId, new CheckGroupSelector(PanelWrapper.ID), PanelWrapper.TYPE.INPUT_CHECKBOX);
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
        cellItem.add(PanelWrapper.of(componentId, new Check<>(PanelWrapper.ID, rowModel), PanelWrapper.TYPE.INPUT_CHECKBOX));
    }

    @Override
    public void detach() {

    }
}
