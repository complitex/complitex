package org.complitex.common.entity;

import org.complitex.common.util.StringValueUtil;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EntityAttribute implements Serializable {
    private Long id;
    private Long entityId;
    private boolean mandatory;
    private Date startDate;
    private Date endDate;
    private Long nameId;

    private List<StringValue> names;

    private List<ValueType> valueTypes;

    private boolean system;

    public ValueType getAttributeValueType(long attributeValueTypeId){
        for(ValueType valueType : getValueTypes()){
            if(valueType.getId().equals(attributeValueTypeId)){
                return valueType;
            }
        }
        return null;
    }

    public String getAttributeName(Locale locale){
        return StringValueUtil.getValue(names, locale);
    }

    public List<StringValue> getNames() {
        return names;
    }

    public void setNames(List<StringValue> names) {
        this.names = names;
    }

    public List<ValueType> getValueTypes() {
        return valueTypes;
    }

    public void setValueTypes(List<ValueType> valueTypes) {
        this.valueTypes = valueTypes;
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

    public Long getNameId() {
        return nameId;
    }

    public void setNameId(Long nameId) {
        this.nameId = nameId;
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
