package ru.complitex.eirc.registry.entity;

import com.google.common.collect.ImmutableMap;
import ru.complitex.common.entity.IFixedIdType;
import ru.complitex.common.entity.ILocalizedType;
import ru.complitex.common.util.ResourceUtil;
import ru.complitex.correction.entity.AddressLinkStatus;
import ru.complitex.correction.entity.LinkStatus;

import java.util.Locale;
import java.util.Map;

/**
 * @author Pavel Sknar
 */
public enum ImportErrorType implements IFixedIdType, ILocalizedType {

    CITY_UNRESOLVED(1),
    STREET_TYPE_UNRESOLVED(2),
    STREET_UNRESOLVED(3),
    STREET_AND_BUILDING_UNRESOLVED(4),
    BUILDING_UNRESOLVED(5),
    APARTMENT_UNRESOLVED(6),
    ROOM_UNRESOLVED(19),

    /* найдено больше одной записи адреса во внутреннем адресном справочнике */
    MORE_ONE_CITY(7),
    MORE_ONE_STREET_TYPE(8),
    MORE_ONE_STREET(9),
    MORE_ONE_BUILDING(10),
    MORE_ONE_APARTMENT(11),
    MORE_ONE_ROOM(20),

    /* Найдено более одной записи в коррекциях */
    MORE_ONE_CITY_CORRECTION(12),
    MORE_ONE_STREET_TYPE_CORRECTION(13),
    MORE_ONE_STREET_CORRECTION(14),
    MORE_ONE_BUILDING_CORRECTION(15),
    MORE_ONE_APARTMENT_CORRECTION(16),
    MORE_ONE_ROOM_CORRECTION(21),

    ACCOUNT_UNRESOLVED(17),
    MORE_ONE_ACCOUNT(18);

    private static final String RESOURCE_BUNDLE = ImportErrorType.class.getName();

    private Integer id;
    private static final Map<LinkStatus, ImportErrorType> mapper = ImmutableMap.<LinkStatus, ImportErrorType>builder().
            put(AddressLinkStatus.CITY_UNRESOLVED, CITY_UNRESOLVED).
            put(AddressLinkStatus.STREET_TYPE_UNRESOLVED, STREET_TYPE_UNRESOLVED).
            put(AddressLinkStatus.STREET_UNRESOLVED, STREET_UNRESOLVED).
            put(AddressLinkStatus.STREET_AND_BUILDING_UNRESOLVED, STREET_AND_BUILDING_UNRESOLVED).
            put(AddressLinkStatus.BUILDING_UNRESOLVED, BUILDING_UNRESOLVED).
            put(AddressLinkStatus.APARTMENT_UNRESOLVED, APARTMENT_UNRESOLVED).
            put(AddressLinkStatus.ROOM_UNRESOLVED, ROOM_UNRESOLVED).
            put(AddressLinkStatus.MORE_ONE_CITY, MORE_ONE_CITY).
            put(AddressLinkStatus.MORE_ONE_STREET_TYPE, MORE_ONE_STREET_TYPE).
            put(AddressLinkStatus.MORE_ONE_STREET, MORE_ONE_STREET).
            put(AddressLinkStatus.MORE_ONE_BUILDING, MORE_ONE_BUILDING).
            put(AddressLinkStatus.MORE_ONE_APARTMENT, MORE_ONE_APARTMENT).
            put(AddressLinkStatus.MORE_ONE_ROOM, MORE_ONE_ROOM).
            put(AddressLinkStatus.MORE_ONE_CITY_CORRECTION, MORE_ONE_CITY_CORRECTION).
            put(AddressLinkStatus.MORE_ONE_STREET_TYPE_CORRECTION, MORE_ONE_STREET_TYPE_CORRECTION).
            put(AddressLinkStatus.MORE_ONE_STREET_CORRECTION, MORE_ONE_STREET_CORRECTION).
            put(AddressLinkStatus.MORE_ONE_BUILDING_CORRECTION, MORE_ONE_BUILDING_CORRECTION).
            put(AddressLinkStatus.MORE_ONE_APARTMENT_CORRECTION, MORE_ONE_APARTMENT_CORRECTION).
            put(AddressLinkStatus.MORE_ONE_ROOM_CORRECTION, MORE_ONE_ROOM_CORRECTION).
            build();

    private ImportErrorType(Integer id) {
        this.id = id;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public String getLabel(Locale locale) {
        return ResourceUtil.getString(RESOURCE_BUNDLE, String.valueOf(getId()), locale);
    }

    public static ImportErrorType getImportErrorType(LinkStatus linkStatus) {
        return mapper.get(linkStatus);
    }
}
