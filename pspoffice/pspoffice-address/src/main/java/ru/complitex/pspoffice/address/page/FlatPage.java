package ru.complitex.pspoffice.address.page;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.model.IModel;
import ru.complitex.catalog.entity.Item;
import ru.complitex.catalog.entity.Locale;
import ru.complitex.catalog.entity.Value;
import ru.complitex.catalog.model.DataModel;
import ru.complitex.catalog.service.CatalogService;
import ru.complitex.pspoffice.address.entity.Flat;
import ru.complitex.pspoffice.address.component.group.HouseGroup;

import javax.inject.Inject;

/**
 * @author Ivanov Anatoliy
 */
public class FlatPage extends AddressPage {
    @Inject
    private CatalogService catalogService;

    public FlatPage() {
        super(Flat.CATALOG);
    }

    @Override
    protected Component newReferenceGroup(String id, IModel<Item> model, Value value) {
        if (value.is(Flat.STREET)) {
            return new EmptyPanel(id);
        }

        if (value.is(Flat.HOUSE)) {
            return new HouseGroup(id, new DataModel<>(model, value), getDate());
        }

        return super.newReferenceGroup(id, model, value);
    }

    @Override
    protected boolean isRequired(Value value) {
        return value.is(Flat.STREET) || value.is(Flat.HOUSE) || value.is(Flat.FLAT_NUMBER, Locale.SYSTEM);
    }

    @Override
    protected boolean validate(Item item) {
        if (catalogService.getItemsCount(Flat.CATALOG, getDate())
                .withoutId(item.getId())
                .withReferenceId(Flat.STREET, item.getReferenceId(Flat.STREET))
                .withReferenceId(Flat.HOUSE, item.getReferenceId(Flat.HOUSE))
                .withText(Flat.FLAT_NUMBER, Locale.SYSTEM, item.getText(Flat.FLAT_NUMBER, Locale.SYSTEM))
                .get() > 0) {
            error(getString("error_unique"));

            return false;
        }

        return super.validate(item);
    }
}
