package org.complitex.keconnection.heatmeter.entity;

import org.complitex.common.mybatis.IFixedIdType;
import org.complitex.keconnection.heatmeter.strategy.HeatmeterPeriodTypeStrategy;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 14.09.12 19:25
 */
public enum HeatmeterPeriodSubType implements IFixedIdType{
    OPERATING(HeatmeterPeriodTypeStrategy.OPERATING), ADJUSTMENT(HeatmeterPeriodTypeStrategy.ADJUSTMENT);

    private Integer id;

    private HeatmeterPeriodSubType(Integer id) {
        this.id = id;
    }

    @Override
    public Integer getId() {
        return id;
    }
}
