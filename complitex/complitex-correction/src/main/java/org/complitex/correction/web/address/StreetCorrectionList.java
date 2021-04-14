package org.complitex.correction.web.address;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.complitex.address.strategy.city.CityStrategy;
import org.complitex.address.strategy.street_type.StreetTypeStrategy;
import org.complitex.address.util.AddressRenderer;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.entity.FilterWrapper;
import org.complitex.common.service.SessionBean;
import org.complitex.common.strategy.IStrategy;
import org.complitex.common.strategy.StrategyFactory;
import org.complitex.correction.entity.Correction;
import org.complitex.correction.service.CorrectionBean;
import org.complitex.template.web.security.SecurityRole;

import javax.ejb.EJB;
import java.util.List;
import java.util.Locale;

@AuthorizeInstantiation(SecurityRole.ADMIN_MODULE_EDIT)
public class StreetCorrectionList extends AddressCorrectionList {

    @EJB
    private StrategyFactory strategyFactory;

    @EJB
    private SessionBean sessionBean;

    @EJB
    private CorrectionBean correctionBean;

    @EJB
    private CityStrategy cityStrategy;

    @EJB
    private StreetTypeStrategy streetTypeStrategy;

    public StreetCorrectionList() {
        super("street");
    }

    @Override
    protected IModel<String> getTitleModel() {
        return new StringResourceModel("title", this, null);
    }


    @Override
    protected List<Correction> getCorrections(FilterWrapper<Correction> filterWrapper) {
        sessionBean.authorize(filterWrapper);

        List<Correction> streets = correctionBean.getCorrections(filterWrapper);

        IStrategy streetStrategy = strategyFactory.getStrategy("street");
        IStrategy cityStrategy = strategyFactory.getStrategy("city");
        Locale locale = getLocale();

        for (Correction c : streets) {
            DomainObject street = streetStrategy.getDomainObject(c.getObjectId(), false);
            if (street == null) {
                street = streetStrategy.getDomainObject(c.getObjectId(), true);
                c.setEditable(false);
            }
            DomainObject city = null;
            if (c.isEditable()) {
                city = cityStrategy.getDomainObject(street.getParentId(), false);
            }
            if (city == null) {
                city = cityStrategy.getDomainObject(street.getParentId(), true);
                c.setEditable(false);
            }
            String displayCity = cityStrategy.displayDomainObject(city, locale);
            String displayStreet = streetStrategy.displayDomainObject(street, locale);
            c.setDisplayObject(displayCity + ", " + displayStreet);
        }

        return streets;
    }

    @Override
    protected String displayCorrection(Correction streetCorrection) {
        String city = null;
        if (streetCorrection.getParentId() != null) {
            city = cityStrategy.displayDomainObject(cityStrategy.getDomainObject(streetCorrection.getParentId(), true),
                    getLocale());
        }

        String streetType = null;
        if (streetCorrection.getAdditionalParentId() != null) {
            streetType = streetTypeStrategy.displayDomainObject(
                    streetTypeStrategy.getDomainObject(streetCorrection.getAdditionalParentId(), true), getLocale());
        }

        return AddressRenderer.displayAddress(null, city, streetType, streetCorrection.getCorrection(),
                null, null, null, getLocale());
    }
}
