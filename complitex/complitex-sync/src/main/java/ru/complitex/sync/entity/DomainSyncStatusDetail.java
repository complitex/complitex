package ru.complitex.sync.entity;

import ru.complitex.common.entity.IFixedIdType;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 25.04.2014 23:47
 */
public enum DomainSyncStatusDetail implements IFixedIdType{
    NEW(1);

    private Integer id;

    DomainSyncStatusDetail(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }
}
