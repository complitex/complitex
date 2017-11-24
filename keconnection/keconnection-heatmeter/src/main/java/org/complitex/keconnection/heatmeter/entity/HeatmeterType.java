package org.complitex.keconnection.heatmeter.entity;

import org.complitex.common.entity.IFixedIdType;
import org.complitex.keconnection.heatmeter.strategy.HeatmeterTypeStrategy;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 14.09.12 16:46
 */
public enum HeatmeterType implements IFixedIdType {
    HEATING(HeatmeterTypeStrategy.HEATING), HEATING_AND_WATER(HeatmeterTypeStrategy.HEATING_AND_WATER);

    private Integer id;

    private HeatmeterType(Integer id) {
        this.id = id;
    }

    @Override
    public Integer getId() {
        return id;
    }
    
    public String getShortName() {
        return name() + "_SHORT";
    }
}
