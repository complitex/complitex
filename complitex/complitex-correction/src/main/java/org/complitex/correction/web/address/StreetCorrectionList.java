package org.complitex.correction.web.address;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.complitex.address.strategy.city.CityStrategy;
import org.complitex.address.strategy.street_type.StreetTypeStrategy;
import org.complitex.address.util.AddressRenderer;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.entity.FilterWrapper;
import org.complitex.common.service.SessionBean;
import org.complitex.common.strategy.IStrategy;
import org.complitex.correction.entity.Correction;
import org.complitex.correction.entity.StreetCorrection;
import org.complitex.correction.service.AddressCorrectionBean;

import javax.ejb.EJB;
import java.util.List;
import java.util.Locale;


/**
 *
 * @author Artem
 */
public class StreetCorrectionList extends AddressCorrectionList<StreetCorrection> {

    @EJB
    private SessionBean sessionBean;

    @EJB
    private AddressCorrectionBean addressCorrectionBean;

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
    protected StreetCorrection newCorrection() {
        return new StreetCorrection();
    }

    @Override
    protected List<StreetCorrection> getCorrections(FilterWrapper<StreetCorrection> filterWrapper) {
        sessionBean.prepareFilterForPermissionCheck(filterWrapper);

        List<StreetCorrection> streets = addressCorrectionBean.getStreetCorrections(filterWrapper);

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
    protected Long getCorrectionsCount(FilterWrapper<StreetCorrection> filterWrapper) {
        return addressCorrectionBean.getStreetCorrectionsCount(filterWrapper);
    }

    @Override
    protected String displayCorrection(StreetCorrection streetCorrection) {
        String city = null;
        if (streetCorrection.getCityId() != null) {
            city = cityStrategy.displayDomainObject(cityStrategy.getDomainObject(streetCorrection.getCityId(), true),
                    getLocale());
        }

        String streetType = null;
        if (streetCorrection.getStreetTypeId() != null) {
            streetType = streetTypeStrategy.displayDomainObject(
                    streetTypeStrategy.getDomainObject(streetCorrection.getStreetTypeId(), true), getLocale());
        }

        return AddressRenderer.displayAddress(null, city, streetType, streetCorrection.getCorrection(), null, null, null, getLocale());
    }
}