/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.entity;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

/**
 *
 * @author Artem
 */
public class ApartmentCardModification implements Serializable {

    private final Map<Long, ModificationType> attributeModificationMap = newHashMap();
    private final Map<Long, RegistrationModification> registrationModificationMap = newHashMap();
    private Long editedByUserId;
    private String explanation;

    public ApartmentCardModification() {
    }

    public ModificationType getModificationType(long entityAttributeId) {
        return attributeModificationMap.get(entityAttributeId);
    }

    public void addAttributeModification(long entityAttributeId, ModificationType modificationType) {
        attributeModificationMap.put(entityAttributeId, modificationType);
    }

    public RegistrationModification getRegistrationModification(long registrationId) {
        return registrationModificationMap.get(registrationId);
    }

    public Collection<RegistrationModification> getRegistrationModifications() {
        return registrationModificationMap.values();
    }

    public void addRegistrationModification(long registrationId, RegistrationModification registrationModification) {
        registrationModificationMap.put(registrationId, registrationModification);
    }

    public Long getEditedByUserId() {
        return editedByUserId;
    }

    public void setEditedByUserId(Long editedByUserId) {
        this.editedByUserId = editedByUserId;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
}
