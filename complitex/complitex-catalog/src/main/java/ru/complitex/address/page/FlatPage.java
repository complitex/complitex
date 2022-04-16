package ru.complitex.address.page;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import ru.complitex.address.component.group.HouseGroup;
import ru.complitex.address.entity.Flat;
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
public class FlatPage extends CatalogPage {
    @Inject
    private CatalogService catalogService;

    @Inject
    private AddressService addressService;

    public FlatPage() {
        super(Flat.CATALOG);
    }

    @Override
    protected IModel<String> getColumnModel(IModel<Item> model, Value value) {
        if (value.is(Flat.HOUSE)) {
            return () -> addressService.getFullHouseName(model.getObject().getReferenceId(Flat.HOUSE), getDate());
        }

        return super.getColumnModel(model, value);
    }

    @Override
    protected Component newReferenceGroup(String id, IModel<Item> model, Value value) {
        if (value.is(Flat.HOUSE)) {
            return new HouseGroup(id, new DataModel<>(model, value), getDate());
        }

        return super.newReferenceGroup(id, model, value);
    }

    @Override
    protected boolean isRequired(Value value) {
        return value.is(Flat.HOUSE) || value.is(Flat.FLAT_NUMBER, Locale.SYSTEM);
    }

    @Override
    protected boolean validate(Item item) {
        if (catalogService.getItemsCount(Flat.CATALOG, getDate())
                .withoutId(item.getId())
                .withReferenceId(Flat.HOUSE, item.getReferenceId(Flat.HOUSE))
                .withText(Flat.FLAT_NUMBER, item.getText(Flat.FLAT_NUMBER, Locale.SYSTEM), Locale.SYSTEM)
                .get() > 0) {
            error(getString("error_unique"));

            return false;
        }

        return super.validate(item);
    }
}
