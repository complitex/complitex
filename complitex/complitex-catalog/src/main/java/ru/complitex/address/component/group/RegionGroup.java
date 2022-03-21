package ru.complitex.address.component.group;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import ru.complitex.address.component.input.RegionInput;

import java.time.LocalDate;

/**
 * @author Ivanov Anatoliy
 */
public class RegionGroup extends AddressGroup {
    public RegionGroup(String id, IModel<Long> regionModel, LocalDate date) {
        super(id, new ResourceModel("region"));

        add(new RegionInput("address", regionModel, date) {
            @Override
            public boolean isRegionRequired() {
                return RegionGroup.this.isRequired();
            }
        });
    }
}
