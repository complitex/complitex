/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.complitex.pspoffice.military.entity;

import ru.complitex.common.entity.IImportFile;

/**
 *
 * @author Artem
 */
public enum MilitaryServiceRelationImportFile implements IImportFile {

    MILITARY_SERVICE_RELATION("military_service_relation.csv");
    private String fileName;

    private MilitaryServiceRelationImportFile(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String getFileName() {
        return fileName;
    }
}
