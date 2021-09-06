package ru.complitex.ui.component.form;

import org.apache.wicket.extensions.markup.html.form.datetime.LocalDateTextField;
import org.apache.wicket.model.IModel;

import java.time.LocalDate;

/**
 * @author Ivanov Anatoliy
 */
public class DateGroup extends FormGroup {
    public DateGroup(String id, IModel<String> labelModel, IModel<LocalDate> model) {
        super(id, labelModel);

        LocalDateTextField dateTextField = new LocalDateTextField("input", model, "dd.MM.yyyy") {
            @Override
            public boolean isRequired() {
                return DateGroup.this.isRequired();
            }
        };

        dateTextField.setLabel(labelModel);

        add(dateTextField);
    }
}
