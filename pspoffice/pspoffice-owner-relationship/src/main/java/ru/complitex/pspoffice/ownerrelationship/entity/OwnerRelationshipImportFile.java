/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.complitex.pspoffice.ownerrelationship.entity;

import ru.complitex.common.entity.IImportFile;

/**
 *
 * @author Artem
 */
public enum OwnerRelationshipImportFile implements IImportFile {

    OWNER_RELATIONSHIP("owner_relationship.csv");
    private String fileName;

    private OwnerRelationshipImportFile(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String getFileName() {
        return fileName;
    }
}
