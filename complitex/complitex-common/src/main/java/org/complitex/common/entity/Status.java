package org.complitex.common.entity;

import org.complitex.common.mybatis.IFixedIdType;

public enum Status implements IFixedIdType{
    INACTIVE(0), ACTIVE(1),  ARCHIVE(2);

    private Integer id;

    Status(Integer id) {
        this.id = id;
    }

    @Override
    public Integer getId() {
        return id;
    }
}
