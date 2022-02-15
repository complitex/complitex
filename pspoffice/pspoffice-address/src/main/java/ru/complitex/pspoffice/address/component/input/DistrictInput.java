package ru.complitex.pspoffice.address.component.input;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import ru.complitex.catalog.component.ItemInput;
import ru.complitex.catalog.entity.Item;
import ru.complitex.pspoffice.address.entity.District;
import ru.complitex.pspoffice.address.model.ReferenceModel;
import ru.complitex.ui.entity.Filter;

import java.time.LocalDate;

/**
 * @author Ivanov Anatoly
 */
public class DistrictInput extends CityInput {
    private final IModel<Long> districtModel;

    private final ItemInput district;

    private boolean districtRequired;

    public DistrictInput(String id, IModel<Long> districtModel, LocalDate date) {
        super(id, new ReferenceModel(District.CATALOG, districtModel, District.CITY, date), date);

        this.districtModel = districtModel;

        district = new ItemInput("district", District.CATALOG, districtModel, District.DISTRICT_NAME, date){
            @Override
            protected void onFilter(Filter<Item> filter) {
                filter.getObject().setReferenceId(District.CITY, getCityModel().getObject());
            }

            @Override
            protected void onChangeId(AjaxRequestTarget target) {
                updateCountry(target);
                updateRegion(target);
                updateCity(target);

                onDistrictChange(target);
            }

            @Override
            public boolean isRequired() {
                return isDistrictRequired();
            }
        };
        district.setPlaceholder(new StringResourceModel("district", this));

        add(district);
    }

    public boolean isDistrictRequired() {
        return districtRequired;
    }

    public DistrictInput setDistrictRequired(boolean districtRequired) {
        this.districtRequired = districtRequired;

        return this;
    }

    @Override
    protected void onCityChange(AjaxRequestTarget target) {
        updateDistrict(target);

        onDistrictChange(target);
    }

    protected void onDistrictChange(AjaxRequestTarget target) {}

    protected void updateDistrict(AjaxRequestTarget target) {
        if (districtModel.getObject() != null){
            districtModel.setObject(null);

            target.add(district);
        }
    }
}
