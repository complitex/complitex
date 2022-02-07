package ru.complitex.osznconnection.file.service.process;


import ru.complitex.osznconnection.organization.strategy.OsznOrganizationStrategy;

public enum RequestFileDirectoryType {

    LOAD_PAYMENT_BENEFIT_FILES_DIR(OsznOrganizationStrategy.LOAD_PAYMENT_BENEFIT_FILES_DIR),
    SAVE_PAYMENT_BENEFIT_FILES_DIR(OsznOrganizationStrategy.SAVE_PAYMENT_BENEFIT_FILES_DIR),
    LOAD_ACTUAL_PAYMENT_DIR(OsznOrganizationStrategy.LOAD_ACTUAL_PAYMENT_DIR),
    SAVE_ACTUAL_PAYMENT_DIR(OsznOrganizationStrategy.SAVE_ACTUAL_PAYMENT_DIR),
    LOAD_SUBSIDY_DIR(OsznOrganizationStrategy.LOAD_SUBSIDY_DIR),
    SAVE_SUBSIDY_DIR(OsznOrganizationStrategy.SAVE_SUBSIDY_DIR),
    LOAD_DWELLING_CHARACTERISTICS_DIR(OsznOrganizationStrategy.LOAD_DWELLING_CHARACTERISTICS_DIR),
    SAVE_DWELLING_CHARACTERISTICS_DIR(OsznOrganizationStrategy.SAVE_DWELLING_CHARACTERISTICS_DIR),
    LOAD_FACILITY_SERVICE_TYPE_DIR(OsznOrganizationStrategy.LOAD_FACILITY_SERVICE_TYPE_DIR),
    SAVE_FACILITY_SERVICE_TYPE_DIR(OsznOrganizationStrategy.SAVE_FACILITY_SERVICE_TYPE_DIR),
    SAVE_FACILITY_FORM2_DIR(OsznOrganizationStrategy.SAVE_FACILITY_FORM2_DIR),
    SAVE_FACILITY_LOCAL_DIR(OsznOrganizationStrategy.SAVE_FACILITY_LOCAL_DIR),
    EXPORT_SUBSIDY_DIR(OsznOrganizationStrategy.ROOT_EXPORT_DIRECTORY),
    REFERENCES_DIR(OsznOrganizationStrategy.REFERENCES_DIR),
    LOAD_PRIVILEGE_PROLONGATION_DIR(OsznOrganizationStrategy.LOAD_PRIVILEGE_PROLONGATION_DIR),
    LOAD_OSCHADBANK_REQUEST_DIR(OsznOrganizationStrategy.LOAD_OSCHADBANK_REQUEST_DIR),
    SAVE_OSCHADBANK_REQUEST_DIR(OsznOrganizationStrategy.SAVE_OSCHADBANK_REQUEST_DIR),
    LOAD_OSCHADBANK_RESPONSE_DIR(OsznOrganizationStrategy.LOAD_OSCHADBANK_RESPONSE_DIR),
    SAVE_OSCHADBANK_RESPONSE_DIR(OsznOrganizationStrategy.SAVE_OSCHADBANK_RESPONSE_DIR),
    LOAD_DEBT_DIR(OsznOrganizationStrategy.LOAD_DEBT_DIR),
    SAVE_DEBT_DIR(OsznOrganizationStrategy.SAVE_DEBT_DIR);
    
    private long entityAttributeId;

    private RequestFileDirectoryType(long entityAttributeId) {
        this.entityAttributeId = entityAttributeId;
    }

    public long getAttributeTypeId() {
        return entityAttributeId;
    }
}
