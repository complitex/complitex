package org.complitex.osznconnection.file.entity;

import org.complitex.common.entity.IFixedIdType;

/**
 * @author inheaven on 027 27.01.17.
 */
public enum RequestFileGroupType implements IFixedIdType {
    SUBSIDY_GROUP(1), PRIVILEGE_GROUP(2);

    private Integer id;

    RequestFileGroupType(Integer id) {
        this.id = id;
    }

    @Override
    public Integer getId() {
        return id;
    }
}
