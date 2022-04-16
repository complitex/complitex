package ru.complitex.address.component.group;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import ru.complitex.address.component.input.CityInput;
import ru.complitex.ui.component.form.InputGroup;

import java.time.LocalDate;

/**
 * @author Ivanov Anatoliy
 */
public class CityGroup extends InputGroup {
    public CityGroup(String id, IModel<Long> cityModel, LocalDate date) {
        super(id, new ResourceModel("city"));

        add(new CityInput(INPUT_ID, cityModel, date) {
            @Override
            public boolean isCityRequired() {
                return CityGroup.this.isRequired();
            }
        });
    }
}
