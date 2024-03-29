package ru.complitex.common.entity;

import java.io.Serializable;

public class StringLocale implements Serializable {
    public static final StringLocale RU = new StringLocale(1L, "ru", true, false);
    public static final StringLocale UK = new StringLocale(2L, "uk", false, true);

    private Long id;
    private String language;
    private boolean system;
    private boolean alternative;

    public StringLocale() {
    }

    public StringLocale(Long id, String language, Boolean system, Boolean alternative) {
        this.id = id;
        this.language = language;
        this.system = system;
        this.alternative = alternative;
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

    public Boolean isSystem() {
        return system;
    }

    public void setSystem(Boolean system) {
        this.system = system;
    }

    public boolean isAlternative() {
        return alternative;
    }

    public void setAlternative(boolean alternative) {
        this.alternative = alternative;
    }
}
