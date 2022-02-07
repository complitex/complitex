package ru.complitex.common.web.component.datatable.column;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.TextFilteredPropertyColumn;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.web.component.form.TextFieldPanel;

import java.io.Serializable;

/**
 * @author inheaven on 24.04.2015 15:36.
 */
public abstract class EditableColumn<T extends Serializable> extends TextFilteredPropertyColumn<T, FilterWrapper<T>, String> {
    public EditableColumn(String field) {
        super(new ResourceModel(field), field, field);
    }

    @Override
    public void populateItem(Item<ICellPopulator<T>> item, String componentId, IModel<T> rowModel) {
        if (isEdit(getPropertyExpression(), rowModel)){
            TextFieldPanel textFieldPanel = new TextFieldPanel<>(componentId, getDataModel(rowModel));
            textFieldPanel.getTextField().add(new OnChangeAjaxBehavior() {
                @Override
                protected void onUpdate(AjaxRequestTarget target) {
                }
            });
            item.add(textFieldPanel);

        }else {
            super.populateItem(item, componentId, rowModel);
        }
    }

    protected abstract boolean isEdit(String propertyExpression, IModel<T> rowModel);
}
