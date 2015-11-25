package org.complitex.common.web.component.form;

import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.complitex.common.util.ResourceUtil;

import java.util.List;
import java.util.Locale;

/**
 * @author Anatoly Ivanov
 *         Date: 21.07.2014 20:49
 */
public class EnumChoiceRenderer<T extends Enum<T>> implements IChoiceRenderer<T> {
    private Locale locale;

    public EnumChoiceRenderer(Locale locale) {
        this.locale = locale;
    }

    @Override
    public Object getDisplayValue(T object) {
        return object != null
                ? ResourceUtil.getString(object.getClass().getName(), object.name(), locale)
                : "";
    }

    @Override
    public String getIdValue(T object, int index) {
        return object.ordinal() + "";
    }

    @Override
    public T getObject(String id, IModel<? extends List<? extends T>> choices) {
        return choices.getObject().stream().filter(c -> id.equals(c.ordinal() + "")).findAny().orElse(null);
    }
}
