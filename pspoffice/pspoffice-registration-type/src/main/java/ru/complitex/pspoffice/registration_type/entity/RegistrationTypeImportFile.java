/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.complitex.pspoffice.registration_type.entity;

import ru.complitex.common.entity.IImportFile;

/**
 *
 * @author Artem
 */
public enum RegistrationTypeImportFile implements IImportFile {

    REGISTRATION_TYPE("registration_type.csv");
    private String fileName;

    private RegistrationTypeImportFile(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String getFileName() {
        return fileName;
    }
}
