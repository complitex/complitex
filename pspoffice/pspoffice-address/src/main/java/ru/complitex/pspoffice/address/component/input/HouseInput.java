package ru.complitex.pspoffice.address.component.input;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import ru.complitex.catalog.component.ItemInput;
import ru.complitex.catalog.entity.Item;
import ru.complitex.catalog.entity.Locale;
import ru.complitex.pspoffice.address.catalog.entity.House;
import ru.complitex.pspoffice.address.model.ReferenceModel;
import ru.complitex.ui.entity.Filter;

import java.time.LocalDate;

/**
 * @author Ivanov Anatoliy
 */
public class HouseInput extends DistrictStreetInput {
    private final IModel<Long> houseModel;

    private final ItemInput house;

    private boolean houseRequired;

    public HouseInput(String id, IModel<Long> houseModel, LocalDate date) {
        super(id, new ReferenceModel(House.CATALOG, houseModel, House.DISTRICT, date),
                new ReferenceModel(House.CATALOG, houseModel, House.STREET, date), date);

        this.houseModel = houseModel;

        house = new ItemInput("house", House.CATALOG, houseModel, House.HOUSE_NUMBER, date){
            @Override
            protected void onFilter(Filter<Item> filter) {
                filter.getObject().setReferenceId(House.DISTRICT, getDistrictModel().getObject());
                filter.getObject().setReferenceId(House.STREET, getStreetModel().getObject());
            }

            @Override
            protected void onChangeId(AjaxRequestTarget target) {
                updateDistrict(target);
                updateStreet(target);
                updateCity(target);
                updateRegion(target);
                updateCountry(target);

                onHouseChange(target);
            }

            @Override
            protected String getTextValue(Item item) {
                String textValue = item.getText(House.HOUSE_NUMBER, Locale.SYSTEM);

                String part = item.getText(House.HOUSE_PART, Locale.SYSTEM);

                if (part != null) {
                    textValue += ", КОРП." + part;
                }

                return textValue;
            }

            @Override
            public boolean isRequired() {
                return isHouseRequired();
            }
        };
        house.setPlaceholder(new StringResourceModel("house", this));

        add(house);
    }

    public IModel<Long> getHouseModel() {
        return houseModel;
    }

    public boolean isHouseRequired() {
        return houseRequired;
    }

    public HouseInput setHouseRequired(boolean houseRequired) {
        this.houseRequired = houseRequired;

        return this;
    }

    @Override
    protected void onDistrictStreetChange(AjaxRequestTarget target) {
        updateHouse(target);
    }

    protected void onHouseChange(AjaxRequestTarget target) {}

    protected void updateHouse(AjaxRequestTarget target) {
        if (houseModel.getObject() != null) {
            houseModel.setObject(null);

            target.add(house);
        }
    }
}
