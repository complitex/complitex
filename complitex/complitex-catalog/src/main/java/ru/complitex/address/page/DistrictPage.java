package ru.complitex.address.page;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import ru.complitex.address.component.group.CityGroup;
import ru.complitex.address.entity.District;
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
public class DistrictPage extends CatalogPage {
    @Inject
    private CatalogService catalogService;

    @Inject
    private AddressService addressService;

    public DistrictPage() {
        super(District.CATALOG);
    }

    @Override
    protected IModel<String> getColumnModel(IModel<Item> model, Value value) {
        if (value.is(District.CITY)) {
            return () -> addressService.getFullCityName(model.getObject().getReferenceId(District.CITY), getDate());

        }

        return super.getColumnModel(model, value);
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
