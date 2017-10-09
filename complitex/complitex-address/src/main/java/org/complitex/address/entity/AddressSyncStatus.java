package org.complitex.address.entity;

import org.complitex.common.entity.IFixedIdType;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 25.04.2014 23:47
 */
public enum AddressSyncStatus implements IFixedIdType{
    LOCAL(1), NEW(2), NEW_NAME(3), DUPLICATE(4), ARCHIVAL(5), EXTERNAL_DUPLICATE(6);

    private Integer id;

    AddressSyncStatus(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }
}
