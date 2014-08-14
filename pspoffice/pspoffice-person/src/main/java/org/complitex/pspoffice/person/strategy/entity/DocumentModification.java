/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.entity;

import java.util.Map;
import java.io.Serializable;
import static com.google.common.collect.Maps.*;

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

    public ModificationType getAttributeModificationType(long attributeTypeId) {
        return attributeModificationMap.get(attributeTypeId);
    }

    public void addAttributeModification(long attributeTypeId, ModificationType modificationType) {
        attributeModificationMap.put(attributeTypeId, modificationType);
    }
}
