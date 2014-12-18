package org.complitex.common.entity;

import org.complitex.common.util.Locales;

import java.io.Serializable;

public class StringCulture implements Serializable {
    private String entityTable;
    private Long id;
    private Long localeId;
    private String value;

    public StringCulture(Long localeId) {
        this.localeId = localeId;
    }

    public StringCulture(Long localeId, String value) {
        this.localeId = localeId;
        this.value = value;
    }

    public boolean isSystemLocale(){
        return Locales.getSystemLocaleId().equals(localeId);
    }

    public String getEntityTable() {
        return entityTable;
    }

    public void setEntityTable(String entityTable) {
        this.entityTable = entityTable;
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


