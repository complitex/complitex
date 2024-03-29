package ru.complitex.correction.web.address.component;

import org.apache.wicket.Page;
import ru.complitex.address.entity.AddressEntity;

import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 22.01.2018 19:02
 */
public class StreetTypeCorrectionEditPanel extends AddressCorrectionEditPanel{
    public StreetTypeCorrectionEditPanel(String id, Long correctionId) {
        super(AddressEntity.STREET_TYPE, id, correctionId);
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
