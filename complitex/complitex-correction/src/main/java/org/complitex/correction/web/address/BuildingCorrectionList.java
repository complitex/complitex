package org.complitex.correction.web.address;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.complitex.address.strategy.city.CityStrategy;
import org.complitex.address.strategy.street.StreetStrategy;
import org.complitex.address.util.AddressRenderer;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.entity.FilterWrapper;
import org.complitex.common.service.SessionBean;
import org.complitex.common.strategy.IStrategy;
import org.complitex.common.strategy.StringLocaleBean;
import org.complitex.common.web.component.search.SearchComponentState;
import org.complitex.correction.entity.BuildingCorrection;
import org.complitex.correction.entity.Correction;
import org.complitex.correction.service.AddressCorrectionBean;

import javax.ejb.EJB;
import java.util.List;
import java.util.Locale;

/**
 * Список коррекций домов.
 * @author Artem
 */
public class BuildingCorrectionList extends AddressCorrectionList<BuildingCorrection> {
    @EJB
    private AddressCorrectionBean addressCorrectionBean;

    @EJB
    private SessionBean sessionBean;

    @EJB
    private CityStrategy cityStrategy;

    @EJB
    private StreetStrategy streetStrategy;

    @EJB
    private StringLocaleBean stringLocaleBean;

    public BuildingCorrectionList() {
        super("building");
    }

    @Override
    protected IModel<String> getTitleModel() {
        return new StringResourceModel("title", this, null);
    }

    @Override
    protected BuildingCorrection newCorrection() {
        return new BuildingCorrection();
    }

    @Override
    protected List<BuildingCorrection> getCorrections(FilterWrapper<BuildingCorrection> filterWrapper) {
        sessionBean.authorize(filterWrapper);

        List<BuildingCorrection> list = addressCorrectionBean.getBuildingCorrections(filterWrapper);

        IStrategy cityStrategy = strategyFactory.getStrategy("city");
        IStrategy streetStrategy = strategyFactory.getStrategy("street");
        IStrategy buildingStrategy = strategyFactory.getStrategy("building");

        Locale locale = getLocale();

        for (Correction c : list) {
            try {
                DomainObject building = buildingStrategy.getDomainObject(c.getObjectId(), false);

                if (building == null) {
                    building = buildingStrategy.getDomainObject(c.getObjectId(), true);
                    c.setEditable(false);
                }
                SearchComponentState state = buildingStrategy.getSearchComponentStateForParent(building.getParentId(), "building_address", null);
                DomainObject street = state.get("street");
                DomainObject city = state.get("city");
                String displayBuilding = buildingStrategy.displayDomainObject(building, locale);
                String displayStreet = streetStrategy.displayDomainObject(street, locale);
                String displayCity = cityStrategy.displayDomainObject(city, locale);
                c.setDisplayObject(displayCity + ", " + displayStreet + ", " + displayBuilding);
            } catch (Exception e) {
                log().warn("[Полный адрес не найден]", e);
                c.setDisplayObject("[Полный адрес не найден]");
                c.setEditable(false);
            }
        }

        return list;
    }

    @Override
    protected Long getCorrectionsCount(FilterWrapper<BuildingCorrection> filterWrapper) {
        return addressCorrectionBean.getBuildingCorrectionsCount(filterWrapper);
    }

    @Override
    protected String displayCorrection(BuildingCorrection correction) {
        DomainObject streetDomainObject = streetStrategy.getDomainObject(correction.getStreetId(), true);
        DomainObject cityDomainObject = cityStrategy.getDomainObject(streetDomainObject.getParentId(), true);

        String city = cityStrategy.displayDomainObject(cityDomainObject, getLocale());

        String street = streetStrategy.displayDomainObject(streetDomainObject, getLocale());

        return AddressRenderer.displayAddress(null, city, null, street, correction.getCorrection(),
                correction.getCorrectionCorp(), null, getLocale());
    }
}
