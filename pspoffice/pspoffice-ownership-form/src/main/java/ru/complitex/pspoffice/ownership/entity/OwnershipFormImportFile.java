/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.complitex.pspoffice.ownership.entity;

import ru.complitex.common.entity.IImportFile;

/**
 *
 * @author Artem
 */
public enum OwnershipFormImportFile implements IImportFile {

    OWNERSHIP_FORM("ownership_form.csv");
    private String fileName;

    private OwnershipFormImportFile(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String getFileName() {
        return fileName;
    }
}
