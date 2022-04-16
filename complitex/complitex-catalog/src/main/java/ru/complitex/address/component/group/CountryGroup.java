package ru.complitex.address.component.group;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import ru.complitex.address.entity.Country;
import ru.complitex.catalog.component.ItemInput;
import ru.complitex.ui.component.form.InputGroup;

import java.time.LocalDate;

/**
 * @author Ivanov Anatoliy
 */
public class CountryGroup extends InputGroup {
    public CountryGroup(String id, IModel<Long> countryModel, LocalDate date) {
        super(id, new ResourceModel("country"));

        add(new ItemInput(INPUT_ID, Country.CATALOG, countryModel, Country.COUNTRY_NAME, date) {
            @Override
            public boolean isRequired() {
                return CountryGroup.this.isRequired();
            }
        });
    }
}
