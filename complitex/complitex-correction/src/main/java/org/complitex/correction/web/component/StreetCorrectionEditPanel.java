package org.complitex.correction.web.component;

import com.google.common.collect.ImmutableList;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.util.string.Strings;
import org.complitex.address.entity.AddressEntity;
import org.complitex.address.strategy.city.CityStrategy;
import org.complitex.address.strategy.street_type.StreetTypeStrategy;
import org.complitex.address.util.AddressRenderer;
import org.complitex.correction.entity.Correction;
import org.complitex.correction.web.address.StreetCorrectionList;

import javax.ejb.EJB;
import java.util.List;

/**
 * Панель редактирования коррекции улицы.
 */
public class StreetCorrectionEditPanel extends AddressCorrectionEditPanel {
    @EJB
    private CityStrategy cityStrategy;

    @EJB
    private StreetTypeStrategy streetTypeStrategy;

    public StreetCorrectionEditPanel(String id, Long correctionId) {
        super(id, AddressEntity.STREET, correctionId);
    }

    @Override
    protected String displayCorrection() {
        Correction correction = getCorrection();

        String city = null;
        if (correction.getParentId() != null) {
            city = cityStrategy.displayDomainObject(correction.getParentId(), getLocale());
        }

        String streetType = null;
        if (correction.getAdditionalParentId() != null) {
            streetType = streetTypeStrategy.displayDomainObject(correction.getAdditionalParentId(), getLocale());
        }
        if (Strings.isEmpty(streetType)) {
            streetType = null;
        }

        return AddressRenderer.displayAddress(null, city, streetType, correction.getCorrection(), null, null, null, getLocale());
    }

    @Override
    protected Class<? extends Page> getBackPageClass() {
        return StreetCorrectionList.class;
    }

    @Override
    protected Panel getCorrectionInputPanel(String id) {
        return new AddressCorrectionInputPanel(id, getCorrection());
    }

    @Override
    protected boolean freezeOrganization() {
        return true;
    }

    @Override
    protected List<String> getSearchFilters() {
        return ImmutableList.of("city", "street");
    }

    @Override
    protected boolean checkCorrectionEmptiness() {
        return false;
    }

    @Override
    protected boolean preValidate() {
        if (Strings.isEmpty(getCorrection().getCorrection())) {
            error(getString("correction_required"));
            return false;
        }
        return true;
    }

    @Override
    protected IModel<String> getTitleModel() {
        return new StringResourceModel("street_title", this, null);
    }
}
