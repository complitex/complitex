package ru.complitex.keconnection.heatmeter.entity.consumption;

import ru.complitex.common.entity.IFixedIdType;

import java.util.EnumSet;

/**
 * @author inheaven on 20.03.2015 2:25.
 */
public enum ConsumptionStatus implements IFixedIdType{
    LOADED(11), LOADING(12), LOAD_ERROR(13),
    VALIDATED(21), VALIDATING(22), VALIDATION_ERROR(23),
    BOUND(31), BINDING(32), BIND_ERROR(33),

    VALIDATION_STREET_TYPE_ERROR(101),
    VALIDATION_STREET_ERROR(102),
    VALIDATION_BUILDING_ERROR(103),
    VALIDATION_APARTMENT_ERROR(104),


    LOCAL_CITY_UNRESOLVED(201),
    LOCAL_STREET_TYPE_UNRESOLVED(202),
    LOCAL_STREET_UNRESOLVED(203),
    LOCAL_BUILDING_UNRESOLVED(204),
    LOCAL_BUILDING_CODE_UNRESOLVED(205),

    EXTERNAL_CITY_UNRESOLVED(301),
    EXTERNAL_STREET_TYPE_UNRESOLVED(302),
    EXTERNAL_STREET_UNRESOLVED(303),
    EXTERNAL_BUILDING_UNRESOLVED(304),

    METER_NOT_FOUND(401);

    public static final EnumSet<ConsumptionStatus> LOCAL_UNRESOLVED = EnumSet.of(
            LOCAL_CITY_UNRESOLVED,
            LOCAL_STREET_TYPE_UNRESOLVED,
            LOCAL_STREET_UNRESOLVED,
            LOCAL_BUILDING_UNRESOLVED);

    private Integer id;

    ConsumptionStatus(Integer id) {
        this.id = id;
    }

    @Override
    public Integer getId() {
        return id;
    }
}
