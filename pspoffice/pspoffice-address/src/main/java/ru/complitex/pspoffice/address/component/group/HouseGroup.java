package ru.complitex.pspoffice.address.component.group;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import ru.complitex.pspoffice.address.component.input.HouseInput;

import java.time.LocalDate;

/**
 * @author Ivanov Anatoliy
 */
public class HouseGroup extends AddressGroup {
    public HouseGroup(String id, IModel<Long> houseModel, LocalDate date) {
        super(id, new ResourceModel("house"));

        add(new HouseInput("address", houseModel, date) {
            @Override
            public boolean isHouseRequired() {
                return HouseGroup.this.isRequired();
            }
        });
    }
}
