/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.complitex.pspoffice.importing.legacy.entity;

import ru.complitex.common.entity.IConfig;

/**
 *
 * @author Artem
 */
public enum LegacyDataImportConfig implements IConfig{
    
    DEFAULT_LEGACY_IMPORT_FILE_DIR, DEFAULT_LEGACY_IMPORT_FILE_ERRORS_DIR;

    @Override
    public String getGroupKey() {
        return "legacy_data_import";
    }
    
}
