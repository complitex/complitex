package org.complitex.admin.web;

import org.apache.wicket.model.IModel;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.web.component.domain.AbstractComplexAttributesPanel;
import org.complitex.common.web.component.name.FullNamePanel;

import static org.complitex.admin.strategy.UserInfoStrategy.*;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 16.02.11 15:18
 */
public class UserInfoComplexAttributesPanel extends AbstractComplexAttributesPanel{
    public UserInfoComplexAttributesPanel(String id, boolean disabled) {
        super(id, disabled);
    }

    @Override
    protected void init() {
        DomainObject userInfo = getDomainObject();

        add(new FullNamePanel("full_name", linkModel(userInfo, FIRST_NAME),
                linkModel(userInfo, MIDDLE_NAME), linkModel(userInfo, LAST_NAME)));
    }

    private IModel<Long> linkModel(final DomainObject domainObject, final Long entityAttributeId){
        return new IModel<Long>(){

            @Override
            public Long getObject() {
                return domainObject.getAttribute(entityAttributeId).getValueId();
            }

            @Override
            public void setObject(Long object) {
                domainObject.getAttribute(entityAttributeId).setValueId(object);
            }

            @Override
            public void detach() {
                //bzz...
            }
        };
    }
}
