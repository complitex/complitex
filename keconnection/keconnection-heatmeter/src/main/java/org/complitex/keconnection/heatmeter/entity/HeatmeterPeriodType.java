package org.complitex.keconnection.heatmeter.entity;

import org.complitex.common.mybatis.IFixedIdType;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 14.09.12 19:25
 */
public enum HeatmeterPeriodType implements IFixedIdType{
    OPERATION(1), CONNECTION(2), PAYLOAD(3), INPUT(4);

    private Integer id;

    private HeatmeterPeriodType(Integer id) {
        this.id = id;
    }

    @Override
    public Integer getId() {
        return id;
    }
}
