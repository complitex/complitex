package ru.complitex.keconnection.heatmeter.entity;

import ru.complitex.common.entity.IFixedIdType;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 26.10.12 16:06
 */
public enum HeatmeterStatus implements IFixedIdType {
    OFF(0), OPERATION(1), ADJUSTMENT(2), REMOVED(3);

    private Integer id;

    private HeatmeterStatus(Integer id) {
        this.id = id;
    }

    @Override
    public Integer getId() {
        return id;
    }
}
