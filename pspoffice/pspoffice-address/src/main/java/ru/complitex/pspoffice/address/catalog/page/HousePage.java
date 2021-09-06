package ru.complitex.pspoffice.address.catalog.page;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.model.IModel;
import ru.complitex.catalog.entity.Item;
import ru.complitex.catalog.entity.Locale;
import ru.complitex.catalog.entity.Value;
import ru.complitex.catalog.model.DataModel;
import ru.complitex.catalog.service.CatalogService;
import ru.complitex.pspoffice.address.catalog.entity.House;
import ru.complitex.pspoffice.address.component.group.DistrictStreetGroup;

import javax.inject.Inject;

/**
 * @author Ivanov Anatoliy
 */
public class HousePage extends AddressPage {
    @Inject
    private CatalogService catalogService;

    public HousePage() {
        super(House.CATALOG);
    }

    @Override
    protected Component newReferenceGroup(String id, IModel<Item> model, Value value) {
        if (value.is(House.DISTRICT)) {
            return new EmptyPanel(id);
        }

        if (value.is(House.STREET)) {
            return new DistrictStreetGroup(id, new DataModel<>(model, getCatalog().getValue(House.DISTRICT)),
                    new DataModel<>(model, value), getDate()).setRequired(true);
        }

        return super.newReferenceGroup(id, model, value);
    }

    @Override
    protected boolean isRequired(Value value) {
        return value.is(House.STREET) || value.is(House.HOUSE_NUMBER, Locale.SYSTEM);
    }

    @Override
    protected boolean validate(Item item) {
        if (catalogService.getItemsCount(House.CATALOG, getDate())
                .withoutId(item.getId())
                .withReferenceId(House.STREET, item.getReferenceId(House.STREET))
                .withText(House.HOUSE_NUMBER, Locale.SYSTEM, item.getText(House.HOUSE_NUMBER, Locale.SYSTEM))
                .get() > 0) {
            error(getString("error_unique"));

            return false;
        }

        return super.validate(item);
    }
}
