package ru.complitex.common.entity;

import java.io.Serializable;

public class AttributeFilter implements Serializable {
    private Long attributeId;

    private Long entityAttributeId;

    private String value;

    private Long valueId;

    private Long localeId;

    public AttributeFilter(Long entityAttributeId) {
        this.entityAttributeId = entityAttributeId;
    }

    public AttributeFilter(Long entityAttributeId, String value) {
        this.entityAttributeId = entityAttributeId;
        this.value = value;
    }

    public AttributeFilter(Long entityAttributeId, String value, Long localeId) {
        this.entityAttributeId = entityAttributeId;
        this.value = value;
        this.localeId = localeId;
    }

    public AttributeFilter(Long entityAttributeId, Long valueId) {
        this.entityAttributeId = entityAttributeId;
        this.valueId = valueId;
    }

    public Long getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(Long attributeId) {
        this.attributeId = attributeId;
    }

    public Long getEntityAttributeId() {
        return entityAttributeId;
    }

    public void setEntityAttributeId(Long entityAttributeId) {
        this.entityAttributeId = entityAttributeId;
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

    public Long getLocaleId() {
        return localeId;
    }

    public void setLocaleId(Long localeId) {
        this.localeId = localeId;
    }
}