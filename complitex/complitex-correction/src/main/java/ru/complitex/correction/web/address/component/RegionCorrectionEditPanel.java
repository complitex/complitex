package ru.complitex.correction.web.address.component;

import org.apache.wicket.Page;
import ru.complitex.address.entity.AddressEntity;
import ru.complitex.correction.web.address.RegionCorrectionList;

import java.util.Collections;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 22.01.2018 19:01
 */
public class RegionCorrectionEditPanel extends AddressCorrectionEditPanel{
    public RegionCorrectionEditPanel(String id, Long correctionId) {
        super(AddressEntity.REGION, id, correctionId);
    }

    @Override
    protected List<String> getSearchFilters() {
        return Collections.singletonList("region");
    }

    @Override
    protected Class<? extends Page> getBackPageClass() {
        return RegionCorrectionList.class;
    }
}
