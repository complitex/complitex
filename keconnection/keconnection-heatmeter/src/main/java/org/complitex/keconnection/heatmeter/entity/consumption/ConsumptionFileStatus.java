package org.complitex.keconnection.heatmeter.entity.consumption;

import org.complitex.common.mybatis.FixedIdTypeHandler;
import org.complitex.common.mybatis.IFixedIdType;

/**
 * @author inheaven on 016 16.03.15 19:06
 */
@FixedIdTypeHandler
public enum ConsumptionFileStatus implements IFixedIdType {
    LOADED(1), LOADING(2), LOAD_ERROR(3),
    BOUND(11), BINDING(12), BIND_ERROR(13);

    private long id;

    ConsumptionFileStatus(long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }
}
