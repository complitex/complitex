package ru.complitex.common.web.component;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

/**
 * @author Anatoly A. Ivanov
 *         01.02.2017 18:13
 */
public class PropertyLabel<T> extends Label{
    public PropertyLabel(String id, IModel<T> model, String expression) {
        super(id, new PropertyModel<>(model, expression));
    }

    public PropertyLabel(String id, IModel<T> model) {
       this(id, model, id);
    }
}
