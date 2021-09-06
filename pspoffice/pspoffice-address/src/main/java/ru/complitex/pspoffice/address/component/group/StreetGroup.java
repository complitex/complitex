package ru.complitex.pspoffice.address.component.group;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import ru.complitex.pspoffice.address.component.input.StreetInput;

import java.time.LocalDate;

/**
 * @author Ivanov Anatoly
 */
public class StreetGroup extends AddressGroup {
    public StreetGroup(String id, IModel<Long> streetModel, LocalDate date) {
        super(id, new ResourceModel("street"));

        add(new StreetInput("address", streetModel, date) {
            @Override
            public boolean isStreetRequired() {
                return StreetGroup.this.isRequired();
            }
        });
    }
}
