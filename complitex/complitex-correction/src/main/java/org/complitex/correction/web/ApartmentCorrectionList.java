package org.complitex.correction.web;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.complitex.address.strategy.building.BuildingStrategy;
import org.complitex.address.strategy.building.entity.Building;
import org.complitex.address.util.AddressRenderer;
import org.complitex.common.entity.Correction;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.entity.FilterWrapper;
import org.complitex.common.service.SessionBean;
import org.complitex.common.strategy.IStrategy;
import org.complitex.common.web.component.search.SearchComponentState;
import org.complitex.correction.entity.ApartmentCorrection;
import org.complitex.correction.service.AddressCorrectionBean;

import javax.ejb.EJB;
import java.util.List;
import java.util.Locale;

/**
 * @author Pavel Sknar
 */
public class ApartmentCorrectionList extends AddressCorrectionList<ApartmentCorrection> {
    @EJB
    private SessionBean sessionBean;

    @EJB
    private AddressCorrectionBean addressCorrectionBean;

    @EJB
    private BuildingStrategy buildingStrategy;

    public ApartmentCorrectionList() {
        super("apartment");
    }

    @Override
    protected ApartmentCorrection newCorrection() {
        return new ApartmentCorrection();
    }

    @Override
    protected List<ApartmentCorrection> getCorrections(FilterWrapper<ApartmentCorrection> filterWrapper) {
        sessionBean.prepareFilterForPermissionCheck(filterWrapper);

        List<ApartmentCorrection> apartments = addressCorrectionBean.getApartmentCorrections(filterWrapper);

        IStrategy apartmentStrategy = strategyFactory.getStrategy("apartment");
        IStrategy buildingStrategy = strategyFactory.getStrategy("building");
        IStrategy streetStrategy = strategyFactory.getStrategy("street");
        IStrategy cityStrategy = strategyFactory.getStrategy("city");

        Locale locale = getLocale();

        for (Correction c : apartments) {
            DomainObject apartment = apartmentStrategy.getDomainObject(c.getObjectId(), false);
            if (apartment == null) {
                apartment = apartmentStrategy.getDomainObject(c.getObjectId(), true);
                c.setEditable(false);
            }
            DomainObject building = null;
            if (c.isEditable()) {
                building = buildingStrategy.getDomainObject(apartment.getParentId(), false);
            }
            if (building == null) {
                building = buildingStrategy.getDomainObject(apartment.getParentId(), true);
                c.setEditable(false);
            }
            SearchComponentState state = buildingStrategy.getSearchComponentStateForParent(building.getParentId(), "building_address", null);
            DomainObject street = state.get("street");
            DomainObject city = state.get("city");
            String displayBuilding = buildingStrategy.displayDomainObject(building, locale);
            String displayApartment = apartmentStrategy.displayDomainObject(apartment, locale);
            String displayStreet = streetStrategy.displayDomainObject(street, locale);
            String displayCity = cityStrategy.displayDomainObject(city, locale);
            c.setDisplayObject(displayCity + ", " + displayStreet + ", " + displayBuilding + ", " + displayApartment);
        }
        return apartments;
    }

    @Override
    protected Long getCorrectionsCount(FilterWrapper<ApartmentCorrection> filterWrapper) {
        return addressCorrectionBean.getApartmentCorrectionsCount(filterWrapper);
    }

    @Override
    protected IModel<String> getTitleModel() {
        return new StringResourceModel("title", this, null);
    }

    @Override
    protected String displayCorrection(ApartmentCorrection correction) {
        IStrategy buildingStrategy = strategyFactory.getStrategy("building");
        IStrategy streetStrategy = strategyFactory.getStrategy("street");
        IStrategy cityStrategy = strategyFactory.getStrategy("city");

        Building buildingDomainObject = (Building)buildingStrategy.getDomainObject(correction.getBuildingObjectId(), true);
        String building = buildingStrategy.displayDomainObject(buildingDomainObject, getLocale());

        DomainObject streetDomainObject = streetStrategy.getDomainObject(buildingDomainObject.getPrimaryStreetId(), true);
        String street = streetStrategy.displayDomainObject(streetDomainObject, getLocale());

        DomainObject cityDomainObject = cityStrategy.getDomainObject(streetDomainObject.getParentId(), true);
        String city = cityStrategy.displayDomainObject(cityDomainObject, getLocale());

        return AddressRenderer.displayAddress(null, city, null, street, building, null, correction.getCorrection(), getLocale());
    }
}

