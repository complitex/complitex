package org.complitex.common.web.component.datatable.column;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.TextFilteredPropertyColumn;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.complitex.common.web.component.ajax.AjaxLinkLabel;

/**
 * @author inheaven on 007 07.04.15 16:09
 */
public abstract class AjaxLinkColumn<T> extends TextFilteredPropertyColumn<T, String, String> {
    public AjaxLinkColumn(String propertyExpression) {
        super(new ResourceModel(propertyExpression), propertyExpression);
    }

    @Override
    public void populateItem(Item<ICellPopulator<T>> item, String componentId, IModel<T> rowModel) {
        item.add(new AjaxLinkLabel(componentId, new PropertyModel<>(rowModel, getPropertyExpression())) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                AjaxLinkColumn.this.onClick(target, rowModel);
            }
        });
    }

    protected abstract void onClick(AjaxRequestTarget target, IModel<T> rowModel);
}
