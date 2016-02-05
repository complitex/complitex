package org.complitex.common.web.component.type;

import org.apache.wicket.model.IModel;

/**
 *
 * @author Artem
 */
public class StringPanel extends InputPanel<String> {

    public StringPanel(String id, IModel<String> model, boolean required, IModel<String> labelModel, boolean enabled) {
        super(id, model, String.class, required, labelModel, enabled);
    }
}
