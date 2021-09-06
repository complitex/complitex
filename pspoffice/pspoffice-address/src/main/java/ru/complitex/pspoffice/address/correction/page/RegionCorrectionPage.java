package ru.complitex.pspoffice.address.correction.page;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import ru.complitex.catalog.entity.Item;
import ru.complitex.catalog.entity.Value;
import ru.complitex.catalog.model.DataModel;
import ru.complitex.pspoffice.address.component.group.RegionGroup;
import ru.complitex.pspoffice.address.correction.entity.RegionCorrection;

/**
 * @author Ivanov Anatoliy
 */
public class RegionCorrectionPage extends CorrectionPage {
    public RegionCorrectionPage() {
        super(RegionCorrection.CATALOG);
    }

    @Override
    protected Component newReferenceGroup(String id, IModel<Item> model, Value value) {
        if (value.is(RegionCorrection.REGION)) {
            return new RegionGroup(id, new DataModel<>(model, value), getDate());
        }

        return super.newReferenceGroup(id, model, value);
    }
}
