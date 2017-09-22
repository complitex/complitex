package org.complitex.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class EntityAttribute implements Serializable {
    private Long id;
    private Long entityId;
    private Date startDate;
    private Date endDate;
    private Long nameId;

    private boolean required;

    @JsonIgnore
    private List<StringValue> names;

    private boolean system;

    @JsonIgnore
    private ValueType valueType;

    private Long referenceId;

    public Map<Long, String> getLabels(){
        return names.stream().collect(Collectors.toMap(StringValue::getLocaleId, StringValue::getValue));
    }

    public Integer getValueTypeId(){
        return valueType.getId();
    }

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

    @JsonIgnore
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
