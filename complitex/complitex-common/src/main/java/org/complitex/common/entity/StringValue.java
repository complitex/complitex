package org.complitex.common.entity;

import com.google.common.base.MoreObjects;

import java.io.Serializable;

public class StringValue implements Serializable {
    private Long pkId;

    private Long id;

    private String entityName;

    private Long localeId;
    private String value;

    public StringValue() {
    }

    public StringValue(Long localeId) {
        this.localeId = localeId;
    }

    public StringValue(Long localeId, String value) {
        this.localeId = localeId;
        this.value = value;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLocaleId() {
        return localeId;
    }

    public void setLocaleId(Long localeId) {
        this.localeId = localeId;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).omitNullValues()
                .add("pkId", pkId)
                .add("id", id)
                .add("entityName", entityName)
                .add("localeId", localeId)
                .add("value", value)
                .toString();
    }
}


