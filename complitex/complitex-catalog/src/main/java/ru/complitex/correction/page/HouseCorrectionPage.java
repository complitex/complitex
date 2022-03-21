package ru.complitex.correction.page;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import ru.complitex.correction.entity.HouseCorrection;
import ru.complitex.catalog.entity.Item;
import ru.complitex.catalog.entity.Value;
import ru.complitex.catalog.model.DataModel;
import ru.complitex.address.component.group.HouseGroup;

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
