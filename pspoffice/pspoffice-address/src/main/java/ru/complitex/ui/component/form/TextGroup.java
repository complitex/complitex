package ru.complitex.ui.component.form;

import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Locale;

/**
 * @author Ivanov Anatoliy
 */
@SuppressWarnings("unchecked")
public class TextGroup<T extends Serializable> extends FormGroup {
    public TextGroup(String id, IModel<String> labelModel, IModel<T> model, Class<T> type) {
        super(id, labelModel);

        TextField<T> textField = new TextField<>("input", model, type){
            @Override
            public boolean isRequired() {
                return TextGroup.this.isRequired();
            }

            @Override
            public <C> IConverter<C> getConverter(Class<C> type) {
                if (type.equals(BigDecimal.class)) {
                    return new IConverter<C>() {
                        @Override
                        public C convertToObject(String value, Locale locale) throws ConversionException {
                            return value != null ? (C) new BigDecimal(value) : null;
                        }

                        @Override
                        public String convertToString(C value, Locale locale) {
                            return value != null ? ((BigDecimal)value).toPlainString() : null;
                        }
                    };
                }

                return super.getConverter(type);
            }
        };

        textField.setLabel(labelModel);

        add(textField);
    }
}
