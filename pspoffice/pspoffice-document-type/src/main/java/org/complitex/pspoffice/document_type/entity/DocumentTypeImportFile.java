/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.document_type.entity;

import org.complitex.dictionary.entity.IImportFile;

/**
 *
 * @author Artem
 */
public enum DocumentTypeImportFile implements IImportFile {

    DOCUMENT_TYPE("document_type.csv");
    private String fileName;

    private DocumentTypeImportFile(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String getFileName() {
        return fileName;
    }
}
