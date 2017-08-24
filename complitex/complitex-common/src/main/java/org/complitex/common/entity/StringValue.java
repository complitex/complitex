package org.complitex.common.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

public class StringValue implements Serializable {
    @JsonIgnore
    private Long pkId;

    private String entity;
    private Long id;
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

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entityName) {
        this.entity = entityName;
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


