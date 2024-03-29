package ru.complitex.address.strategy.street.web.edit;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import ru.complitex.address.strategy.street.StreetStrategy;
import ru.complitex.address.strategy.street_type.StreetTypeStrategy;
import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.web.component.EntityTypePanel;
import ru.complitex.common.web.component.domain.AbstractComplexAttributesPanel;
import ru.complitex.common.web.component.domain.DomainObjectAccessUtil;

/**
 *
 * @author Artem
 */
public class StreetTypeComponent extends AbstractComplexAttributesPanel {

    public StreetTypeComponent(String id, boolean disabled) {
        super(id, disabled);
    }

    @Override
    protected void init() {
        final DomainObject street = getDomainObject();
        IModel<Long> streetTypeModel = new Model<Long>() {

            @Override
            public Long getObject() {
                return street.getAttribute(StreetStrategy.STREET_TYPE).getValueId();
            }

            @Override
            public void setObject(Long object) {
                street.getAttribute(StreetStrategy.STREET_TYPE).setValueId(object);
            }
        };
        EntityTypePanel streetType = new EntityTypePanel("streetType", "street_type", StreetTypeStrategy.NAME, streetTypeModel,
                new ResourceModel("street_type"), true, !isDisabled() && DomainObjectAccessUtil.canEdit(null, "street", street));
        add(streetType);
    }
}
