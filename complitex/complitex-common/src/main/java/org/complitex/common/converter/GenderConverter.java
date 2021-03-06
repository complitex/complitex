package org.complitex.common.converter;

import org.complitex.common.entity.Gender;

/**
 *
 * @author Artem
 */
public class GenderConverter implements IConverter<Gender> {

    @Override
    public Gender toObject(String value) {
        return Enum.valueOf(Gender.class, value);
    }

    @Override
    public String toString(Gender object) {
        return object.name();
    }
}
