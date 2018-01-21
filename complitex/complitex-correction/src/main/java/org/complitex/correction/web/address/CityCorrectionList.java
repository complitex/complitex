package org.complitex.correction.web.address;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.complitex.address.strategy.city.CityStrategy;
import org.complitex.correction.entity.Correction;

import javax.ejb.EJB;

public class CityCorrectionList extends AddressCorrectionList {

    @EJB
    private CityStrategy cityStrategy;

    public CityCorrectionList() {
        super("city");
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
