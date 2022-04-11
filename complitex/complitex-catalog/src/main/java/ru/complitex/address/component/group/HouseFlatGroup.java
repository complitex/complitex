package ru.complitex.address.component.group;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import ru.complitex.address.component.input.FlatInput;

import java.time.LocalDate;

/**
 * @author Ivanov Anatoliy
 */
public class HouseFlatGroup extends AddressGroup {
    public HouseFlatGroup(String id, IModel<Long> houseModel, IModel<Long> flatModel, LocalDate date) {
        super(id, new ResourceModel("address"));

        add(new FlatInput("address", houseModel, flatModel, date) {
            @Override
            public boolean isHouseRequired() {
                return HouseFlatGroup.this.isRequired();
            }
        });
    }
}
