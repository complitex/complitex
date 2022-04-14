package ru.complitex.correction.page;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import ru.complitex.address.component.group.CityGroup;
import ru.complitex.address.service.AddressService;
import ru.complitex.catalog.entity.Item;
import ru.complitex.catalog.entity.Value;
import ru.complitex.catalog.model.DataModel;
import ru.complitex.correction.entity.CityCorrection;

import javax.inject.Inject;

/**
 * @author Ivanov Anatoliy
 */
public class CityCorrectionPage extends CorrectionPage {
    @Inject
    private AddressService addressService;

    public CityCorrectionPage() {
        super(CityCorrection.CATALOG);
    }

    @Override
    protected IModel<String> getColumnModel(IModel<Item> model, Value value) {
        if (value.is(CityCorrection.CITY)) {
            return () -> addressService.getFullCityName(model.getObject().getReferenceId(CityCorrection.CITY), getDate());
        }

        return super.getColumnModel(model, value);
    }

    @Override
    protected Component newReferenceGroup(String id, IModel<Item> model, Value value) {
        if (value.is(CityCorrection.CITY)) {
            return new CityGroup(id, new DataModel<>(model, value), getDate());
        }

        return super.newReferenceGroup(id, model, value);
    }
}
