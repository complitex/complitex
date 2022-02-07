package ru.complitex.osznconnection.file.entity;

import ru.complitex.common.entity.IFixedIdType;

/**
 * @author inheaven on 008 08.12.16.
 */
public enum RequestFileSubType implements IFixedIdType{
    PRIVILEGE_PROLONGATION_S(1401), PRIVILEGE_PROLONGATION_P(1402),
    FACILITY_LOCAL_JANITOR(1501), FACILITY_LOCAL_COMPENSATION(1502);

    private Integer id;

    RequestFileSubType(Integer id) {
        this.id = id;
    }

    @Override
    public Integer getId() {
        return id;
    }
}
