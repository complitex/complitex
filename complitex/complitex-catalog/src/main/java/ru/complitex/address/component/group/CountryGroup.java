package ru.complitex.address.component.group;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import ru.complitex.catalog.component.ItemInput;
import ru.complitex.address.entity.Country;

import java.time.LocalDate;

/**
 * @author Ivanov Anatoliy
 */
public class CountryGroup extends AddressGroup {
    public CountryGroup(String id, IModel<Long> countryModel, LocalDate date) {
        super(id, new ResourceModel("country"));

        add(new ItemInput("address", Country.CATALOG, countryModel, Country.COUNTRY_NAME, date) {
            @Override
            public boolean isRequired() {
                return CountryGroup.this.isRequired();
            }
        });
    }
}
