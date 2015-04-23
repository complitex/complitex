package org.complitex.common.web.component.datatable;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilteredPropertyColumn;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.complitex.common.web.component.type.MaskedDateInputPanel;

/**
 * @author inheaven on 023 21.04.15 17:18
 */
public class DateColumn<T> extends FilteredPropertyColumn<T, String>{
    public DateColumn(IModel<String> displayModel, String sortProperty, String propertyExpression) {
        super(displayModel, sortProperty, propertyExpression);
    }

    @Override
    public Component getFilter(String componentId, FilterForm<?> form) {
        return new MaskedDateInputPanel(componentId, new PropertyModel<>(form.getModel(), getPropertyExpression()))
                .add(new AttributeModifier("style", "white-space:nowrap; padding-right: 16px;"));
    }
}