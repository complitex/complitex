package org.complitex.keconnection.heatmeter.entity;

import org.complitex.common.entity.IFixedIdType;

public enum HeatmeterBindingStatus implements IFixedIdType {
    UNBOUND(1), ORGANIZATION_NOT_FOUND(2), BUILDING_NOT_FOUND(3), BINDING_ERROR(4), MORE_ONE_EXTERNAL_HEATMETER(5),
    NO_EXTERNAL_HEATMETERS(6), BOUND(7);

    private Integer id;

    private HeatmeterBindingStatus(Integer id) {
        this.id = id;
    }

    @Override
    public Integer getId() {
        return id;
    }
}
