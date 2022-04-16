package ru.complitex.address.component.group;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import ru.complitex.address.component.input.StreetInput;
import ru.complitex.ui.component.form.InputGroup;

import java.time.LocalDate;

/**
 * @author Ivanov Anatoly
 */
public class StreetGroup extends InputGroup {
    public StreetGroup(String id, IModel<Long> streetModel, LocalDate date) {
        super(id, new ResourceModel("street"));

        add(new StreetInput(INPUT_ID, streetModel, date) {
            @Override
            public boolean isStreetRequired() {
                return StreetGroup.this.isRequired();
            }
        });
    }
}
