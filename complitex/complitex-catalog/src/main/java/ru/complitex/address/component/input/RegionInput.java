package ru.complitex.address.component.input;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import ru.complitex.address.entity.Country;
import ru.complitex.address.entity.Region;
import ru.complitex.catalog.model.ReferenceModel;
import ru.complitex.catalog.component.ItemInput;
import ru.complitex.catalog.entity.Item;
import ru.complitex.ui.entity.Filter;

import java.time.LocalDate;

/**
 * @author Ivanov Anatoliy
 */
public class RegionInput extends Panel {
    private final IModel<Long> countryModel;
    private final IModel<Long> regionModel;

    private final ItemInput country;
    private final ItemInput region;

    private boolean regionRequired;

    public RegionInput(String id, IModel<Long> regionModel, LocalDate date) {
        super(id);

        this.regionModel = regionModel;

        setOutputMarkupId(true);

        countryModel = new ReferenceModel(Region.CATALOG, regionModel, Region.COUNTRY, date);

        country = new ItemInput("country", Country.CATALOG, countryModel, Country.COUNTRY_NAME, date) {
            @Override
            protected void onChangeId(AjaxRequestTarget target) {
                updateRegion(target);

                onRegionChange(target);
            }
        };
        country.setPlaceholder(new StringResourceModel("country", this));

        add(country);

        region = new ItemInput("region", Region.CATALOG, regionModel, Region.REGION_NAME, date) {
            @Override
            protected void onFilter(Filter<Item> filter) {
                filter.getObject().setReferenceId(Region.COUNTRY, countryModel.getObject());
            }

            @Override
            protected void onChangeId(AjaxRequestTarget target) {
                updateCountry(target);

                onRegionChange(target);
            }

            @Override
            public boolean isRequired() {
                return isRegionRequired();
            }
        };
        region.setPlaceholder(new StringResourceModel("region", this));

        add(region);
    }

    public IModel<Long> getRegionModel() {
        return regionModel;
    }

    public boolean isRegionRequired() {
        return regionRequired;
    }

    public RegionInput setRegionRequired(boolean regionRequired) {
        this.regionRequired = regionRequired;

        return this;
    }

    protected void onRegionChange(AjaxRequestTarget target) {}

    protected void updateCountry(AjaxRequestTarget target) {
        countryModel.setObject(null);

        target.add(country);
    }

    protected void updateRegion(AjaxRequestTarget target) {
        regionModel.setObject(null);

        target.add(region);
    }
}
