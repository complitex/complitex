package ru.complitex.address.page;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import ru.complitex.address.component.group.RegionGroup;
import ru.complitex.address.entity.City;
import ru.complitex.address.service.AddressService;
import ru.complitex.catalog.entity.Item;
import ru.complitex.catalog.entity.Locale;
import ru.complitex.catalog.entity.Value;
import ru.complitex.catalog.model.DataModel;
import ru.complitex.catalog.page.CatalogPage;
import ru.complitex.catalog.service.CatalogService;

import javax.inject.Inject;

/**
 * @author Ivanov Anatoliy
 */
public class CityPage extends CatalogPage {
    @Inject
    private CatalogService catalogService;

    @Inject
    private AddressService addressService;

    public CityPage() {
        super(City.CATALOG);
    }

    @Override
    protected IModel<String> getColumnModel(IModel<Item> model, Value value) {
        if (value.is(City.REGION)) {
            return () -> addressService.getFullRegionName(model.getObject().getReferenceId(City.REGION), getDate());
        }

        return super.getColumnModel(model, value);
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
                .withText(City.CITY_NAME, item.getText(City.CITY_NAME, Locale.SYSTEM), Locale.SYSTEM)
                .get() > 0) {
            error(getString("error_unique"));

            return false;
        }

        return super.validate(item);
    }
}
