package ru.complitex.address.page;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import ru.complitex.address.component.group.DistrictStreetGroup;
import ru.complitex.address.entity.House;
import ru.complitex.address.entity.Street;
import ru.complitex.address.entity.StreetType;
import ru.complitex.address.service.AddressService;
import ru.complitex.catalog.entity.Item;
import ru.complitex.catalog.entity.Locale;
import ru.complitex.catalog.entity.Value;
import ru.complitex.catalog.model.DataModel;
import ru.complitex.catalog.page.CatalogPage;
import ru.complitex.catalog.service.CatalogService;
import ru.complitex.catalog.util.Dates;

import javax.inject.Inject;

/**
 * @author Ivanov Anatoliy
 */
public class HousePage extends CatalogPage {
    @Inject
    private CatalogService catalogService;

    @Inject
    private AddressService addressService;

    public HousePage() {
        super(House.CATALOG);
    }

    @Override
    protected IModel<String> getColumnModel(IModel<Item> model, Value value) {
        if (value.is(House.STREET)) {
            return () -> addressService.getFullStreetName(model.getObject().getReferenceId(House.STREET), getDate());
        }

        if (value.getReferenceCatalog() != null && value.getReferenceCatalog().getKeyId() == Street.CATALOG) {
            Long referenceId = model.getObject().getReferenceId(value.getKeyId());

            if (referenceId != null) {
                Item street = catalogService.getItem(Street.CATALOG, referenceId, Dates.now());

                if (street != null) {
                    return Model.of(catalogService.getItem(StreetType.CATALOG, street.getReferenceId(Street.STREET_TYPE), Dates.now())
                            .getText(StreetType.STREET_TYPE_SHORT_NAME, Locale.SYSTEM) + " " + street.getText(Street.STREET_NAME, Locale.SYSTEM));
                }
            }
        }

        return super.getColumnModel(model, value);
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
                .withText(House.HOUSE_NUMBER, item.getText(House.HOUSE_NUMBER, Locale.SYSTEM), Locale.SYSTEM)
                .get() > 0) {
            error(getString("error_unique"));

            return false;
        }

        return super.validate(item);
    }
}
