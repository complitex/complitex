package ru.complitex.pspoffice.api.model;

/**
 * @author Anatoly A. Ivanov
 *         04.05.2017 18:02
 */
public class AddressName {
    private String locale;
    private String name;

    public AddressName() {
    }

    public AddressName(String locale, String name) {
        this.locale = locale;
        this.name = name;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
