package org.complitex.address.entity;

import org.complitex.common.entity.IEntity;
import org.complitex.common.mybatis.IFixedIdType;

/**
 * @author inheaven on 22.01.2016 12:54.
 */
public enum SyncEntity implements IEntity, IFixedIdType{
    APARTMENT(100L, "apartment"), ROOM(200L, "room"), STREET(300L, "street"), CITY(400L, "city"),
    BUILDING(500L, "building"), DISTRICT(600L, "district"), REGION(700L, "region"), COUNTRY(800L, "country"),
    CITY_TYPE(1300L, "city_type"), STREET_TYPE(1400L, "street_type"), BUILDING_ADDRESS(1500L, "building_address"),
    ORGANIZATION(900L, "organization");

    private Long id;
    private String entityName;

    SyncEntity(Long id, String entityName) {
        this.id = id;
        this.entityName = entityName;
    }

    @Override
    public String getEntityName() {
        return entityName;
    }

    @Override
    public Long getId() {
        return id;
    }

    public static SyncEntity getValue(String entityName){
        for (SyncEntity syncEntity : SyncEntity.values()){
            if (syncEntity.getEntityName().equals(entityName)){
                return syncEntity;
            }
        }

        return null;
    }
}
