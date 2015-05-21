package org.complitex.common.entity;

import org.complitex.common.util.Locales;
import org.complitex.common.util.StringCultures;

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

    private StatusType status = StatusType.ACTIVE;

    private List<StringCulture> stringCultures;

    public Attribute() {
    }

    public Attribute(Long attributeTypeId, Long attributeId) {
        this.attributeTypeId = attributeTypeId;
        this.attributeId = attributeId;

        this.valueTypeId = attributeTypeId;
    }

    public StringCulture getStringCulture(Long localeId){
        if (stringCultures != null){
            for (StringCulture sc: stringCultures){
                if (sc.getLocaleId().equals(localeId)){
                    return sc;
                }
            }
        }

        return null;
    }

    public void setStringValue(String value, long localeId){
        for (StringCulture string : stringCultures) {
            if (string.getLocaleId().equals(localeId) || (string.isSystemLocale() && string.getValue() == null)) {
                string.setValue(value);
            }
        }
    }

    public String getStringValue(){
        StringCulture stringCulture = getStringCulture(Locales.getSystemLocaleId());

        return stringCulture != null ? stringCulture.getValue() : null;
    }

    public String getStringValue(java.util.Locale locale){
        return StringCultures.getValue(stringCultures, locale);
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

    public StatusType getStatus() {
        return status;
    }

    public void setStatus(StatusType status) {
        this.status = status;
    }

    public Long getValueId() {
        return valueId;
    }

    public void setValueId(Long valueId) {
        this.valueId = valueId;
    }

    public List<StringCulture> getStringCultures() {
        return stringCultures;
    }

    public void setStringCultures(List<StringCulture> stringCultures) {
        this.stringCultures = stringCultures;
    }

    public Long getValueTypeId() {
        return valueTypeId;
    }

    public void setValueTypeId(Long valueTypeId) {
        this.valueTypeId = valueTypeId;
    }
}
