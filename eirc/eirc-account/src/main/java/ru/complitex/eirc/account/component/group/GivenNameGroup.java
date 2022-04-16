package ru.complitex.eirc.account.component.group;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import ru.complitex.eirc.account.component.input.GivenNameInput;
import ru.complitex.ui.component.form.InputGroup;

import java.time.LocalDate;

/**
 * @author Ivanov Anatoliy
 */
public class GivenNameGroup extends InputGroup {
    public GivenNameGroup(String id, IModel<Long> model, LocalDate date) {
        super(id, new ResourceModel("givenName"));

        add(new GivenNameInput(INPUT_ID, model, date));
    }
}
