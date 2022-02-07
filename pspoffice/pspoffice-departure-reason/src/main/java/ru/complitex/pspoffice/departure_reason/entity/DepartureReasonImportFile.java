/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.complitex.pspoffice.departure_reason.entity;

import ru.complitex.common.entity.IImportFile;

/**
 *
 * @author Artem
 */
public enum DepartureReasonImportFile implements IImportFile {

    DEPARTURE_REASON("departure_reason.csv");
    private String fileName;

    private DepartureReasonImportFile(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String getFileName() {
        return fileName;
    }
}
