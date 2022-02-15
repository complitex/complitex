package ru.complitex.pspoffice.address.page;

import ru.complitex.catalog.entity.Item;
import ru.complitex.catalog.entity.Locale;
import ru.complitex.catalog.entity.Value;
import ru.complitex.catalog.service.CatalogService;
import ru.complitex.pspoffice.address.entity.OrganizationType;

import javax.inject.Inject;

/**
 * @author Ivanov Anatoliy
 */
public class OrganizationTypePage extends AddressPage {
    @Inject
    private CatalogService catalogService;

    public OrganizationTypePage() {
        super(OrganizationType.CATALOG);
    }

    @Override
    protected boolean isRequired(Value value) {
        if (value.is(OrganizationType.ORGANIZATION_TYPE_NAME)) {
            return true;
        }

        return super.isRequired(value);
    }

    @Override
    protected boolean validate(Item item) {
        if (catalogService.getItemsCount(OrganizationType.CATALOG, getDate())
                .withoutId(item.getId())
                .withText(OrganizationType.ORGANIZATION_TYPE_NAME, Locale.SYSTEM, item.getText(OrganizationType.ORGANIZATION_TYPE_NAME, Locale.SYSTEM))
                .get() > 0) {
            error(getString("error_unique"));

            return false;
        }

        return super.validate(item);
    }
}
