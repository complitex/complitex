package ru.complitex.eirc.account.component.input;

import org.apache.wicket.model.IModel;
import ru.complitex.eirc.account.entity.Surname;

import java.time.LocalDate;

/**
 * @author Ivanov Anatoliy
 */
public class SurnameInput extends NameInput {

    public SurnameInput(String id, IModel<Long> model, LocalDate date) {
        super(id, Surname.CATALOG, model, Surname.SURNAME, date);
    }
}
