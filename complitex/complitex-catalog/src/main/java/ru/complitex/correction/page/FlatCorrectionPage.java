package ru.complitex.correction.page;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import ru.complitex.correction.entity.FlatCorrection;
import ru.complitex.catalog.entity.Item;
import ru.complitex.catalog.entity.Value;
import ru.complitex.catalog.model.DataModel;
import ru.complitex.address.component.group.FlatGroup;

/**
 * @author Ivanov Anatoliy
 */
public class FlatCorrectionPage extends CorrectionPage {
    public FlatCorrectionPage() {
        super(FlatCorrection.CATALOG);
    }

    @Override
    protected Component newReferenceGroup(String id, IModel<Item> model, Value value) {
        if (value.is(FlatCorrection.FLAT)) {
            return new FlatGroup(id, new DataModel<>(model, value), getDate());
        }

        return super.newReferenceGroup(id, model, value);
    }
}
