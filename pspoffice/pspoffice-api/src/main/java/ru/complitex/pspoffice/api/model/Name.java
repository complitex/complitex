package ru.complitex.pspoffice.api.model;

/**
 * @author Anatoly A. Ivanov
 *         04.05.2017 18:02
 */
public class Name {
    private Long localeId;
    private String name;

    public Name() {
    }

    public Name(Long localeId, String name) {
        this.localeId = localeId;
        this.name = name;
    }

    public Long getLocaleId() {
        return localeId;
    }

    public void setLocaleId(Long localeId) {
        this.localeId = localeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
