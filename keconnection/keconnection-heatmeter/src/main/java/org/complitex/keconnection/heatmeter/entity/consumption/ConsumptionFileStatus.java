package org.complitex.keconnection.heatmeter.entity.consumption;

import org.complitex.common.mybatis.FixedIdTypeHandler;
import org.complitex.common.mybatis.IFixedIdType;

/**
 * @author inheaven on 016 16.03.15 19:06
 */
@FixedIdTypeHandler
public enum ConsumptionFileStatus implements IFixedIdType {
    LOADED(1L);

    private Long id;

    ConsumptionFileStatus(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }
}
