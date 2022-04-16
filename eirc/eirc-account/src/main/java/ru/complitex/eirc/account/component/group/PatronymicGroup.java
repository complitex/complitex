package ru.complitex.eirc.account.component.group;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import ru.complitex.eirc.account.component.input.PatronymicInput;
import ru.complitex.ui.component.form.InputGroup;

import java.time.LocalDate;

/**
 * @author Ivanov Anatoliy
 */
public class PatronymicGroup extends InputGroup {
    public PatronymicGroup(String id, IModel<Long> model, LocalDate date) {
        super(id, new ResourceModel("patronymic"));

        add(new PatronymicInput(INPUT_ID, model, date));
    }
}
