package ru.complitex.common.converter;

/**
 *
 * @author Artem
 */
public abstract class AbstractConverter<T> implements IConverter<T> {

    @Override
    public String toString(T object) {
        return object.toString();
    }
}
