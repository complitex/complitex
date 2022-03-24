package ru.complitex.eirc.payment.entity;

import com.google.common.collect.ImmutableMap;
import ru.complitex.common.entity.IFixedIdType;

import java.util.Map;

/**
 * @author Pavel Sknar
 */
public enum SearchType implements IFixedIdType {
    UNKNOWN_TYPE(0),
    TYPE_ACCOUNT_NUMBER(1),
    TYPE_QUITTANCE_NUMBER(2),
    TYPE_APARTMENT_NUMBER(3),
    TYPE_SERVICE_PROVIDER_ACCOUNT_NUMBER(4),
    TYPE_ADDRESS(5),
    TYPE_COMBINED(6),
    TYPE_ERC_KVP_NUMBER(7),
    TYPE_ERC_KVP_ADDRESS(8),
    TYPE_ADDRESS_STR(9),
    TYPE_BUILDING_NUMBER(10),
    TYPE_ROOM_NUMBER(11);

    private static final Map<Integer, SearchType> SEARCH_TYPES;

    static {
        ImmutableMap.Builder<Integer, SearchType> builder = ImmutableMap.builder();
        for (SearchType searchType : SearchType.values()) {
            builder.put(searchType.getId(), searchType);
        }
        SEARCH_TYPES = builder.build();
    }

    private Integer id;

    private SearchType(Integer id) {
        this.id = id;
    }

    @Override
    public Integer getId() {
        return id;
    }

    public static SearchType getSearchType(int id) {
        SearchType searchType = SEARCH_TYPES.get(id);
        return searchType == null? UNKNOWN_TYPE : searchType;
    }
}
