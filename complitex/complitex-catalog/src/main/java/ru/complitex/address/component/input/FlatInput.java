package ru.complitex.address.component.input;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import ru.complitex.address.entity.Flat;
import ru.complitex.catalog.model.ReferenceModel;
import ru.complitex.catalog.component.ItemInput;
import ru.complitex.catalog.entity.Item;
import ru.complitex.ui.entity.Filter;

import java.time.LocalDate;

/**
 * @author Ivanov Anatoliy
 */
public class FlatInput extends HouseInput {
    private boolean flatRequired;

    public FlatInput(String id, IModel<Long> flatModel, LocalDate date) {
        super(id, new ReferenceModel(Flat.CATALOG, flatModel, Flat.HOUSE, date), date);

        ItemInput flat = new ItemInput("flat", Flat.CATALOG, flatModel, Flat.FLAT_NUMBER, date) {
            @Override
            protected void onFilter(Filter<Item> filter) {
                filter.getObject().setReferenceId(Flat.HOUSE, getHouseModel().getObject());
            }

            @Override
            protected void onChangeId(AjaxRequestTarget target) {
                updateHouse(target);
            }

            @Override
            public boolean isRequired() {
                return isFlatRequired();
            }
        };
        flat.setPlaceholder(new StringResourceModel("flat", this));

        add(flat);
    }

    public boolean isFlatRequired() {
        return flatRequired;
    }

    public FlatInput setFlatRequired(boolean flatRequired) {
        this.flatRequired = flatRequired;

        return this;
    }
}
