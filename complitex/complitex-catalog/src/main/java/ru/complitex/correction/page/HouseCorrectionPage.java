package ru.complitex.correction.page;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import ru.complitex.address.component.group.HouseGroup;
import ru.complitex.address.service.AddressService;
import ru.complitex.catalog.entity.Item;
import ru.complitex.catalog.entity.Value;
import ru.complitex.catalog.model.DataModel;
import ru.complitex.correction.entity.HouseCorrection;

import javax.inject.Inject;

/**
 * @author Ivanov Anatoliy
 */
public class HouseCorrectionPage extends CorrectionPage {
    @Inject
    private AddressService addressService;

    public HouseCorrectionPage() {
        super(HouseCorrection.CATALOG);
    }

    @Override
    protected IModel<String> getColumnModel(IModel<Item> model, Value value) {
        if (value.is(HouseCorrection.HOUSE)) {
            return () -> addressService.getFullHouseName(model.getObject().getReferenceId(HouseCorrection.HOUSE), getDate());
        }

        return super.getColumnModel(model, value);
    }

    @Override
    protected Component newReferenceGroup(String id, IModel<Item> model, Value value) {
        if (value.is(HouseCorrection.HOUSE)) {
            return new HouseGroup(id, new DataModel<>(model, value), getDate());
        }

        return super.newReferenceGroup(id, model, value);
    }
}
