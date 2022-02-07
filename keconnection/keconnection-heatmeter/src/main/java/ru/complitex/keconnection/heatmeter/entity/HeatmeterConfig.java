package ru.complitex.keconnection.heatmeter.entity;

import ru.complitex.common.entity.IConfig;

import static ru.complitex.common.entity.DictionaryConfig.IMPORT_FILE_STORAGE_DIR;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 26.09.12 18:31
 */
public enum HeatmeterConfig implements IConfig{
    IMPORT_HEATMETER_DIR, IMPORT_PAYLOAD_DIR;

    @Override
    public String getGroupKey() {
        return "heatmeter";
    }
}
