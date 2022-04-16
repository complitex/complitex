package ru.complitex.address.page;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import ru.complitex.address.component.group.CityGroup;
import ru.complitex.address.entity.Street;
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
public class StreetPage extends CatalogPage {
    @Inject
    private CatalogService catalogService;

    @Inject
    private AddressService addressService;

    public StreetPage() {
        super(Street.CATALOG);
    }

    @Override
    protected IModel<String> getColumnModel(IModel<Item> model, Value value) {
        if (value.is(Street.CITY)) {
           return () -> addressService.getFullCityName(model.getObject().getReferenceId(Street.CITY), getDate());
        }

        return super.getColumnModel(model, value);
    }

    @Override
    protected Component newReferenceGroup(String id, IModel<Item> model, Value value) {
        if (value.is(Street.CITY)) {
            return new CityGroup(id, new DataModel<>(model, value), getDate()).setRequired(true);
        }

        return super.newReferenceGroup(id, model, value);
    }

    @Override
    protected boolean isRequired(Value value) {
        return value.is(Street.CITY) || value.is(Street.STREET_TYPE) || value.is(Street.STREET_NAME, Locale.SYSTEM);
    }

    @Override
    protected boolean validate(Item item) {
        if (catalogService.getItemsCount(Street.CATALOG, getDate())
                .withoutId(item.getId())
                .withReferenceId(Street.CITY, item.getReferenceId(Street.CITY))
                .withReferenceId(Street.STREET_TYPE, item.getReferenceId(Street.STREET_TYPE))
                .withText(Street.STREET_NAME, item.getText(Street.STREET_NAME, Locale.SYSTEM), Locale.SYSTEM)
                .get() > 0) {
            error(getString("error_unique"));

            return false;
        }

        return super.validate(item);
    }
}
