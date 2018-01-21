package org.complitex.correction.web.component;

import com.google.common.collect.ImmutableList;
import org.apache.wicket.Page;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.complitex.address.entity.AddressEntity;
import org.complitex.correction.web.address.CityCorrectionList;

import java.util.List;

/**
 * Панель редактирования коррекции населенного пункта.
 */
public class CityCorrectionEditPanel extends AddressCorrectionEditPanel {

    public CityCorrectionEditPanel(String id, Long correctionId) {
        super(id, AddressEntity.CITY, correctionId);
    }

    @Override
    protected List<String> getSearchFilters() {
        return ImmutableList.of("city");
    }

    @Override
    protected IModel<String> getTitleModel() {
        return new StringResourceModel("city_title", this, null);
    }

    @Override
    protected Class<? extends Page> getBackPageClass() {
        return CityCorrectionList.class;
    }
}
