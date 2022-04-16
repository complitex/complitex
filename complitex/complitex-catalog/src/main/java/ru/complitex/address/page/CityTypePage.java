package ru.complitex.address.page;

import ru.complitex.address.entity.CityType;
import ru.complitex.catalog.entity.Item;
import ru.complitex.catalog.entity.Locale;
import ru.complitex.catalog.entity.Value;
import ru.complitex.catalog.page.CatalogPage;
import ru.complitex.catalog.service.CatalogService;

import javax.inject.Inject;

/**
 * @author Ivanov Anatoliy
 */
public class CityTypePage extends CatalogPage {
    @Inject
    private CatalogService catalogService;

    public CityTypePage() {
        super(CityType.CATALOG);
    }

    @Override
    protected boolean isRequired(Value value) {
        return value.is(CityType.CITY_TYPE_NAME, Locale.SYSTEM) || value.is(CityType.CITY_TYPE_SHORT_NAME, Locale.SYSTEM);
    }

    @Override
    protected boolean validate(Item item) {
        if (catalogService.getItemsCount(CityType.CATALOG, getDate())
                .withoutId(item.getId())
                .withText(CityType.CITY_TYPE_NAME, item.getText(CityType.CITY_TYPE_NAME, Locale.SYSTEM), Locale.SYSTEM)
                .get() > 0) {
            error(getString("error_unique"));

            return false;
        }

        return super.validate(item);
    }
}
