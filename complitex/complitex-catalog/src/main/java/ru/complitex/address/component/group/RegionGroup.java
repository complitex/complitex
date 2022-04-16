package ru.complitex.address.component.group;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import ru.complitex.address.component.input.RegionInput;
import ru.complitex.ui.component.form.InputGroup;

import java.time.LocalDate;

/**
 * @author Ivanov Anatoliy
 */
public class RegionGroup extends InputGroup {
    public RegionGroup(String id, IModel<Long> regionModel, LocalDate date) {
        super(id, new ResourceModel("region"));

        add(new RegionInput(INPUT_ID, regionModel, date) {
            @Override
            public boolean isRegionRequired() {
                return RegionGroup.this.isRequired();
            }
        });
    }
}
