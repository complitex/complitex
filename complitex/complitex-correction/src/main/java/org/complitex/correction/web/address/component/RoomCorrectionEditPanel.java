package org.complitex.correction.web.address.component;

import com.google.common.collect.ImmutableList;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.util.string.Strings;
import org.complitex.address.entity.AddressEntity;
import org.complitex.address.strategy.apartment.ApartmentStrategy;
import org.complitex.address.strategy.building.BuildingStrategy;
import org.complitex.address.strategy.city.CityStrategy;
import org.complitex.address.strategy.street.StreetStrategy;
import org.complitex.address.util.AddressRenderer;
import org.complitex.common.entity.DomainObject;
import org.complitex.correction.entity.Correction;
import org.complitex.correction.web.address.RoomCorrectionList;

import javax.ejb.EJB;
import java.util.List;

/**
 * Панель редактирования коррекции района.
 */
public class RoomCorrectionEditPanel extends AddressCorrectionEditPanel {
    @EJB
    private CityStrategy cityStrategy;

    @EJB
    private StreetStrategy streetStrategy;

    @EJB
    private BuildingStrategy buildingStrategy;

    @EJB
    private ApartmentStrategy apartmentStrategy;

    public RoomCorrectionEditPanel(String id, Long correctionId) {
        super(AddressEntity.ROOM, id, correctionId);
    }

    @Override
    protected List<String> getSearchFilters() {
        return ImmutableList.of("city", "street", "building", "apartment", "room");
    }

    @Override
    protected boolean freezeOrganization() {
        return true;
    }

    @Override
    protected Class<? extends Page> getBackPageClass() {
        return RoomCorrectionList.class;
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

        String apartment = null;
        DomainObject buildingDomainObject;
        if (correction.getAdditionalParentId() == null) {
            buildingDomainObject = buildingStrategy.getDomainObject(correction.getParentId(), true);
        } else {
            DomainObject apartmentDomainObject = apartmentStrategy.getDomainObject(correction.getAdditionalParentId(), true);
            apartment = apartmentStrategy.displayDomainObject(apartmentDomainObject, getLocale());

            buildingDomainObject = buildingStrategy.getDomainObject(apartmentDomainObject.getParentId(), true);
        }
        String building = buildingStrategy.displayDomainObject(buildingDomainObject, getLocale());

        DomainObject streetDomainObject = streetStrategy.getDomainObject(buildingDomainObject.getParentId(), true);
        String street = streetStrategy.displayDomainObject(streetDomainObject, getLocale());

        DomainObject cityDomainObject = cityStrategy.getDomainObject(streetDomainObject.getParentId(), true);
        String city = cityStrategy.displayDomainObject(cityDomainObject, getLocale());

        return AddressRenderer.displayAddress(null, city, null, street, building, null, apartment, correction.getCorrection(), getLocale());
    }

    @Override
    protected Panel getCorrectionInputPanel(String id) {
        return new AddressCorrectionInputPanel(id, getCorrection());
    }

    @Override
    protected IModel<String> getTitleModel() {
        return new StringResourceModel("room_title", this, null);
    }
}
