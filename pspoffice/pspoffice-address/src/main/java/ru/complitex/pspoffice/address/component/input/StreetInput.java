package ru.complitex.pspoffice.address.component.input;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import ru.complitex.catalog.component.ItemInput;
import ru.complitex.catalog.entity.Item;
import ru.complitex.pspoffice.address.catalog.entity.Street;
import ru.complitex.pspoffice.address.model.ReferenceModel;
import ru.complitex.ui.entity.Filter;

import java.time.LocalDate;

/**
 * @author Ivanov Anatoliy
 */
public class StreetInput extends CityInput {
    private final IModel<Long> streetModel;

    private final ItemInput street;

    private boolean streetRequired;

    public StreetInput(String id, IModel<Long> streetModel, LocalDate date) {
        super(id, new ReferenceModel(Street.CATALOG, streetModel, Street.CITY, date), date);

        this.streetModel = streetModel;

        street = new ItemInput("street", Street.CATALOG, streetModel, Street.STREET_NAME, date){
            @Override
            protected void onFilter(Filter<Item> filter) {
                filter.getObject().setReferenceId(Street.CITY, getCityModel().getObject());
            }

            @Override
            protected void onChangeId(AjaxRequestTarget target) {
                updateCountry(target);
                updateRegion(target);
                updateCity(target);

                onStreetChange(target);
            }

            @Override
            public boolean isRequired() {
                return isStreetRequired();
            }
        };
        street.setPlaceholder(new StringResourceModel("street", this));
        add(street);
    }

    public IModel<Long> getStreetModel() {
        return streetModel;
    }

    public boolean isStreetRequired() {
        return streetRequired;
    }

    public StreetInput setStreetRequired(boolean streetRequired) {
        this.streetRequired = streetRequired;

        return this;
    }

    @Override
    protected void onCityChange(AjaxRequestTarget target) {
        onStreetChange(target);

        updateStreet(target);
    }

    protected void onStreetChange(AjaxRequestTarget target) {}

    protected void updateStreet(AjaxRequestTarget target) {
        if (streetModel.getObject() != null){
            streetModel.setObject(null);

            target.add(street);
        }
    }
}
