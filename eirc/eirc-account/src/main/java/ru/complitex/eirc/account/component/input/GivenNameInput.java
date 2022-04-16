package ru.complitex.eirc.account.component.input;

import org.apache.wicket.model.IModel;
import ru.complitex.eirc.account.entity.GivenName;

import java.time.LocalDate;

/**
 * @author Ivanov Anatoliy
 */
public class GivenNameInput extends NameInput {

    public GivenNameInput(String id, IModel<Long> model, LocalDate date) {
        super(id, GivenName.CATALOG, model, GivenName.GIVEN_NAME, date);
    }
}
