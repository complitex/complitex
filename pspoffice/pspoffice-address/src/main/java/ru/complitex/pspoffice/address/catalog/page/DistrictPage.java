package ru.complitex.pspoffice.address.catalog.page;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import ru.complitex.catalog.entity.Item;
import ru.complitex.catalog.entity.Locale;
import ru.complitex.catalog.entity.Value;
import ru.complitex.catalog.model.DataModel;
import ru.complitex.catalog.service.CatalogService;
import ru.complitex.pspoffice.address.catalog.entity.District;
import ru.complitex.pspoffice.address.component.group.CityGroup;

import javax.inject.Inject;

/**
 * @author Ivanov Anatoliy
 */
public class DistrictPage extends AddressPage {
    @Inject
    private CatalogService catalogService;

    public DistrictPage() {
        super(District.CATALOG);
    }

    @Override
    protected Component newReferenceGroup(String id, IModel<Item> model, Value value) {
        if (value.is(District.CITY)) {
            return new CityGroup(id, new DataModel<>(model, value), getDate()).setRequired(true);
        }

        return super.newReferenceGroup(id, model, value);
    }

    @Override
    protected boolean isRequired(Value value) {
        return value.is(District.CITY) || value.is(District.DISTRICT_NAME, Locale.SYSTEM) ;
    }

    @Override
    protected boolean validate(Item item) {
        if (catalogService.getItemsCount(District.CATALOG, getDate())
                .withoutId(item.getId())
                .withReferenceId(District.CITY, item.getReferenceId(District.CITY))
                .withText(District.DISTRICT_NAME, Locale.SYSTEM, item.getText(District.DISTRICT_NAME, Locale.SYSTEM))
                .get() > 0) {
            error(getString("error_unique"));

            return false;
        }

        return super.validate(item);
    }
}
