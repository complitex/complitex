/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.complitex.pspoffice.person.strategy.entity;

import java.io.Serializable;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

/**
 *
 * @author Artem
 */
public class DocumentModification implements Serializable {

    private final Map<Long, ModificationType> attributeModificationMap = newHashMap();
    private final ModificationType modificationType;

    public DocumentModification(boolean added) {
        this.modificationType = added ? ModificationType.ADD : ModificationType.NONE;
    }

    public DocumentModification() {
        this.modificationType = ModificationType.NONE;
    }

    public ModificationType getModificationType() {
        return modificationType;
    }

    public ModificationType getAttributeModificationType(long entityAttributeId) {
        return attributeModificationMap.get(entityAttributeId);
    }

    public void addAttributeModification(long entityAttributeId, ModificationType modificationType) {
        attributeModificationMap.put(entityAttributeId, modificationType);
    }
}
