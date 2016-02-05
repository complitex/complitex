package org.complitex.correction.entity;

/**
 * @author Pavel Sknar
 */
public enum AddressLinkStatus implements LinkStatus {

    /* группа "неразрешимых" статусов, т.е. любой статус из группы указывает на то, что какая-то часть внутреннего адреса у записи не разрешена */
    CITY_UNRESOLVED(200), STREET_TYPE_UNRESOLVED(237), STREET_UNRESOLVED(201), STREET_AND_BUILDING_UNRESOLVED(231),
    BUILDING_UNRESOLVED(202), APARTMENT_UNRESOLVED(250), ROOM_UNRESOLVED(260),

    /* найдено больше одной записи адреса во внутреннем адресном справочнике */
    MORE_ONE_CITY(234), MORE_ONE_STREET_TYPE(238), MORE_ONE_STREET(235), MORE_ONE_BUILDING(236),
    MORE_ONE_APARTMENT(251), MORE_ONE_ROOM(261),

    /* Найдено более одной записи в коррекциях */
    MORE_ONE_CITY_CORRECTION(210), MORE_ONE_STREET_TYPE_CORRECTION(239), MORE_ONE_STREET_CORRECTION(211),
    MORE_ONE_BUILDING_CORRECTION(228), MORE_ONE_APARTMENT_CORRECTION(252), MORE_ONE_ROOM_CORRECTION(262),

    ADDRESS_LINKED(299);

    private Integer id;

    AddressLinkStatus(Integer id) {
        this.id = id;
    }


    @Override
    public Integer getId() {
        return id;
    }
}
