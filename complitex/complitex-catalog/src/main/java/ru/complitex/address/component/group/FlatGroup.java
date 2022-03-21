package ru.complitex.address.component.group;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import ru.complitex.address.component.input.FlatInput;

import java.time.LocalDate;

/**
 * @author Ivanov Anatoliy
 */
public class FlatGroup extends AddressGroup {
    public FlatGroup(String id, IModel<Long> flatModel, LocalDate date) {
        super(id, new ResourceModel("flat"));

        add(new FlatInput("address", flatModel, date) {
            @Override
            public boolean isFlatRequired() {
                return FlatGroup.this.isRequired();
            }
        });
    }
}
