package ru.complitex.common.web.component.type;

import org.apache.wicket.model.IModel;

/**
 *
 * @author Artem
 */
public class DoublePanel extends InputPanel<Double> {

    public DoublePanel(String id, IModel<Double> model, boolean required, IModel<String> labelModel, boolean enabled) {
        super(id, model, Double.class, required, labelModel, enabled);
    }
}
