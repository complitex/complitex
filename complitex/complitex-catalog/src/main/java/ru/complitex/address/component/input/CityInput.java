package ru.complitex.address.component.input;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import ru.complitex.address.entity.City;
import ru.complitex.address.entity.CityType;
import ru.complitex.catalog.model.ReferenceModel;
import ru.complitex.catalog.component.ItemInput;
import ru.complitex.catalog.entity.Item;
import ru.complitex.catalog.entity.Locale;
import ru.complitex.catalog.service.CatalogService;
import ru.complitex.ui.entity.Filter;

import javax.inject.Inject;
import java.time.LocalDate;

/**
 * @author Ivanov Anatoliy
 */
public class CityInput extends RegionInput {
    @Inject
    private CatalogService catalogService;

    private IModel<Long> cityModel;

    private final ItemInput city;

    private boolean cityRequired;

    public CityInput(String id, IModel<Long> cityModel, LocalDate date) {
        super(id, new ReferenceModel(City.CATALOG, cityModel, City.REGION, date), date);

        this.cityModel = cityModel;

        city = new ItemInput("city", City.CATALOG, cityModel, City.CITY_NAME, date) {
            @Override
            protected void onFilter(Filter<Item> filter) {
                filter.getObject().setReferenceId(City.REGION, getRegionModel().getObject());
            }

            @Override
            protected void onChangeId(AjaxRequestTarget target) {
                updateCountry(target);
                updateRegion(target);

                onCityChange(target);
            }

            @Override
            protected String getTextValue(Item item) {
                return catalogService.getItem(CityType.CATALOG, item.getReferenceId(City.CITY_TYPE), date)
                        .getText(CityType.CITY_TYPE_SHORT_NAME, Locale.SYSTEM) + " " + super.getTextValue(item);
            }

            @Override
            public boolean isRequired() {
                return isCityRequired();
            }
        };
        city.setPlaceholder(new StringResourceModel("city", this));

        add(city);
    }

    public IModel<Long> getCityModel() {
        return cityModel;
    }

    public void setCityModel(IModel<Long> cityModel) {
        this.cityModel = cityModel;
    }

    public boolean isCityRequired() {
        return cityRequired;
    }

    public CityInput setCityRequired(boolean cityRequired) {
        this.cityRequired = cityRequired;

        return this;
    }

    @Override
    protected void onRegionChange(AjaxRequestTarget target) {
        updateCity(target);

        onCityChange(target);
    }

    protected void onCityChange(AjaxRequestTarget target) {}

    protected void updateCity(AjaxRequestTarget target) {
        cityModel.setObject(null);

        renderCity(target);
    }

    protected void renderCity(AjaxRequestTarget target){
        target.add(city);
    }
}
