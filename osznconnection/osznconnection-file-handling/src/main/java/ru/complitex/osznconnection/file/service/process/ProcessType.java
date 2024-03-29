package ru.complitex.osznconnection.file.service.process;

/**
* @author Anatoly A. Ivanov java@inheaven.ru
*         Date: 13.04.11 18:36
*/
public enum ProcessType {
    LOAD_GROUP,
    BIND_GROUP,
    FILL_GROUP,
    SAVE_GROUP,

    LOAD_ACTUAL_PAYMENT,
    BIND_ACTUAL_PAYMENT,
    FILL_ACTUAL_PAYMENT,
    SAVE_ACTUAL_PAYMENT,

    LOAD_SUBSIDY,
    BIND_SUBSIDY,
    FILL_SUBSIDY,
    SAVE_SUBSIDY,
    EXPORT_SUBSIDY_MASTER_DATA,
    EXPORT_SUBSIDY,
    DOWNLOAD_SUBSIDY,

    LOAD_FACILITY_STREET_TYPE_REFERENCE,
    LOAD_FACILITY_STREET_REFERENCE,
    LOAD_FACILITY_TARIF_REFERENCE,

    LOAD_SUBSIDY_TARIF,

    LOAD_PRIVILEGE_GROUP,
    BIND_PRIVILEGE_GROUP,
    FILL_PRIVILEGE_GROUP,
    SAVE_PRIVILEGE_GROUP,

    LOAD_PRIVILEGE_PROLONGATION,
    BIND_PRIVILEGE_PROLONGATION,
    FILL_PRIVILEGE_PROLONGATION,
    SAVE_PRIVILEGE_PROLONGATION,
    EXPORT_PRIVILEGE_PROLONGATION,

    LOAD_FACILITY_FORM2,
    BIND_FACILITY_FORM2,
    FILL_FACILITY_FORM2,
    SAVE_FACILITY_FORM2,

    LOAD_FACILITY_LOCAL,
    BIND_FACILITY_LOCAL,
    FILL_FACILITY_LOCAL,
    SAVE_FACILITY_LOCAL,

    LOAD_OSCHADBANK_REQUEST,
    FILL_OSCHADBANK_REQUEST,
    SAVE_OSCHADBANK_REQUEST,

    LOAD_OSCHADBANK_RESPONSE,
    EXPORT_OSCHADBANK_RESPONSE,
    SAVE_OSCHADBANK_RESPONSE,

    LOAD_DEBT,
    BIND_DEBT,
    FILL_DEBT,
    SAVE_DEBT
}
