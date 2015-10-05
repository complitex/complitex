/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.common.web.component;

import org.apache.wicket.model.IModel;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.entity.StatusType;

import java.util.List;

/**
 *
 * @author Artem
 */
public abstract class DomainObjectDisableAwareRenderer implements IDisableAwareChoiceRenderer<DomainObject> {

    @Override
    public boolean isDisabled(DomainObject object) {
        return object.getStatus() == StatusType.INACTIVE;
    }

    @Override
    public String getIdValue(DomainObject object, int index) {
        return String.valueOf(object.getObjectId());
    }

    @Override
    public DomainObject getObject(String id, IModel<? extends List<? extends DomainObject>> choices) {
        return choices.getObject().stream().filter(c -> id.equals(String.valueOf(c.getObjectId()))).findAny().get();
    }
}
