package org.complitex.common.converter;

/**
 *
 * @author Artem
 */
public class BooleanConverter extends AbstractConverter<Boolean> {

    @Override
    public Boolean toObject(String bool) {
        return Boolean.valueOf(bool);
    }
}
