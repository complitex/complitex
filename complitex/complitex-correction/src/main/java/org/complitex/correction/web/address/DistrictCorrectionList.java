package org.complitex.correction.web.address;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.complitex.address.strategy.city.CityStrategy;
import org.complitex.address.util.AddressRenderer;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.entity.FilterWrapper;
import org.complitex.common.service.SessionBean;
import org.complitex.common.strategy.IStrategy;
import org.complitex.common.strategy.StrategyFactory;
import org.complitex.correction.entity.Correction;
import org.complitex.correction.service.CorrectionBean;

import javax.ejb.EJB;
import java.util.List;
import java.util.Locale;

public class DistrictCorrectionList extends AddressCorrectionList {
    @EJB
    private StrategyFactory strategyFactory;

    @EJB
    private SessionBean sessionBean;

    @EJB
    private CorrectionBean correctionBean;

    @EJB
    private CityStrategy cityStrategy;

    public DistrictCorrectionList() {
        super("district");
    }


    @Override
    protected List<Correction> getCorrections(FilterWrapper<Correction> filterWrapper) {
        sessionBean.authorize(filterWrapper);

        List<Correction> districts = correctionBean.getCorrections(filterWrapper);

        IStrategy districtStrategy = strategyFactory.getStrategy("district");
        IStrategy cityStrategy = strategyFactory.getStrategy("city");

        Locale locale = getLocale();

        for (Correction c : districts) {
            DomainObject district = districtStrategy.getDomainObject(c.getObjectId(), false);
            if (district == null) {
                district = districtStrategy.getDomainObject(c.getObjectId(), true);
                c.setEditable(false);
            }
            DomainObject city = null;
            if (c.isEditable()) {
                city = cityStrategy.getDomainObject(district.getParentId(), false);
            }
            if (city == null) {
                city = cityStrategy.getDomainObject(district.getParentId(), true);
                c.setEditable(false);
            }
            String displayCity = cityStrategy.displayDomainObject(city, locale);
            String displayDistrict = districtStrategy.displayDomainObject(district, locale);
            c.setDisplayObject(displayCity + ", " + displayDistrict); //todo ref c display object
        }
        return districts;
    }

    @Override
    protected IModel<String> getTitleModel() {
        return new StringResourceModel("title", this, null);
    }

    @Override
    protected String displayCorrection(Correction correction) {
        String city = cityStrategy.displayDomainObject(cityStrategy.getDomainObject(correction.getParentId(), true), getLocale());

        return AddressRenderer.displayAddress(null, city, correction.getCorrection(), getLocale());
    }
}
