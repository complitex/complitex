package org.complitex.correction.web.address.component;

import org.apache.wicket.Page;
import org.complitex.address.entity.AddressEntity;

import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 22.01.2018 19:02
 */
public class CityTypeCorrectionEditPanel extends AddressCorrectionEditPanel{
    public CityTypeCorrectionEditPanel(String id, Long correctionId) {
        super(AddressEntity.CITY_TYPE, id, correctionId);
    }

    @Override
    protected List<String> getSearchFilters() {
        return null;
    }

    @Override
    protected Class<? extends Page> getBackPageClass() {
        return null;
    }
}
