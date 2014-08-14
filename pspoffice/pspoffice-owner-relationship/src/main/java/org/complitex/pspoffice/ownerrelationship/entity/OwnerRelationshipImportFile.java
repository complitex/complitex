/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.ownerrelationship.entity;

import org.complitex.dictionary.entity.IImportFile;

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
