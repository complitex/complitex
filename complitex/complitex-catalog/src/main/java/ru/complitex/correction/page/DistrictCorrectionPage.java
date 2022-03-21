package ru.complitex.correction.page;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import ru.complitex.catalog.entity.Item;
import ru.complitex.catalog.entity.Value;
import ru.complitex.catalog.model.DataModel;
import ru.complitex.address.component.group.DistrictGroup;
import ru.complitex.correction.entity.DistrictCorrection;

/**
 * @author Ivanov Anatoliy
 */
public class DistrictCorrectionPage extends CorrectionPage {
    public DistrictCorrectionPage() {
        super(DistrictCorrection.CATALOG);
    }

    @Override
    protected Component newReferenceGroup(String id, IModel<Item> model, Value value) {
        if (value.is(DistrictCorrection.DISTRICT)) {
            return new DistrictGroup(id, new DataModel<>(model, value), getDate());
        }

        return super.newReferenceGroup(id, model, value);
    }
}
