package org.complitex.common.entity;

import org.complitex.common.util.Locales;
import org.complitex.common.util.StringValueUtil;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Attribute implements Serializable {
    private Long pkId;
    private String entityName;
    private Long attributeId;
    private Long objectId;
    private Long attributeTypeId;
    private Long valueId;
    private Long valueTypeId;
    private Date startDate;
    private Date endDate;

    private Status status = Status.ACTIVE;

    private List<StringValue> stringValues;

    public Attribute() {
    }

    public Attribute(Long attributeTypeId, Long attributeId) {
        this.attributeTypeId = attributeTypeId;
        this.attributeId = attributeId;

        this.valueTypeId = attributeTypeId;
    }

    public StringValue getStringValue(Long localeId){
        if (stringValues != null){
            for (StringValue sc: stringValues){
                if (sc.getLocaleId().equals(localeId)){
                    return sc;
                }
            }
        }

        return null;
    }

    public String getStringValue(){
        StringValue stringValue = getStringValue(Locales.getSystemLocaleId());

        return stringValue != null ? stringValue.getValue() : null;
    }

    public String getStringValue(java.util.Locale locale){
        StringValue stringValue = getStringValue(Locales.getLocaleId(locale));

        return stringValue != null ? stringValue.getValue() : null;
    }

    public void setStringValue(String value, long localeId){
        if (stringValues == null){
            stringValues = StringValueUtil.newStringValues();
        }

        stringValues.stream()
                .filter(s -> s.getLocaleId().equals(localeId) || (s.isSystemLocale() && s.getValue() == null))
                .forEach(s -> s.setValue(value));
    }

    public Long getPkId() {
        return pkId;
    }

    public void setPkId(Long pkId) {
        this.pkId = pkId;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
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

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Long getObjectId() {
        return objectId;
    }

    public void setObjectId(Long entityId) {
        this.objectId = entityId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Long getValueId() {
        return valueId;
    }

    public void setValueId(Long valueId) {
        this.valueId = valueId;
    }

    public List<StringValue> getStringValues() {
        return stringValues;
    }

    public void setStringValues(List<StringValue> stringValues) {
        this.stringValues = stringValues;
    }

    public Long getValueTypeId() {
        return valueTypeId;
    }

    public void setValueTypeId(Long valueTypeId) {
        this.valueTypeId = valueTypeId;
    }
}
