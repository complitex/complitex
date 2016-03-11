package org.complitex.osznconnection.file.entity;

import org.complitex.common.entity.PersonalName;

/**
 * @author Anatoly Ivanov java@inheaven.ru
 *         Date: 15.08.13 21:00
 */
public abstract class AbstractAccountRequest<E extends Enum> extends AbstractAddressRequest<E> {
    private String accountNumber;

    private String lastName;
    private String firstName;
    private String middleName;

    private String inn;
    private String passport;

    public PersonalName getPersonalName(){
        return new PersonalName(getFirstName(), getMiddleName(), getLastName());
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
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

    public String getInn() {
        return inn;
    }

    public void setInn(String inn) {
        this.inn = inn;
    }

    public String getPassport() {
        return passport;
    }

    public void setPassport(String passport) {
        this.passport = passport;
    }
}
