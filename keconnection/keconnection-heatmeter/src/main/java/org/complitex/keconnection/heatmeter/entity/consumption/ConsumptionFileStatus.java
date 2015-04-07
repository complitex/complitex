package org.complitex.keconnection.heatmeter.entity.consumption;

import org.complitex.common.mybatis.FixedIdTypeHandler;
import org.complitex.common.mybatis.IFixedIdType;

/**
 * @author inheaven on 016 16.03.15 19:06
 */
@FixedIdTypeHandler
public enum ConsumptionFileStatus implements IFixedIdType {
    LOADED(11), LOADING(12), LOAD_ERROR(13),
    BOUND(31), BINDING(32), BIND_ERROR(33);

    private long id;

    ConsumptionFileStatus(long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }
}
