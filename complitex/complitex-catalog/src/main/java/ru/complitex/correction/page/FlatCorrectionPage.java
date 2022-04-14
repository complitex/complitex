package ru.complitex.correction.page;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import ru.complitex.address.component.group.FlatGroup;
import ru.complitex.address.service.AddressService;
import ru.complitex.catalog.entity.Item;
import ru.complitex.catalog.entity.Value;
import ru.complitex.catalog.model.DataModel;
import ru.complitex.correction.entity.FlatCorrection;

import javax.inject.Inject;

/**
 * @author Ivanov Anatoliy
 */
public class FlatCorrectionPage extends CorrectionPage {
    @Inject
    private AddressService addressService;

    public FlatCorrectionPage() {
        super(FlatCorrection.CATALOG);
    }

    @Override
    protected IModel<String> getColumnModel(IModel<Item> model, Value value) {
        if (value.is(FlatCorrection.FLAT)) {
            return () -> addressService.getFullFlatName(model.getObject().getReferenceId(FlatCorrection.FLAT), getDate());
        }

        return super.getColumnModel(model, value);
    }

    @Override
    protected Component newReferenceGroup(String id, IModel<Item> model, Value value) {
        if (value.is(FlatCorrection.FLAT)) {
            return new FlatGroup(id, new DataModel<>(model, value), getDate());
        }

        return super.newReferenceGroup(id, model, value);
    }
}
