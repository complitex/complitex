package ru.complitex.correction.web.address.component;

import com.google.common.collect.ImmutableList;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.util.string.Strings;
import ru.complitex.address.entity.AddressEntity;
import ru.complitex.address.strategy.city.CityStrategy;
import ru.complitex.address.strategy.street.StreetStrategy;
import ru.complitex.address.util.AddressRenderer;
import ru.complitex.common.entity.DomainObject;
import ru.complitex.correction.entity.Correction;
import ru.complitex.correction.web.address.BuildingCorrectionList;

import javax.ejb.EJB;
import java.util.List;

/**
 * Панель редактирования коррекции дома.
 */
public class BuildingCorrectionEditPanel extends AddressCorrectionEditPanel {
    @EJB
    private StreetStrategy streetStrategy;

    @EJB
    private CityStrategy cityStrategy;

    public BuildingCorrectionEditPanel(String id, Long correctionId) {
        super(AddressEntity.BUILDING, id, correctionId);
    }

    @Override
    protected String displayCorrection() {
        Correction correction = getCorrection();

        DomainObject streetDomainObject = streetStrategy.getDomainObject(correction.getParentId(), true);
        String street = streetStrategy.displayDomainObject(streetDomainObject, getLocale());

        String city = cityStrategy.displayDomainObject(streetDomainObject.getParentId(), getLocale());

        return AddressRenderer.displayAddress(null, city, null, street, correction.getCorrection(),
                correction.getAdditionalCorrection(), null, getLocale());
    }

    @Override
    protected Class<? extends Page> getBackPageClass() {
        return BuildingCorrectionList.class;
    }

    @Override
    protected Panel getCorrectionInputPanel(String id) {
        return new AddressCorrectionInputPanel(id, getCorrection());
    }

    @Override
    protected List<String> getSearchFilters() {
        return ImmutableList.of("city", "street", "building");
    }

    @Override
    protected boolean freezeOrganization() {
        return true;
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
        return new StringResourceModel("building_title", this, null);
    }

    @Override
    protected void save() {
        if (getCorrection().getAdditionalCorrection() == null){
            getCorrection().setAdditionalCorrection("");
        }

        super.save();
    }
}
