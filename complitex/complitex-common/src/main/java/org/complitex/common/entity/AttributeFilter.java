package org.complitex.common.entity;

import java.io.Serializable;

public class AttributeFilter implements Serializable {
    private Long attributeId;

    private Long attributeTypeId;

    private String value;

    private Long valueId;

    public AttributeFilter(Long attributeTypeId) {
        this.attributeTypeId = attributeTypeId;
    }

    public AttributeFilter(Long attributeTypeId, String value) {
        this.attributeTypeId = attributeTypeId;
        this.value = value;
    }

    public AttributeFilter(Long attributeTypeId, Long valueId) {
        this.attributeTypeId = attributeTypeId;
        this.valueId = valueId;
    }

    public Long getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(Long attributeId) {
        this.attributeId = attributeId;
    }

    public Long getAttributeTypeId() {
        return attributeTypeId;
    }

    public void setAttributeTypeId(Long attributeTypeId) {
        this.attributeTypeId = attributeTypeId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Long getValueId() {
        return valueId;
    }

    public void setValueId(Long valueId) {
        this.valueId = valueId;
    }
}