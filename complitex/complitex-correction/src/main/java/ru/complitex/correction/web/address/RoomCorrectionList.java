package ru.complitex.correction.web.address;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import ru.complitex.address.util.AddressRenderer;
import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.service.SessionBean;
import ru.complitex.common.strategy.IStrategy;
import ru.complitex.common.strategy.StrategyFactory;
import ru.complitex.common.web.component.search.SearchComponentState;
import ru.complitex.correction.entity.Correction;
import ru.complitex.correction.service.CorrectionBean;
import ru.complitex.template.web.security.SecurityRole;

import javax.ejb.EJB;
import java.util.List;
import java.util.Locale;

@AuthorizeInstantiation(SecurityRole.ADMIN_MODULE_EDIT)
public class RoomCorrectionList extends AddressCorrectionList {
    @EJB
    private StrategyFactory strategyFactory;

    @EJB
    private SessionBean sessionBean;

    @EJB
    private CorrectionBean correctionBean;

    public RoomCorrectionList() {
        super("room");
    }


    @Override
    protected List<Correction> getCorrections(FilterWrapper<Correction> filterWrapper) {
        sessionBean.authorize(filterWrapper);

        List<Correction> rooms = correctionBean.getCorrections(filterWrapper);

        IStrategy roomStrategy = strategyFactory.getStrategy("room");
        IStrategy apartmentStrategy = strategyFactory.getStrategy("apartment");
        IStrategy buildingStrategy = strategyFactory.getStrategy("building");
        IStrategy streetStrategy = strategyFactory.getStrategy("street");
        IStrategy cityStrategy = strategyFactory.getStrategy("city");

        Locale locale = getLocale();

        for (Correction c : rooms) {
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
    protected IModel<String> getTitleModel() {
        return new StringResourceModel("title", this, null);
    }

    @Override
    protected String displayCorrection(Correction correction) {
        IStrategy apartmentStrategy = strategyFactory.getStrategy("apartment");
        IStrategy buildingStrategy = strategyFactory.getStrategy("building");
        IStrategy streetStrategy = strategyFactory.getStrategy("street");
        IStrategy cityStrategy = strategyFactory.getStrategy("city");

        String apartment = null;
        DomainObject buildingDomainObject;
        if (correction.getAdditionalParentId() == null) {
            buildingDomainObject = buildingStrategy.getDomainObject(correction.getParentId());
        } else {
            DomainObject apartmentDomainObject = apartmentStrategy.getDomainObject(correction.getAdditionalParentId());
            apartment = apartmentStrategy.displayDomainObject(apartmentDomainObject, getLocale());

            buildingDomainObject = buildingStrategy.getDomainObject(apartmentDomainObject.getParentId());
        }
        String building = buildingStrategy.displayDomainObject(buildingDomainObject, getLocale());

        DomainObject streetDomainObject = streetStrategy.getDomainObject(buildingDomainObject.getParentId());
        String street = streetStrategy.displayDomainObject(streetDomainObject, getLocale());

        DomainObject cityDomainObject = cityStrategy.getDomainObject(streetDomainObject.getParentId());
        String city = cityStrategy.displayDomainObject(cityDomainObject, getLocale());

        return AddressRenderer.displayAddress(null, city, null, street, building, null, apartment, correction.getCorrection(), getLocale());
    }
}
