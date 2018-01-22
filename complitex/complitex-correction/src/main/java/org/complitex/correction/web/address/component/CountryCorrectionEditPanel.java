package org.complitex.correction.web.address.component;

import org.apache.wicket.Page;
import org.complitex.address.entity.AddressEntity;
import org.complitex.correction.web.address.CountryCorrectionList;

import java.util.Collections;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 22.01.2018 18:57
 */
public class CountryCorrectionEditPanel extends AddressCorrectionEditPanel{
    public CountryCorrectionEditPanel(String id, Long correctionId) {
        super(AddressEntity.COUNTRY, id, correctionId);
    }

    @Override
    protected List<String> getSearchFilters() {
        return Collections.singletonList("country");
    }

    @Override
    protected Class<? extends Page> getBackPageClass() {
        return CountryCorrectionList.class;
    }
}
