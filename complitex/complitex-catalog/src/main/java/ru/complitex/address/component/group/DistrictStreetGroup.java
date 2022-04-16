package ru.complitex.address.component.group;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import ru.complitex.address.component.input.DistrictStreetInput;
import ru.complitex.ui.component.form.InputGroup;

import java.time.LocalDate;

/**
 * @author Ivanov Anatoliy
 */
public class DistrictStreetGroup extends InputGroup {
    public DistrictStreetGroup(String id, IModel<Long> districtModel, IModel<Long> streetModel, LocalDate date) {
        super(id, new ResourceModel("street"));

        add(new DistrictStreetInput(INPUT_ID, districtModel, streetModel, date) {
            @Override
            public boolean isDistrictRequired() {
                return DistrictStreetGroup.this.isRequired();
            }

            @Override
            public boolean isStreetRequired() {
                return DistrictStreetGroup.this.isRequired();
            }
        });
    }
}
