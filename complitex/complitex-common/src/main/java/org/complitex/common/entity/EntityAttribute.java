package org.complitex.common.entity;

import org.complitex.common.util.StringValueUtil;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EntityAttribute implements Serializable {
    private Long id;
    private Long entityId;
    private Date startDate;
    private Date endDate;
    private Long nameId;

    private boolean required;

    private List<StringValue> names;

    private boolean system;

    private ValueType valueType;

    private Long referenceId;

    public String getName(Locale locale){
        return StringValueUtil.getValue(names, locale);
    }

    public List<StringValue> getNames() {
        return names;
    }

    public void setNames(List<StringValue> names) {
        this.names = names;
    }

    public ValueType getValueType() {
        return valueType;
    }

    public void setValueType(ValueType valueType) {
        this.valueType = valueType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
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

    public Long getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Long referenceId) {
        this.referenceId = referenceId;
    }
}
