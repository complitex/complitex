package org.complitex.correction.web;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.complitex.address.strategy.city.CityStrategy;
import org.complitex.common.entity.FilterWrapper;
import org.complitex.correction.entity.CityCorrection;
import org.complitex.correction.entity.Correction;
import org.complitex.correction.service.AddressCorrectionBean;

import javax.ejb.EJB;
import java.util.List;

public class CityCorrectionList extends AddressCorrectionList<CityCorrection> {
    @EJB
    private AddressCorrectionBean addressCorrectionBean;

    @EJB
    private CityStrategy cityStrategy;

    public CityCorrectionList() {
        super("city");
    }

    @Override
    protected CityCorrection newCorrection() {
        return new CityCorrection();
    }

    @Override
    protected List<CityCorrection> getCorrections(FilterWrapper<CityCorrection> filterWrapper) {
        return addressCorrectionBean.getCityCorrections(filterWrapper);
    }

    @Override
    protected Long getCorrectionsCount(FilterWrapper<CityCorrection> filterWrapper) {
        return addressCorrectionBean.getCityCorrectionsCount(filterWrapper);
    }

    @Override
    protected IModel<String> getTitleModel() {
        return new StringResourceModel("title", this, null);
    }

    @Override
    protected String displayInternalObject(Correction correction) {
        return cityStrategy.displayDomainObject(correction.getObjectId(), getLocale());
    }

    @Override
    protected boolean isSyncVisible() {
        return false;
    }
}
