package org.complitex.common.entity;

import java.io.Serializable;

/**
 * @author inheaven on 015 15.04.15 16:17
 */
public class PersonalName implements Serializable{
    private String firstName;
    private String middleName;
    private String lastName;

    public PersonalName() {
    }

    public PersonalName(String firstName, String middleName, String lastName) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return  lastName + " " + firstName + " " + middleName;
    }
}
