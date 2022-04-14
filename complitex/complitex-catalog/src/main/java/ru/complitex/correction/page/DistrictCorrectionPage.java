package ru.complitex.correction.page;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import ru.complitex.address.component.group.DistrictGroup;
import ru.complitex.address.service.AddressService;
import ru.complitex.catalog.entity.Item;
import ru.complitex.catalog.entity.Value;
import ru.complitex.catalog.model.DataModel;
import ru.complitex.correction.entity.DistrictCorrection;

import javax.inject.Inject;

/**
 * @author Ivanov Anatoliy
 */
public class DistrictCorrectionPage extends CorrectionPage {
    @Inject
    private AddressService addressService;

    public DistrictCorrectionPage() {
        super(DistrictCorrection.CATALOG);
    }

    @Override
    protected IModel<String> getColumnModel(IModel<Item> model, Value value) {
        if (value.is(DistrictCorrection.DISTRICT)) {
            return () -> addressService.getFullDistrictName(model.getObject().getReferenceId(DistrictCorrection.DISTRICT), getDate());
        }

        return super.getColumnModel(model, value);
    }

    @Override
    protected Component newReferenceGroup(String id, IModel<Item> model, Value value) {
        if (value.is(DistrictCorrection.DISTRICT)) {
            return new DistrictGroup(id, new DataModel<>(model, value), getDate());
        }

        return super.newReferenceGroup(id, model, value);
    }
}
