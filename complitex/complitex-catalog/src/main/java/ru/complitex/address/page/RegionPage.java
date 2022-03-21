package ru.complitex.address.page;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import ru.complitex.address.component.group.CountryGroup;
import ru.complitex.address.entity.Region;
import ru.complitex.catalog.entity.Item;
import ru.complitex.catalog.entity.Locale;
import ru.complitex.catalog.entity.Value;
import ru.complitex.catalog.model.DataModel;
import ru.complitex.catalog.service.CatalogService;

import javax.inject.Inject;

/**
 * @author Ivanov Anatoliy
 */
public class RegionPage extends AddressPage {
    @Inject
    private CatalogService catalogService;

    public RegionPage() {
        super(Region.CATALOG);
    }

    @Override
    protected Component newReferenceGroup(String id, IModel<Item> model, Value value) {
        if (value.is(Region.COUNTRY)) {
            return new CountryGroup(id, new DataModel<>(model, value), getDate()).setRequired(true);
        }

        return super.newReferenceGroup(id, model, value);
    }

    @Override
    protected boolean isRequired(Value value) {
        return value.is(Region.COUNTRY) || value.is(Region.REGION_NAME, Locale.SYSTEM);
    }

    @Override
    protected boolean validate(Item item) {
        if (catalogService.getItemsCount(Region.CATALOG, getDate())
                .withoutId(item.getId())
                .withReferenceId(Region.COUNTRY, item.getReferenceId(Region.COUNTRY))
                .withText(Region.REGION_NAME, Locale.SYSTEM, item.getText(Region.REGION_NAME, Locale.SYSTEM))
                .get() > 0) {
            error(getString("error_unique"));

            return false;
        }

        return super.validate(item);
    }
}
