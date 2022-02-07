package ru.complitex.correction.web.address.component;

import org.apache.wicket.Page;
import ru.complitex.address.entity.AddressEntity;
import ru.complitex.correction.web.address.CityTypeCorrectionList;

import java.util.Collections;
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
        return Collections.singletonList("city_type");
    }

    @Override
    protected Class<? extends Page> getBackPageClass() {
        return CityTypeCorrectionList.class;
    }
}
