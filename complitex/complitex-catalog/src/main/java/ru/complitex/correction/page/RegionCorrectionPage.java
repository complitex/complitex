package ru.complitex.correction.page;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import ru.complitex.address.component.group.RegionGroup;
import ru.complitex.address.service.AddressService;
import ru.complitex.catalog.entity.Item;
import ru.complitex.catalog.entity.Value;
import ru.complitex.catalog.model.DataModel;
import ru.complitex.correction.entity.RegionCorrection;

import javax.inject.Inject;

/**
 * @author Ivanov Anatoliy
 */
public class RegionCorrectionPage extends CorrectionPage {
    @Inject
    private AddressService addressService;

    public RegionCorrectionPage() {
        super(RegionCorrection.CATALOG);
    }

    @Override
    protected IModel<String> getColumnModel(IModel<Item> model, Value value) {
        if (value.is(RegionCorrection.REGION)) {
            return () -> addressService.getFullRegionName(model.getObject().getReferenceId(RegionCorrection.REGION), getDate());
        }

        return super.getColumnModel(model, value);
    }

    @Override
    protected Component newReferenceGroup(String id, IModel<Item> model, Value value) {
        if (value.is(RegionCorrection.REGION)) {
            return new RegionGroup(id, new DataModel<>(model, value), getDate());
        }

        return super.newReferenceGroup(id, model, value);
    }
}
