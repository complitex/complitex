package ru.complitex.common.web.model;

import org.apache.wicket.model.IModel;
import ru.complitex.common.entity.DomainObjectFilter;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 30.01.14 2:37
 */
public class AttributeExampleModel implements IModel<String> {
    private DomainObjectFilter example;
    private Long entityAttributeId;

    public AttributeExampleModel(DomainObjectFilter example, Long entityAttributeId) {
        this.example = example;
        this.entityAttributeId = entityAttributeId;
    }

    @Override
    public String getObject() {
        return example.getAttributeExample(entityAttributeId).getValue();
    }

    @Override
    public void setObject(String object) {
        example.getAttributeExample(entityAttributeId).setValue(object);
    }

    @Override
    public void detach() {
    }
}
