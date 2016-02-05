package org.complitex.common.converter;

import java.io.Serializable;

/**
 *
 * @author Artem
 */
public interface IConverter<T> extends Serializable {

    String toString(T object);

    T toObject(String value);
}
