package ru.complitex.ui.component.form;

import org.apache.wicket.model.IModel;

/**
 * @author Ivanov Anatoliy
 */
public class InputGroup extends FormGroup {
    protected static final String INPUT_ID = "container";

    public InputGroup(String id, IModel<String> labelModel) {
        super(id, labelModel);
    }
}
