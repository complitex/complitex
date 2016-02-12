package ru.complitex.salelog.order.entity;

import org.complitex.common.mybatis.IFixedIdType;
import org.complitex.common.util.ResourceUtil;

import java.util.Locale;

/**
 * @author Pavel Sknar
 */
public enum OrderStatus implements IFixedIdType {

    EMPTY(0),
    DELIVERED(1),
    REJECTION(2),
    NO_DIAL_UP(3),
    SPECIAL(4),
    ;

    private static final String RESOURCE_BUNDLE = OrderStatus.class.getName();

    private Integer id;

    private OrderStatus(Integer id) {
        this.id = id;
    }

    @Override
    public Integer getId() {
        return id;
    }

    public String getLabel(Locale locale) {
        return ResourceUtil.getString(RESOURCE_BUNDLE, String.valueOf(getId()), locale);
    }
}
