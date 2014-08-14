/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.entity;

/**
 *
 * @author Artem
 */
public enum ModificationType {

    CHANGE, ADD, REMOVE, NONE;

    public String getCssClass() {
        return ModificationType.class.getSimpleName() + "_" + name();
    }
}
