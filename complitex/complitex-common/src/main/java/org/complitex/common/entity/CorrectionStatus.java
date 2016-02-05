package org.complitex.common.entity;

import org.complitex.common.mybatis.IFixedIdType;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 15.10.13 16:39
 */
public enum CorrectionStatus implements IFixedIdType {
    LOCAL(1), NEW(2), NEW_NAME(3), DUPLICATE(4);

    private Integer id;

    private CorrectionStatus(Integer id) {
        this.id = id;
    }

    @Override
    public Integer getId() {
        return id;
    }
}
