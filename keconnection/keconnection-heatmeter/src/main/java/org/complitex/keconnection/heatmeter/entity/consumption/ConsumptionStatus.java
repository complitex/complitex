package org.complitex.keconnection.heatmeter.entity.consumption;

import org.complitex.common.mybatis.FixedIdTypeHandler;
import org.complitex.common.mybatis.IFixedIdType;

import java.util.EnumSet;

/**
 * @author inheaven on 20.03.2015 2:25.
 */
@FixedIdTypeHandler
public enum  ConsumptionStatus implements IFixedIdType{
    LOADED(11), LOADING(12), LOAD_ERROR(13),
    VALIDATED(21), VALIDATING(22), VALIDATION_ERROR(23),
    BOUND(31), BINDING(32), BIND_ERROR(33),

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

    public static final EnumSet<ConsumptionStatus> LOCAL_UNRESOLVED = EnumSet.of(
            LOCAL_CITY_UNRESOLVED,
            LOCAL_STREET_TYPE_UNRESOLVED,
            LOCAL_STREET_UNRESOLVED,
            LOCAL_BUILDING_UNRESOLVED);

    private long id;

    ConsumptionStatus(long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }
}
