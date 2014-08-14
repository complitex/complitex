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
class ChildModification implements Serializable {

    private ModificationType modificationType;

    ChildModification(ModificationType modificationType) {
        if (!(modificationType == ModificationType.ADD || modificationType == ModificationType.NONE)) {
            throw new IllegalArgumentException("Child modification type can be only ADD or NONE.");
        }
        this.modificationType = modificationType;
    }

    public ModificationType getModificationType() {
        return modificationType;
    }
}
