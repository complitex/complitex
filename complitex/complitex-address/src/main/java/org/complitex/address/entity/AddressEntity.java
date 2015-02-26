package org.complitex.address.entity;

import org.complitex.common.entity.IEntity;
import org.complitex.common.mybatis.IFixedIdType;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 16.07.13 16:25
 */
public enum AddressEntity implements IEntity, IFixedIdType{
    APARTMENT(100L, "apartment"), ROOM(200L, "room"), STREET(300L, "street"), CITY(400L, "city"),
    BUILDING(500L, "building"), DISTRICT(600L, "district"), REGION(700L, "region"), COUNTRY(800L, "country"),
    CITY_TYPE(1300L, "city_type"), STREET_TYPE(1400L, "street_type"), BUILDING_ADDRESS(1500L, "building_address");

    private Long id;
    private String entityName;

    private AddressEntity(Long id, String entityName) {
        this.id = id;
        this.entityName = entityName;
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getEntityName() {
        return entityName;
    }
}
