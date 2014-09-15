package org.complitex.osznconnection.file.entity;

import org.complitex.common.entity.IConfig;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 04.10.2010 11:12:46
 */
public enum FileHandlingConfig implements IConfig {
    PAYMENT_FILENAME_PREFIX("mask"),
    BENEFIT_FILENAME_PREFIX("mask"),
    PAYMENT_BENEFIT_FILENAME_SUFFIX("mask"),
    
    ACTUAL_PAYMENT_FILENAME_MASK("mask"),
    SUBSIDY_FILENAME_MASK("mask"),
    
    DWELLING_CHARACTERISTICS_INPUT_FILENAME_MASK("mask"),
    DWELLING_CHARACTERISTICS_OUTPUT_FILE_EXTENSION_PREFIX("mask"),
    
    FACILITY_SERVICE_TYPE_INPUT_FILENAME_MASK("mask"),
    FACILITY_SERVICE_TYPE_OUTPUT_FILE_EXTENSION_PREFIX("mask"),
    
    SUBSIDY_TARIF_FILENAME_MASK("mask"),
    
    FACILITY_STREET_TYPE_REFERENCE_FILENAME_MASK("mask"),
    FACILITY_STREET_REFERENCE_FILENAME_MASK("mask"),
    FACILITY_TARIF_REFERENCE_FILENAME_MASK("mask"),

    LOAD_THREAD_SIZE("thread"),
    BIND_THREAD_SIZE("thread"),
    FILL_THREAD_SIZE("thread"),
    SAVE_THREAD_SIZE("thread"),

    LOAD_BATCH_SIZE("batch"),
    BIND_BATCH_SIZE("batch"),
    FILL_BATCH_SIZE("batch"),

    LOAD_MAX_ERROR_COUNT("error"),
    BIND_MAX_ERROR_COUNT("error"),
    FILL_MAX_ERROR_COUNT("error"),
    SAVE_MAX_ERROR_COUNT("error"),
    
    DEFAULT_REQUEST_FILE_CITY("request_file_params");

    private String groupKey;

    FileHandlingConfig(String groupKey) {
        this.groupKey = groupKey;
    }

    @Override
    public String getGroupKey() {
        return groupKey;
    }
}