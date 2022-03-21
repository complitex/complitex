package ru.complitex.address.component.group;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import ru.complitex.address.component.input.DistrictInput;

import java.time.LocalDate;

/**
 * @author Ivanov Anatoliy
 */
public class DistrictGroup extends AddressGroup {
    public DistrictGroup(String id, IModel<Long> districtModel, LocalDate date) {
        super(id, new ResourceModel("district"));

        add(new DistrictInput("address", districtModel, date) {
            @Override
            public boolean isDistrictRequired() {
                return DistrictGroup.this.isRequired();
            }
        });
    }
}
