package ru.complitex.pspoffice.address.catalog.page;

import ru.complitex.catalog.entity.Item;
import ru.complitex.catalog.entity.Locale;
import ru.complitex.catalog.entity.Value;
import ru.complitex.catalog.service.CatalogService;
import ru.complitex.pspoffice.address.catalog.entity.Country;

import javax.inject.Inject;

/**
 * @author Ivanov Anatoliy
 */
public class CountryPage extends AddressPage {
    @Inject
    private CatalogService catalogService;

    public CountryPage() {
        super(Country.CATALOG);
    }

    @Override
    protected boolean isRequired(Value value) {
        return value.is(Country.COUNTRY_NAME, Locale.SYSTEM);
    }

    @Override
    protected boolean validate(Item item) {
        if (catalogService.getItemsCount(Country.CATALOG, getDate())
                .withoutId(item.getId())
                .withText(Country.COUNTRY_NAME, Locale.SYSTEM, item.getText(Country.COUNTRY_NAME, Locale.SYSTEM))
                .get() > 0) {
            error(getString("error_unique"));

            return false;
        }

        return super.validate(item);
    }
}
