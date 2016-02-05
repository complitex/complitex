package org.complitex.keconnection.heatmeter.entity;

import org.complitex.common.mybatis.IFixedIdType;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 24.10.12 18:37
 */
public enum HeatmeterConsumptionStatus implements IFixedIdType {
    NOT_LOADED(0), LOADED(1);

    private Integer id;

    private HeatmeterConsumptionStatus(Integer id) {
        this.id = id;
    }

    @Override
    public Integer getId() {
        return id;
    }
}
