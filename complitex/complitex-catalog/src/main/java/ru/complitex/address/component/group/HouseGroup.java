package ru.complitex.address.component.group;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import ru.complitex.address.component.input.HouseInput;
import ru.complitex.ui.component.form.InputGroup;

import java.time.LocalDate;

/**
 * @author Ivanov Anatoliy
 */
public class HouseGroup extends InputGroup {
    public HouseGroup(String id, IModel<Long> houseModel, LocalDate date) {
        super(id, new ResourceModel("house"));

        add(new HouseInput(INPUT_ID, houseModel, date) {
            @Override
            public boolean isHouseRequired() {
                return HouseGroup.this.isRequired();
            }
        });
    }
}
