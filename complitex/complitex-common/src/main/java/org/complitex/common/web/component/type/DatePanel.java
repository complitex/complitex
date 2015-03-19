package org.complitex.common.web.component.type;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import java.util.Date;
import org.complitex.common.web.component.DatePicker;

public final class DatePanel extends Panel {

    public DatePanel(String id, IModel<Date> model, boolean required, IModel<String> labelModel, boolean enabled) {
        super(id);

        DatePicker<Date> dateField = new DatePicker<Date>("dateField", model, Date.class);
        dateField.setEnabled(enabled);
        dateField.setLabel(labelModel);
        dateField.setRequired(required);

        dateField.add(new OnChangeAjaxBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {

            }
        });

        add(dateField);
    }
}
