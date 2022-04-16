package ru.complitex.eirc.account.component.input;

import org.apache.wicket.model.IModel;
import ru.complitex.eirc.account.entity.Patronymic;

import java.time.LocalDate;

/**
 * @author Ivanov Anatoliy
 */
public class PatronymicInput extends NameInput {
    public PatronymicInput(String id, IModel<Long> model, LocalDate date) {
        super(id, Patronymic.CATALOG, model, Patronymic.PATRONYMIC, date);
    }
}
