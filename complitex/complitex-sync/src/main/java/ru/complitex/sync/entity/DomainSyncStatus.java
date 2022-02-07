package ru.complitex.sync.entity;

import ru.complitex.common.entity.IFixedIdType;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 25.04.2014 23:47
 */
public enum DomainSyncStatus implements IFixedIdType{
    LOADED(1), SYNCHRONIZED(2), DEFERRED(3), ERROR(4);

    private Integer id;

    DomainSyncStatus(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }
}
