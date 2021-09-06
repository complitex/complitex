package ru.complitex.pspoffice.address.correction.page;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import ru.complitex.catalog.entity.Item;
import ru.complitex.catalog.entity.Value;
import ru.complitex.catalog.model.DataModel;
import ru.complitex.pspoffice.address.component.group.StreetGroup;
import ru.complitex.pspoffice.address.correction.entity.StreetCorrection;

/**
 * @author Ivanov Anatoliy
 */
public class StreetCorrectionPage extends CorrectionPage {
    public StreetCorrectionPage() {
        super(StreetCorrection.CATALOG);
    }

    @Override
    protected Component newReferenceGroup(String id, IModel<Item> model, Value value) {
        if (value.is(StreetCorrection.STREET)) {
            return new StreetGroup(id, new DataModel<>(model, value), getDate());
        }

        return super.newReferenceGroup(id, model, value);
    }
}
