package ru.complitex.eirc.account.component.group;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import ru.complitex.eirc.account.component.input.SurnameInput;
import ru.complitex.ui.component.form.InputGroup;

import java.time.LocalDate;

/**
 * @author Ivanov Anatoliy
 */
public class SurnameGroup extends InputGroup {
    public SurnameGroup(String id, IModel<Long> model, LocalDate date) {
        super(id, new ResourceModel("surname"));

        add(new SurnameInput(INPUT_ID, model, date));
    }
}
