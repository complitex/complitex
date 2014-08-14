/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.entity;

import java.io.Serializable;

/**
 *
 * @author Artem
 */
public class PersonName implements Serializable {

    public enum PersonNameType {

        LAST_NAME, FIRST_NAME, MIDDLE_NAME
    }
    private PersonNameType personNameType;
    private long id;
    private String name;
    private long localeId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getLocaleId() {
        return localeId;
    }

    public void setLocaleId(long localeId) {
        this.localeId = localeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PersonNameType getPersonNameType() {
        return personNameType;
    }

    public void setPersonNameType(PersonNameType personNameType) {
        this.personNameType = personNameType;
    }
}
