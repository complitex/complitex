package org.complitex.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

public class StringValue implements Serializable {
    @JsonIgnore
    private Long pkId;

    @JsonIgnore
    private Long id;

    @JsonIgnore
    private String entityName;

    private Long localeId;
    private String value;

    public StringValue() {
    }

    public StringValue(Long localeId) {
        this.localeId = localeId;
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
}


