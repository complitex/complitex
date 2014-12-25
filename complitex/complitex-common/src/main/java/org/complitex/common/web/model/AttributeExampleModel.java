package org.complitex.common.web.model;

import org.apache.wicket.model.IModel;
import org.complitex.common.entity.DomainObjectFilter;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 30.01.14 2:37
 */
public class AttributeExampleModel implements IModel<String> {
    private DomainObjectFilter example;
    private Long attributeTypeId;

    public AttributeExampleModel(DomainObjectFilter example, Long attributeTypeId) {
        this.example = example;
        this.attributeTypeId = attributeTypeId;
    }

    @Override
    public String getObject() {
        return example.getAttributeExample(attributeTypeId).getValue();
    }

    @Override
    public void setObject(String object) {
        example.getAttributeExample(attributeTypeId).setValue(object);
    }

    @Override
    public void detach() {
    }
}
