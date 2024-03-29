package ru.complitex.correction.web.address.component;

import com.google.common.collect.ImmutableList;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.util.string.Strings;
import ru.complitex.address.entity.AddressEntity;
import ru.complitex.address.strategy.city.CityStrategy;
import ru.complitex.address.util.AddressRenderer;
import ru.complitex.correction.entity.Correction;
import ru.complitex.correction.web.address.DistrictCorrectionList;

import javax.ejb.EJB;
import java.util.List;

/**
 * Панель редактирования коррекции района.
 */
public class DistrictCorrectionEditPanel extends AddressCorrectionEditPanel {
    @EJB
    private CityStrategy cityStrategy;


    public DistrictCorrectionEditPanel(String id, Long correctionId) {
        super(AddressEntity.DISTRICT, id, correctionId);
    }

    @Override
    protected List<String> getSearchFilters() {
        return ImmutableList.of("city", "district");
    }

    @Override
    protected boolean freezeOrganization() {
        return true;
    }

    @Override
    protected Class<? extends Page> getBackPageClass() {
        return DistrictCorrectionList.class;
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
    protected String displayCorrection() {
        Correction correction = getCorrection();

        String city = cityStrategy.displayDomainObject(cityStrategy.getDomainObject(correction.getParentId()), getLocale());

        return AddressRenderer.displayAddress(null, city, correction.getCorrection(), getLocale());
    }

    @Override
    protected Panel getCorrectionInputPanel(String id) {
        return new AddressCorrectionInputPanel(id, getCorrection());
    }

    @Override
    protected IModel<String> getTitleModel() {
        return new StringResourceModel("district_title", this, null);
    }
}
