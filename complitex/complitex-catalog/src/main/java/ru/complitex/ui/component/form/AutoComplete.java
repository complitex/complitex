package ru.complitex.ui.component.form;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AbstractAutoCompleteTextRenderer;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

/**
 * @author Ivanov Anatoliy
 */
public abstract class AutoComplete<T extends Serializable> extends Panel {
    private final IModel<Long> model;

    private final HiddenField<Long> idField;

    private final AutoCompleteTextField<T> textField;

    private boolean required;

    private IModel<String> placeholder;

    public AutoComplete(String id, IModel<Long> model) {
        super(id);

        this.model = model;

        setOutputMarkupId(true);

        idField = new HiddenField<Long>("id", model, Long.class){
            @Override
            public boolean isRequired() {
                return AutoComplete.this.isRequired();
            }
        };

        idField.setOutputMarkupId(true);
        idField.setConvertEmptyInputStringToNull(true);

        idField.add(OnChangeAjaxBehavior.onChange(this::onChangeId));

        add(idField);

        textField = new AutoCompleteTextField<T>("input",
                new IModel<T>(){
                    @Override
                    public T getObject() {
                        return AutoComplete.this.getObject(model.getObject());
                    }

                    @Override
                    public void setObject(T object) {

                    }
                },
                new AbstractAutoCompleteTextRenderer<T>() {
                    @Override
                    protected String getTextValue(T object) {
                        return AutoComplete.this.getTextValue(object);
                    }

                    @Override
                    protected CharSequence getOnSelectJavaScriptExpression(T item) {
                        Long id = AutoComplete.this.getId(item);

                        return  "$('#" + idField.getMarkupId() + "').val('" + id + "'); " +
                                " $('#" + idField.getMarkupId() + "').change();" +
                                " input";
                    }
                }
        ) {
            private final String inputName = UUID.randomUUID().toString();

            @Override
            public String getInputName() {
                return inputName;
            }

            @Override
            protected void onComponentTag(final ComponentTag tag){
                super.onComponentTag(tag);

                IModel<String> placeholder = AutoComplete.this.getPlaceholder();

                if (placeholder != null){
                    tag.put("placeholder", placeholder.getObject());
                }
            }

            @Override
            protected Iterator<T> getChoices(String input) {
                return AutoComplete.this.getChoices(input);
            }

            @SuppressWarnings("unchecked")
            @Override
            public <C> IConverter<C> getConverter(Class<C> type) {
                return (IConverter<C>) new IConverter<T>() {
                    @Override
                    public T convertToObject(String value, Locale locale) throws ConversionException {
                        return null;
                    }

                    @Override
                    public String convertToString(T value, Locale locale) {
                        return getTextValue(value);
                    }
                };
            }

            @Override
            public void updateModel() {
                super.updateModel();

                if (getForm().isSubmitted()) {
                    onSubmit();
                }
            }
        };

        textField.setType(Object.class);

        textField.add(OnChangeAjaxBehavior.onChange(target -> {
            if (textField.getInput().isEmpty() || (model.getObject() != null &&
                    !Objects.equals(getTextValue(getObject(model.getObject())), textField.getInput()))){
                model.setObject(null);

                target.appendJavaScript(String.format("Wicket.DOM.get('%s').value = null;", idField.getMarkupId()));
            }

            onChangeText(target);
        }));

        add(textField);
    }

    public IModel<Long> getModel() {
        return model;
    }

    protected void onChangeId(AjaxRequestTarget target) {}

    protected void onChangeText(AjaxRequestTarget target) {}

    public Long getObjectId(){
        return idField.getModelObject();
    }

    public String getInputText(){
        return textField.getInput();
    }

    protected HiddenField<Long> getIdField() {
        return idField;
    }

    protected AutoCompleteTextField<T> getTextField() {
        return textField;
    }

    protected abstract String getTextValue(T object);

    protected abstract Iterator<T> getChoices(String input);

    protected abstract Long getId(T object);

    protected abstract T getObject(Long id);

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public IModel<String> getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(IModel<String> placeholder) {
        this.placeholder = placeholder;
    }

    public void onSubmit() {

    }
}
