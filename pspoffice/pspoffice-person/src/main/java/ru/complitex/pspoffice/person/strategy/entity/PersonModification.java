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
public class PersonModification implements Serializable {

    private final Map<Long, ModificationType> attributeModificationMap = newHashMap();
    private final Map<Long, ChildModification> childrenModificationMap = newHashMap();
    private DocumentModification documentModification;
    private boolean childRemoved;
    private Long editedByUserId;
    private String explanation;

    public PersonModification() {
    }

    public ModificationType getModificationType(long entityAttributeId) {
        return attributeModificationMap.get(entityAttributeId);
    }

    public void addAttributeModification(long entityAttributeId, ModificationType modificationType) {
        attributeModificationMap.put(entityAttributeId, modificationType);
    }

    public ModificationType getChildModificationType(long childId) {
        return childrenModificationMap.get(childId).getModificationType();
    }

    public void addChildModificationType(long childId, ModificationType childModificationType) {
        childrenModificationMap.put(childId, new ChildModification(childModificationType));
    }

    public DocumentModification getDocumentModification() {
        return documentModification;
    }

    public void setDocumentModification(DocumentModification documentModification) {
        this.documentModification = documentModification;
    }

    public boolean isChildRemoved() {
        return childRemoved;
    }

    public void setChildRemoved(boolean childRemoved) {
        this.childRemoved = childRemoved;
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
