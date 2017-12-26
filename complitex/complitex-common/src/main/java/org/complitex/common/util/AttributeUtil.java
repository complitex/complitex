package org.complitex.common.util;

import org.complitex.common.converter.*;
import org.complitex.common.entity.Attribute;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.entity.StringValue;

import java.util.Date;

/**
 * Simplifies getting attribute values of domain object.
 * @author Artem
 */
public final class AttributeUtil {

    private AttributeUtil() {
    }

    public static <T> T getAttributeValue(DomainObject object, long entityAttributeId, IConverter<T> converter) {
        Attribute attribute = object.getAttribute(entityAttributeId);
        T value = null;
        if (attribute != null) {
            StringValue stringValue = StringValueUtil.getSystemStringValue(attribute.getStringValues());
            String v = stringValue != null ? stringValue.getValue() : null;
            value = v != null ? converter.toObject(v) : null;
        }
        return value;
    }

    public static String getStringValue(DomainObject object, long entityAttributeId) {
        return getAttributeValue(object, entityAttributeId, new StringConverter());
    }


    public static Integer getIntegerValue(DomainObject object, long entityAttributeId) {
        return getAttributeValue(object, entityAttributeId, new IntegerConverter());
    }

    public static Double getDoubleValue(DomainObject object, long entityAttributeId) {
        return getAttributeValue(object, entityAttributeId, new DoubleConverter());
    }

    public static Date getDateValue(DomainObject object, long entityAttributeId) {
        return getAttributeValue(object, entityAttributeId, new DateConverter());
    }

    public static boolean getBooleanValue(DomainObject object, long entityAttributeId) {
        Boolean value = getAttributeValue(object, entityAttributeId, new BooleanConverter());
        return value != null ? value : false;
    }
}
