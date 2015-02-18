package org.complitex.common.util;

import org.complitex.common.converter.*;
import org.complitex.common.entity.Attribute;
import org.complitex.common.entity.DomainObject;

import java.util.Date;

/**
 * Simplifies getting attribute values of domain object.
 * @author Artem
 */
public final class AttributeUtil {

    private AttributeUtil() {
    }

    public static <T> T getAttributeValue(DomainObject object, long attributeTypeId, IConverter<T> converter) {
        Attribute attribute = object.getAttribute(attributeTypeId);
        T value = null;
        if (attribute != null) {
            String attributeValue = StringCultures.getSystemStringCulture(attribute.getStringCultures()).getValue();
            value = attributeValue != null ? converter.toObject(attributeValue) : null;
        }
        return value;
    }

    public static String getStringValue(DomainObject object, long attributeTypeId) {
        return getAttributeValue(object, attributeTypeId, new StringConverter());
    }


    public static Integer getIntegerValue(DomainObject object, long attributeTypeId) {
        return getAttributeValue(object, attributeTypeId, new IntegerConverter());
    }

    public static Double getDoubleValue(DomainObject object, long attributeTypeId) {
        return getAttributeValue(object, attributeTypeId, new DoubleConverter());
    }

    public static Date getDateValue(DomainObject object, long attributeTypeId) {
        return getAttributeValue(object, attributeTypeId, new DateConverter());
    }

    public static boolean getBooleanValue(DomainObject object, long attributeTypeId) {
        Boolean value = getAttributeValue(object, attributeTypeId, new BooleanConverter());
        return value != null ? value : false;
    }
}
