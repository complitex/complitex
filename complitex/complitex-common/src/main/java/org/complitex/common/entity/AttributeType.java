package org.complitex.common.entity;

import org.complitex.common.util.StringValueUtil;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AttributeType implements Serializable {
    private Long id;
    private Long entityId;
    private boolean mandatory;
    private Date startDate;
    private Date endDate;
    private Long attributeNameId;

    private List<StringValue> attributeNames;

    private List<AttributeValueType> attributeValueTypes;

    private boolean system;

    public AttributeValueType getAttributeValueType(long attributeValueTypeId){
        for(AttributeValueType valueType : getAttributeValueTypes()){
            if(valueType.getId().equals(attributeValueTypeId)){
                return valueType;
            }
        }
        return null;
    }

    public String getAttributeName(Locale locale){
        return StringValueUtil.getValue(attributeNames, locale);
    }

    public List<StringValue> getAttributeNames() {
        return attributeNames;
    }

    public void setAttributeNames(List<StringValue> attributeNames) {
        this.attributeNames = attributeNames;
    }

    public List<AttributeValueType> getAttributeValueTypes() {
        return attributeValueTypes;
    }

    public void setAttributeValueTypes(List<AttributeValueType> attributeValueTypes) {
        this.attributeValueTypes = attributeValueTypes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public Long getAttributeNameId() {
        return attributeNameId;
    }

    public void setAttributeNameId(Long attributeNameId) {
        this.attributeNameId = attributeNameId;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public boolean isSystem() {
        return system;
    }

    public void setSystem(boolean system) {
        this.system = system;
    }

    public boolean isObsolete() {
        return endDate != null;
    }
}
