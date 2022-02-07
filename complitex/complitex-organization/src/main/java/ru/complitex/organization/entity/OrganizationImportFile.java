package ru.complitex.organization.entity;

import ru.complitex.common.entity.IImportFile;

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
