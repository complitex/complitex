package ru.complitex.common.web.component;

import org.apache.wicket.model.IModel;
import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.entity.Status;

import java.util.List;

public abstract class DomainObjectDisableAwareRenderer implements IDisableAwareChoiceRenderer<DomainObject> {

    @Override
    public boolean isDisabled(DomainObject object) {
        return object.getStatus() == Status.INACTIVE;
    }

    @Override
    public String getIdValue(DomainObject object, int index) {
        return String.valueOf(object.getObjectId());
    }

    @Override
    public DomainObject getObject(String id, IModel<? extends List<? extends DomainObject>> choices) {
        return choices.getObject().stream().filter(c -> id.equals(String.valueOf(c.getObjectId()))).findAny().orElse(null);
    }
}
