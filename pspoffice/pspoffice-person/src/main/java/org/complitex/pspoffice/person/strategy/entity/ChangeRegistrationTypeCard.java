/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.entity;

import java.io.Serializable;
import org.complitex.dictionary.entity.DomainObject;

/**
 *
 * @author Artem
 */
public class ChangeRegistrationTypeCard implements Serializable {

    private DomainObject registrationType;
    private String explanation;

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public DomainObject getRegistrationType() {
        return registrationType;
    }

    public void setRegistrationType(DomainObject registrationType) {
        this.registrationType = registrationType;
    }
}
