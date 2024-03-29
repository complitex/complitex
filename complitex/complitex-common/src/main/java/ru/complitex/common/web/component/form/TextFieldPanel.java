package ru.complitex.common.web.component.form;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 21.04.2014 18:15
 */
public class TextFieldPanel<T> extends Panel {
    private TextField<T> textField;

    public TextFieldPanel(String id, IModel<T> model, Class type, int size) {
        super(id, model);

        add(textField = new TextField<>("text_field", model));
        textField.setConvertEmptyInputStringToNull(true)
                .setType(type)
                .setLabel(Model.of(id))
                .add(AttributeModifier.replace("size", size));
    }

    public TextFieldPanel(String id, IModel<T> model) {
        super(id, model);

        add(textField = new TextField<>("text_field", model));
        textField.setConvertEmptyInputStringToNull(true)
                .setLabel(Model.of(id));
    }

    public TextField<T> getTextField() {
        return textField;
    }
}
