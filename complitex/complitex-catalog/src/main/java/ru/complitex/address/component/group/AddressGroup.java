package ru.complitex.address.component.group;

import org.apache.wicket.model.IModel;
import ru.complitex.ui.component.form.FormGroup;

/**
 * @author Ivanov Anatoliy
 */
public class AddressGroup extends FormGroup {
    public AddressGroup(String id, IModel<String> labelModel) {
        super(id, labelModel);
    }
}
