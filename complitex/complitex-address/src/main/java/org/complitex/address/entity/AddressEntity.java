package org.complitex.address.entity;

import com.google.common.collect.ImmutableList;
import org.complitex.common.entity.IEntityName;
import org.complitex.common.entity.IFixedIdType;

import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 16.07.13 16:25
 */
public enum AddressEntity implements IFixedIdType, IEntityName{
    APARTMENT(100, "apartment"), ROOM(200, "room"), STREET(300, "street"), CITY(400, "city"),
    BUILDING(500, "building"), DISTRICT(600, "district"), REGION(700, "region"), COUNTRY(800, "country"),
    CITY_TYPE(1300, "city_type"), STREET_TYPE(1400, "street_type");

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

    public List<String> getFilters() {
        switch (this) {
            case CITY:
                return ImmutableList.of("city");
            case STREET:
                return ImmutableList.of("city", "street");
            case BUILDING:
                return ImmutableList.of("city", "street", "building");
            case APARTMENT:
                return ImmutableList.of("city", "street", "building", "apartment");
            case ROOM:
                return ImmutableList.of("city", "street", "building", "apartment", "room");
        }

        return ImmutableList.of("city", "street", "building");
    }
}
