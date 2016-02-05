package org.complitex.keconnection.organization.entity;

import org.complitex.common.entity.IImportFile;

/**
 *
 * @author Artem
 */
public enum OrganizationImportFile implements IImportFile {

    ORGANIZATION("orgs.csv");
    private String fileName;

    OrganizationImportFile(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String getFileName() {
        return fileName;
    }
}
