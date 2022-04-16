package ru.complitex.address.component.group;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import ru.complitex.address.component.input.DistrictInput;
import ru.complitex.ui.component.form.InputGroup;

import java.time.LocalDate;

/**
 * @author Ivanov Anatoliy
 */
public class DistrictGroup extends InputGroup {
    public DistrictGroup(String id, IModel<Long> districtModel, LocalDate date) {
        super(id, new ResourceModel("district"));

        add(new DistrictInput(INPUT_ID, districtModel, date) {
            @Override
            public boolean isDistrictRequired() {
                return DistrictGroup.this.isRequired();
            }
        });
    }
}
