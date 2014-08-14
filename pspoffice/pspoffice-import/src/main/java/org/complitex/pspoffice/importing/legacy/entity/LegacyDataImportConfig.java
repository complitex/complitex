/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.importing.legacy.entity;

import org.complitex.dictionary.entity.IConfig;

/**
 *
 * @author Artem
 */
public enum LegacyDataImportConfig implements IConfig{
    
    DEFAULT_LEGACY_IMPORT_FILE_DIR("c:\\storage\\pspoffice\\legacy_data\\import"),
    DEFAULT_LEGACY_IMPORT_FILE_ERRORS_DIR("c:\\storage\\pspoffice\\legacy_data\\import\\errors");
    
    private String defaultValue;
    
    private LegacyDataImportConfig(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public String getDefaultValue() {
        return defaultValue;
    }

    @Override
    public String getGroupKey() {
        return "legacy_data_import";
    }
    
}
