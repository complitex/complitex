package org.complitex.address.entity;

import com.google.common.collect.ImmutableList;
import org.complitex.common.mybatis.IFixedIdType;

import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 16.07.13 16:25
 */
public enum AddressEntity implements IFixedIdType{
    APARTMENT(100, "apartment"), ROOM(200, "room"), STREET(300, "street"), CITY(400, "city"),
    BUILDING(500, "building"), DISTRICT(600, "district"), REGION(700, "region"), COUNTRY(800, "country"),
    CITY_TYPE(1300, "city_type"), STREET_TYPE(1400, "street_type"), BUILDING_ADDRESS(1500, "building_address");

    private Integer id;
    private String entity;

    AddressEntity(Integer id, String entity) {
        this.id = id;
        this.entity = entity;
    }

    @Override
    public Integer getId() {
        return id;
    }

    public String getEntity() {
        return entity;
    }

    public static AddressEntity getValue(String entityName){
        for (AddressEntity addressEntity : AddressEntity.values()){
            if (addressEntity.getEntity().equals(entityName)){
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
