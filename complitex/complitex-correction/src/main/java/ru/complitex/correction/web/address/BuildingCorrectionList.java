package ru.complitex.correction.web.address;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import ru.complitex.address.strategy.city.CityStrategy;
import ru.complitex.address.strategy.street.StreetStrategy;
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

/**
 * Список коррекций домов.
 */
@AuthorizeInstantiation(SecurityRole.ADMIN_MODULE_EDIT)
public class BuildingCorrectionList extends AddressCorrectionList {
    @EJB
    private StrategyFactory strategyFactory;

    @EJB
    private CorrectionBean correctionBean;

    @EJB
    private SessionBean sessionBean;

    @EJB
    private CityStrategy cityStrategy;

    @EJB
    private StreetStrategy streetStrategy;

    public BuildingCorrectionList() {
        super("building");
    }

    @Override
    protected IModel<String> getTitleModel() {
        return new StringResourceModel("title", this, null);
    }

    @Override
    protected List<Correction> getCorrections(FilterWrapper<Correction> filterWrapper) {
        sessionBean.authorize(filterWrapper);

        List<Correction> list = correctionBean.getCorrections(filterWrapper);

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
                SearchComponentState state = buildingStrategy.getSearchComponentStateForParent(building.getParentId(), "street", null);
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
    protected String displayCorrection(Correction correction) {
        DomainObject streetDomainObject = streetStrategy.getDomainObject(correction.getParentId(), true);
        DomainObject cityDomainObject = cityStrategy.getDomainObject(streetDomainObject.getParentId(), true);

        String city = cityStrategy.displayDomainObject(cityDomainObject, getLocale());

        String street = streetStrategy.displayDomainObject(streetDomainObject, getLocale());

        return AddressRenderer.displayAddress(null, city, null, street, correction.getCorrection(),
                correction.getAdditionalCorrection(), null, getLocale());
    }
}
