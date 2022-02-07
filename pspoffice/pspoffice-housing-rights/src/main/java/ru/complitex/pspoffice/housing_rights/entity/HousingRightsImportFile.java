/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.complitex.pspoffice.housing_rights.entity;

import ru.complitex.common.entity.IImportFile;

/**
 *
 * @author Artem
 */
public enum HousingRightsImportFile implements IImportFile {

    HOUSING_RIGHTS("housing_rights.csv");
    private String fileName;

    private HousingRightsImportFile(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String getFileName() {
        return fileName;
    }
}
