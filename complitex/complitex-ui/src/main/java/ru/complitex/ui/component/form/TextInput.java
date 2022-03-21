package ru.complitex.ui.component.form;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.danekja.java.util.function.serializable.SerializableConsumer;

/**
 * @author Ivanov Anatoliy
 */
public class TextInput<T> extends Panel {
    private final TextField<T> textField;

    public TextInput(String id, IModel<T> model) {
        super(id);

        textField = new TextField<T>("input", model){
            @SuppressWarnings("unchecked")
            @Override
            public void convertInput() {
                if (getType() != null && getType().equals(String.class)) {
                    String[] value = getInputAsArray();

                    setConvertedInput(value != null && value.length > 0 && !value[0].isEmpty() ? (T) value[0] : null);
                } else {
                    super.convertInput();
                }
            }
        };

        add(textField);
    }

    public TextInput<T> onChange(SerializableConsumer<AjaxRequestTarget> onChange) {
        textField.add(OnChangeAjaxBehavior.onChange(onChange));

        return this;
    }
}
