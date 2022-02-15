package ru.complitex.pspoffice.address.component.input;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import ru.complitex.catalog.component.ItemInput;
import ru.complitex.catalog.entity.Item;
import ru.complitex.catalog.service.CatalogService;
import ru.complitex.pspoffice.address.entity.District;
import ru.complitex.pspoffice.address.entity.Street;
import ru.complitex.ui.entity.Filter;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.Objects;

/**
 * @author Ivanov Anatoliy
 */
public class DistrictStreetInput extends StreetInput {
    @Inject
    private CatalogService catalogService;

    private final IModel<Long> districtModel;
    private final LocalDate date;

    private final ItemInput district;

    private boolean districtRequired;

    public DistrictStreetInput(String id, IModel<Long> districtModel, IModel<Long> streetModel, LocalDate date) {
        super(id, streetModel, date);

        this.districtModel = districtModel;
        this.date = date;

        district = new ItemInput("district", District.CATALOG, districtModel, District.DISTRICT_NAME, date) {
            @Override
            protected void onFilter(Filter<Item> filter) {
                filter.getObject().setReferenceId(District.CITY, getCityModel().getObject());
            }

            @Override
            protected void onChangeId(AjaxRequestTarget target) {
                Long cityId = catalogService.getReferenceId(District.CATALOG, districtModel.getObject(), District.CITY, date);

                if (cityId == null || !Objects.equals(cityId, getCityModel().getObject())){
                    updateStreet(target);

                    getCityModel().setObject(cityId);
                    renderCity(target);

                    updateRegion(target);

                    updateCountry(target);
                }

                onDistrictStreetChange(target);
            }

            @Override
            public boolean isRequired() {
                return isDistrictRequired();
            }
        };
        district.setPlaceholder(new StringResourceModel("district", this));

        add(district);
    }

    public IModel<Long> getDistrictModel() {
        return districtModel;
    }

    public boolean isDistrictRequired() {
        return districtRequired;
    }

    public DistrictStreetInput setDistrictRequired(boolean districtRequired) {
        this.districtRequired = districtRequired;

        return this;
    }

    @Override
    protected void onStreetChange(AjaxRequestTarget target) {
        onDistrictStreetChange(target);

        Long cityId = null;

        if (getStreetModel().getObject() != null) {
            cityId = catalogService.getReferenceId(Street.CATALOG, getStreetModel().getObject(), Street.CITY, date);
        }

        if (!Objects.equals(cityId, getCityModel().getObject())){
            updateDistrict(target);
        }
    }

    protected void onDistrictStreetChange(AjaxRequestTarget target) {}

    protected void updateDistrict(AjaxRequestTarget target) {
        if (districtModel.getObject() != null){
            districtModel.setObject(null);

            target.add(district);
        }
    }
}
