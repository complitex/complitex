package org.complitex.common.converter;

/**
 *
 * @author Artem
 */
public class StringConverter implements IConverter<String> {

    @Override
    public String toString(String object) {
        return object;
    }

    @Override
    public String toObject(String value) {
        return value;
    }
}
