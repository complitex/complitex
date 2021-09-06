package ru.complitex.pspoffice.address.component.group;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import ru.complitex.pspoffice.address.component.input.CityInput;

import java.time.LocalDate;

/**
 * @author Ivanov Anatoliy
 */
public class CityGroup extends AddressGroup {
    public CityGroup(String id, IModel<Long> cityModel, LocalDate date) {
        super(id, new ResourceModel("city"));

        add(new CityInput("address", cityModel, date) {
            @Override
            public boolean isCityRequired() {
                return CityGroup.this.isRequired();
            }
        });
    }
}
