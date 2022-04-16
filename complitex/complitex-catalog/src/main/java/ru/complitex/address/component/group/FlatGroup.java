package ru.complitex.address.component.group;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import ru.complitex.address.component.input.FlatInput;
import ru.complitex.ui.component.form.InputGroup;

import java.time.LocalDate;

/**
 * @author Ivanov Anatoliy
 */
public class FlatGroup extends InputGroup {
    public FlatGroup(String id, IModel<Long> flatModel, LocalDate date) {
        super(id, new ResourceModel("flat"));

        add(new FlatInput(INPUT_ID, flatModel, date) {
            @Override
            public boolean isFlatRequired() {
                return FlatGroup.this.isRequired();
            }
        });
    }
}
