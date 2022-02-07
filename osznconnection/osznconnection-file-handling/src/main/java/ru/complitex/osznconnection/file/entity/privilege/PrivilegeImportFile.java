package ru.complitex.osznconnection.file.entity.privilege;

import ru.complitex.common.entity.IImportFile;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 02.03.11 13:48
 */
public enum PrivilegeImportFile implements IImportFile{
    PRIVILEGE("privilege.csv");

    private String fileName;

    PrivilegeImportFile(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String getFileName() {
        return fileName;
    }
}
