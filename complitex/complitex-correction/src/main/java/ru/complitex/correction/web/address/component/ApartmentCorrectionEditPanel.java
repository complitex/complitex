package ru.complitex.correction.web.address.component;

import com.google.common.collect.ImmutableList;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.util.string.Strings;
import ru.complitex.address.entity.AddressEntity;
import ru.complitex.address.strategy.building.BuildingStrategy;
import ru.complitex.address.strategy.city.CityStrategy;
import ru.complitex.address.strategy.street.StreetStrategy;
import ru.complitex.address.util.AddressRenderer;
import ru.complitex.common.entity.DomainObject;
import ru.complitex.correction.entity.Correction;
import ru.complitex.correction.web.address.ApartmentCorrectionList;

import javax.ejb.EJB;
import java.util.List;

/**
 * Панель редактирования коррекции района.
 */
public class ApartmentCorrectionEditPanel extends AddressCorrectionEditPanel {
    @EJB
    private CityStrategy cityStrategy;

    @EJB
    private StreetStrategy streetStrategy;

    @EJB
    private BuildingStrategy buildingStrategy;

    public ApartmentCorrectionEditPanel(String id, Long correctionId) {
        super(AddressEntity.APARTMENT, id, correctionId);
    }

    @Override
    protected List<String> getSearchFilters() {
        return ImmutableList.of("city", "street", "building", "apartment");
    }

    @Override
    protected boolean freezeOrganization() {
        return true;
    }

    @Override
    protected Class<? extends Page> getBackPageClass() {
        return ApartmentCorrectionList.class;
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

        DomainObject buildingDomainObject = buildingStrategy.getDomainObject(correction.getParentId());
        String building = buildingStrategy.displayDomainObject(buildingDomainObject, getLocale());

        DomainObject streetDomainObject = streetStrategy.getDomainObject(buildingDomainObject.getParentId());
        String street = streetStrategy.displayDomainObject(streetDomainObject, getLocale());

        DomainObject cityDomainObject = cityStrategy.getDomainObject(streetDomainObject.getParentId());
        String city = cityStrategy.displayDomainObject(cityDomainObject, getLocale());

        return AddressRenderer.displayAddress(null, city, null, street, building, null, correction.getCorrection(), getLocale());
    }

    @Override
    protected Panel getCorrectionInputPanel(String id) {
        return new AddressCorrectionInputPanel(id, getCorrection());
    }

}
