package ru.complitex.pspoffice.address.correction.page;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import ru.complitex.catalog.entity.Item;
import ru.complitex.catalog.entity.Value;
import ru.complitex.catalog.model.DataModel;
import ru.complitex.pspoffice.address.component.group.HouseGroup;
import ru.complitex.pspoffice.address.correction.entity.HouseCorrection;

/**
 * @author Ivanov Anatoliy
 */
public class HouseCorrectionPage extends CorrectionPage {
    public HouseCorrectionPage() {
        super(HouseCorrection.CATALOG);
    }

    @Override
    protected Component newReferenceGroup(String id, IModel<Item> model, Value value) {
        if (value.is(HouseCorrection.HOUSE)) {
            return new HouseGroup(id, new DataModel<>(model, value), getDate());
        }

        return super.newReferenceGroup(id, model, value);
    }
}
