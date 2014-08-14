/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.report.entity;

import java.io.Serializable;
import java.util.Date;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.pspoffice.person.strategy.entity.Person;

/**
 *
 * @author Artem
 */
public class FamilyMember implements Serializable {

    private Person person;
    private DomainObject relation;
    private Date registrationDate;
    private String passport;

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public DomainObject getRelation() {
        return relation;
    }

    public void setRelation(DomainObject relation) {
        this.relation = relation;
    }

    public String getPassport() {
        return passport;
    }

    public void setPassport(String passport) {
        this.passport = passport;
    }
}
