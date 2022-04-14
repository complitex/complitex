package ru.complitex.correction.page;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import ru.complitex.address.component.group.StreetGroup;
import ru.complitex.address.service.AddressService;
import ru.complitex.catalog.entity.Item;
import ru.complitex.catalog.entity.Value;
import ru.complitex.catalog.model.DataModel;
import ru.complitex.correction.entity.StreetCorrection;

import javax.inject.Inject;

/**
 * @author Ivanov Anatoliy
 */
public class StreetCorrectionPage extends CorrectionPage {
    @Inject
    private AddressService addressService;

    public StreetCorrectionPage() {
        super(StreetCorrection.CATALOG);
    }

    @Override
    protected IModel<String> getColumnModel(IModel<Item> model, Value value) {
        if (value.is(StreetCorrection.STREET)) {
           return () -> addressService.getFullStreetName(model.getObject().getReferenceId(StreetCorrection.STREET), getDate());
        }

        return super.getColumnModel(model, value);
    }

    @Override
    protected Component newReferenceGroup(String id, IModel<Item> model, Value value) {
        if (value.is(StreetCorrection.STREET)) {
            return new StreetGroup(id, new DataModel<>(model, value), getDate());
        }

        return super.newReferenceGroup(id, model, value);
    }
}
