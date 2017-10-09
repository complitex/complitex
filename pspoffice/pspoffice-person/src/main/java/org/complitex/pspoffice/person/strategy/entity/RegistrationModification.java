/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.entity;

import java.io.Serializable;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

/**
 *
 * @author Artem
 */
public class RegistrationModification implements Serializable {

    private final Map<Long, ModificationType> attributeModificationMap = newHashMap();
    private ModificationType modificationType;
    private Long editedByUserId;
    private String explanation;

    public ModificationType getModificationType() {
        return modificationType;
    }

    public RegistrationModification setModificationType(ModificationType modificationType) {
        this.modificationType = modificationType;
        return this;
    }

    public ModificationType getAttributeModificationType(long entityAttributeId) {
        return attributeModificationMap.get(entityAttributeId);
    }

    public RegistrationModification addAttributeModification(long entityAttributeId, ModificationType modificationType) {
        attributeModificationMap.put(entityAttributeId, modificationType);
        return this;
    }

    public Long getEditedByUserId() {
        return editedByUserId;
    }

    public RegistrationModification setEditedByUserId(Long editedByUserId) {
        this.editedByUserId = editedByUserId;
        return this;
    }

    public String getExplanation() {
        return explanation;
    }

    public RegistrationModification setExplanation(String explanation) {
        this.explanation = explanation;
        return this;
    }
}
