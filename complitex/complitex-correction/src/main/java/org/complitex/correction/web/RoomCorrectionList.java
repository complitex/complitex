package org.complitex.correction.web;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.complitex.address.strategy.building.entity.Building;
import org.complitex.address.util.AddressRenderer;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.entity.FilterWrapper;
import org.complitex.common.service.SessionBean;
import org.complitex.common.strategy.IStrategy;
import org.complitex.common.web.component.search.SearchComponentState;
import org.complitex.correction.entity.RoomCorrection;
import org.complitex.correction.service.AddressCorrectionBean;

import javax.ejb.EJB;
import java.util.List;
import java.util.Locale;

/**
 * @author Pavel Sknar
 */
public class RoomCorrectionList extends AddressCorrectionList<RoomCorrection> {
    @EJB
    private SessionBean sessionBean;

    @EJB
    private AddressCorrectionBean addressCorrectionBean;

    public RoomCorrectionList() {
        super("room");
    }

    @Override
    protected RoomCorrection newCorrection() {
        return new RoomCorrection();
    }

    @Override
    protected List<RoomCorrection> getCorrections(FilterWrapper<RoomCorrection> filterWrapper) {
        sessionBean.prepareFilterForPermissionCheck(filterWrapper);

        List<RoomCorrection> rooms = addressCorrectionBean.getRoomCorrections(filterWrapper);

        IStrategy roomStrategy = strategyFactory.getStrategy("room");
        IStrategy apartmentStrategy = strategyFactory.getStrategy("apartment");
        IStrategy buildingStrategy = strategyFactory.getStrategy("building");
        IStrategy streetStrategy = strategyFactory.getStrategy("street");
        IStrategy cityStrategy = strategyFactory.getStrategy("city");

        Locale locale = getLocale();

        for (RoomCorrection c : rooms) {
            DomainObject room = roomStrategy.getDomainObject(c.getObjectId(), false);
            if (room == null) {
                room = roomStrategy.getDomainObject(c.getObjectId(), true);
                c.setEditable(false);
            }

            SearchComponentState state = roomStrategy.getSearchComponentStateForParent(room.getParentId(), "room", null);
            DomainObject apartment = state.get("apartment");
            DomainObject building = state.get("building");
            DomainObject street = state.get("street");
            DomainObject city = state.get("city");
            String displayRoom = roomStrategy.displayDomainObject(room, locale);
            String displayApartment = apartment == null || apartment.getObjectId() < 1? "" : apartmentStrategy.displayDomainObject(apartment, locale);
            String displayBuilding = buildingStrategy.displayDomainObject(building, locale);
            String displayStreet = streetStrategy.displayDomainObject(street, locale);
            String displayCity = cityStrategy.displayDomainObject(city, locale);
            c.setDisplayObject(displayCity + ", " + displayStreet + ", " + displayBuilding + ", " + displayApartment + ", " + displayRoom);
        }
        return rooms;
    }

    @Override
    protected Long getCorrectionsCount(FilterWrapper<RoomCorrection> filterWrapper) {
        return addressCorrectionBean.getRoomCorrectionsCount(filterWrapper);
    }

    @Override
    protected IModel<String> getTitleModel() {
        return new StringResourceModel("title", this, null);
    }

    @Override
    protected String displayCorrection(RoomCorrection correction) {
        IStrategy apartmentStrategy = strategyFactory.getStrategy("apartment");
        IStrategy buildingStrategy = strategyFactory.getStrategy("building");
        IStrategy streetStrategy = strategyFactory.getStrategy("street");
        IStrategy cityStrategy = strategyFactory.getStrategy("city");

        String apartment = null;
        Building buildingDomainObject;
        if (correction.getApartmentObjectId() == null) {
            buildingDomainObject = (Building)buildingStrategy.getDomainObject(correction.getBuildingObjectId(), true);
        } else {
            DomainObject apartmentDomainObject = apartmentStrategy.getDomainObject(correction.getApartmentObjectId(), true);
            apartment = apartmentStrategy.displayDomainObject(apartmentDomainObject, getLocale());

            buildingDomainObject = (Building)buildingStrategy.getDomainObject(apartmentDomainObject.getParentId(), true);
        }
        String building = buildingStrategy.displayDomainObject(buildingDomainObject, getLocale());

        DomainObject streetDomainObject = streetStrategy.getDomainObject(buildingDomainObject.getPrimaryStreetId(), true);
        String street = streetStrategy.displayDomainObject(streetDomainObject, getLocale());

        DomainObject cityDomainObject = cityStrategy.getDomainObject(streetDomainObject.getParentId(), true);
        String city = cityStrategy.displayDomainObject(cityDomainObject, getLocale());

        return AddressRenderer.displayAddress(null, city, null, street, building, null, apartment, correction.getCorrection(), getLocale());
    }
}

