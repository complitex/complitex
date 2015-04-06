package org.complitex.keconnection.heatmeter.entity.consumption;

import org.complitex.common.mybatis.FixedIdTypeHandler;
import org.complitex.common.mybatis.IFixedIdType;

/**
 * @author inheaven on 20.03.2015 2:25.
 */
@FixedIdTypeHandler
public enum  ConsumptionStatus implements IFixedIdType{
    LOADED(1), LOADING(2), LOAD_ERROR(3),
    VALIDATED(11), VALIDATING(12), VALIDATION_ERROR(13),
    BOUND(21), BINDING(22), BIND_ERROR(23),

    VALIDATION_STREET_TYPE_ERROR(101),
    VALIDATION_STREET_ERROR(102),
    VALIDATION_BUILDING_ERROR(103),

    LOCAL_CITY_UNRESOLVED(201),
    LOCAL_STREET_TYPE_UNRESOLVED(202),
    LOCAL_STREET_UNRESOLVED(203),
    LOCAL_BUILDING_UNRESOLVED(204),

    EXTERNAL_CITY_UNRESOLVED(301),
    EXTERNAL_STREET_TYPE_UNRESOLVED(302),
    EXTERNAL_STREET_UNRESOLVED(303),
    EXTERNAL_BUILDING_UNRESOLVED(304);

    private long id;

    ConsumptionStatus(long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }
}
