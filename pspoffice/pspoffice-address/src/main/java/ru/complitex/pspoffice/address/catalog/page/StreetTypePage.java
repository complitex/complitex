package ru.complitex.pspoffice.address.catalog.page;

import ru.complitex.catalog.entity.Item;
import ru.complitex.catalog.entity.Locale;
import ru.complitex.catalog.entity.Value;
import ru.complitex.catalog.service.CatalogService;
import ru.complitex.pspoffice.address.catalog.entity.StreetType;

import javax.inject.Inject;

/**
 * @author Ivanov Anatoliy
 */
public class StreetTypePage extends AddressPage {
    @Inject
    private CatalogService catalogService;

    public StreetTypePage() {
        super(StreetType.CATALOG);
    }

    @Override
    protected boolean isRequired(Value value) {
        return value.is(StreetType.STREET_TYPE_NAME, Locale.SYSTEM) || value.is(StreetType.STREET_TYPE_SHORT_NAME, Locale.SYSTEM);
    }

    @Override
    protected boolean validate(Item item) {
        if (catalogService.getItemsCount(StreetType.CATALOG, getDate())
                .withoutId(item.getId())
                .withText(StreetType.STREET_TYPE_NAME, Locale.SYSTEM, item.getText(StreetType.STREET_TYPE_NAME, Locale.SYSTEM))
                .get() > 0) {
            error(getString("error_unique"));

            return false;
        }

        return super.validate(item);
    }
}
