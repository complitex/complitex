package org.complitex.sync.entity;

import org.complitex.common.entity.IFixedIdType;

/**
 * @author inheaven on 22.01.2016 12:54.
 */
public enum SyncEntity implements IFixedIdType{
    APARTMENT(100, "apartment"), ROOM(200, "room"), STREET(300, "street"), CITY(400, "city"),
    BUILDING(500, "building"), DISTRICT(600, "district"), REGION(700, "region"), COUNTRY(800, "country"),
    CITY_TYPE(1300, "city_type"), STREET_TYPE(1400, "street_type"), BUILDING_ADDRESS(1500, "building_address"),
    ORGANIZATION(900, "organization");

    private Integer id;
    private String entityName;

    SyncEntity(Integer id, String entityName) {
        this.id = id;
        this.entityName = entityName;
    }
    
    public String getEntityName() {
        return entityName;
    }

    @Override
    public Integer getId() {
        return id;
    }

    public static SyncEntity getValue(String entityName){
        for (SyncEntity syncEntity : SyncEntity.values()){
            if (syncEntity.getEntityName().equals(entityName)){
                return syncEntity;
            }
        }

        throw new IllegalArgumentException("No entity by name " + entityName);
    }
}
