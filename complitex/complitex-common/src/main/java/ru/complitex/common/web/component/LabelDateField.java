package ru.complitex.common.web.component;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import ru.complitex.common.web.component.dateinput.MaskedDateInput;

import java.util.Date;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 28.11.12 16:17
 */
public class LabelDateField extends Panel {
    private Component dateInput;

    public LabelDateField(String id, IModel<Date> model, boolean nullable, boolean required) {
        super(id, model);

        add(new TextLabel("label", model){
            @Override
            public boolean isVisible() {
                return !LabelDateField.this.isEnabled();
            }
        });

        add(dateInput = new MaskedDateInput("date_input", model) {
            @Override
            public boolean isVisible() {
                return LabelDateField.this.isEnabled();
            }
        }.setNullable(nullable).setRequired(required));
    }

    public LabelDateField(String id, IModel<Date> model, boolean nullable){
        this(id, model, nullable, false);
    }

    @Override
    public Component add(Behavior... behaviors) {
        return dateInput.add(behaviors);
    }
}
