package org.complitex.address.entity;

import org.complitex.common.mybatis.IFixedIdType;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 16.07.13 16:25
 */
public enum AddressEntity implements IFixedIdType{
    APARTMENT(100, "apartment"), ROOM(200, "room"), STREET(300, "street"), CITY(400, "city"),
    BUILDING(500, "building"), DISTRICT(600, "district"), REGION(700, "region"), COUNTRY(800, "country"),
    CITY_TYPE(1300, "city_type"), STREET_TYPE(1400, "street_type"), BUILDING_ADDRESS(1500, "building_address");

    private Integer id;
    private String entityName;

    AddressEntity(Integer id, String entityName) {
        this.id = id;
        this.entityName = entityName;
    }

    @Override
    public Integer getId() {
        return id;
    }

    public String getEntityName() {
        return entityName;
    }

    public static AddressEntity getValue(String entityName){
        for (AddressEntity addressEntity : AddressEntity.values()){
            if (addressEntity.getEntityName().equals(entityName)){
                return addressEntity;
            }
        }

        return null;
    }
}
