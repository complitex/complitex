package org.complitex.keconnection.heatmeter.entity.consumption;

import org.complitex.common.mybatis.FixedIdTypeHandler;
import org.complitex.common.mybatis.IFixedIdType;

/**
 * @author inheaven on 20.03.2015 2:25.
 */
@FixedIdTypeHandler
public enum  ConsumptionStatus implements IFixedIdType{
    LOADED(1L);

    private Long id;

    ConsumptionStatus(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }
}
