package org.complitex.osznconnection.file.entity;

import org.complitex.common.mybatis.IFixedIdType;

/**
 * @author inheaven on 008 08.12.16.
 */
public enum RequestFileSubType implements IFixedIdType{
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
