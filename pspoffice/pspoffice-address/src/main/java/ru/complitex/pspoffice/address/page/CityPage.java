package ru.complitex.pspoffice.address.page;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import ru.complitex.catalog.entity.Item;
import ru.complitex.catalog.entity.Locale;
import ru.complitex.catalog.entity.Value;
import ru.complitex.catalog.model.DataModel;
import ru.complitex.catalog.service.CatalogService;
import ru.complitex.pspoffice.address.entity.City;
import ru.complitex.pspoffice.address.component.group.RegionGroup;

import javax.inject.Inject;

/**
 * @author Ivanov Anatoliy
 */
public class CityPage extends AddressPage {
    @Inject
    private CatalogService catalogService;

    public CityPage() {
        super(City.CATALOG);
    }

    @Override
    protected Component newReferenceGroup(String id, IModel<Item> model, Value value) {
        if (value.is(City.REGION)) {
            return new RegionGroup(id, new DataModel<>(model, value), getDate()).setRequired(true);
        }

        return super.newReferenceGroup(id, model, value);
    }

    @Override
    protected boolean isRequired(Value value) {
        return value.is(City.REGION) || value.is(City.CITY_TYPE) || value.is(City.CITY_NAME, Locale.SYSTEM);
    }

    @Override
    protected boolean validate(Item item) {
        if (catalogService.getItemsCount(City.CATALOG, getDate())
                .withoutId(item.getId())
                .withReferenceId(City.REGION, item.getReferenceId(City.REGION))
                .withReferenceId(City.CITY_TYPE, item.getReferenceId(City.CITY_TYPE))
                .withText(City.CITY_NAME, Locale.SYSTEM, item.getText(City.CITY_NAME, Locale.SYSTEM))
                .get() > 0) {
            error(getString("error_unique"));

            return false;
        }

        return super.validate(item);
    }
}
