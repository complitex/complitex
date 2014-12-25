package org.complitex.common.entity;

import java.io.Serializable;

public class StringLocale implements Serializable {

    public static final StringLocale RU = new StringLocale(1L, "ru", true);
    public static final StringLocale UK = new StringLocale(2L, "uk", false);

    private Long id;
    private String language;
    private boolean system;

    public StringLocale(Long id, String language, boolean system) {
        this.id = id;
        this.language = language;
        this.system = system;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public boolean isSystem() {
        return system;
    }

    public void setSystem(boolean system) {
        this.system = system;
    }
}
