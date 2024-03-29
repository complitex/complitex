package ru.complitex.address.strategy.city.web.edit;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import ru.complitex.address.strategy.city.CityStrategy;
import ru.complitex.address.strategy.city_type.CityTypeStrategy;
import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.web.component.EntityTypePanel;
import ru.complitex.common.web.component.domain.AbstractComplexAttributesPanel;
import ru.complitex.common.web.component.domain.DomainObjectAccessUtil;

/**
 *
 * @author Artem
 */
public class CityTypeComponent extends AbstractComplexAttributesPanel {

    public CityTypeComponent(String id, boolean disabled) {
        super(id, disabled);
    }

    @Override
    protected void init() {
        final DomainObject city = getDomainObject();
        IModel<Long> cityTypeModel = new Model<Long>() {

            @Override
            public Long getObject() {
                return city.getAttribute(CityStrategy.CITY_TYPE).getValueId();
            }

            @Override
            public void setObject(Long object) {
                city.getAttribute(CityStrategy.CITY_TYPE).setValueId(object);
            }
        };
        EntityTypePanel cityType = new EntityTypePanel("cityType", "city_type", CityTypeStrategy.NAME, cityTypeModel,
                new ResourceModel("city_type"), true, !isDisabled() && DomainObjectAccessUtil.canEdit(null, "city", city));
        add(cityType);
    }
}
