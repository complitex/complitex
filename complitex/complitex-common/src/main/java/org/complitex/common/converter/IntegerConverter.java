package org.complitex.common.converter;

/**
 *
 * @author Artem
 */
public class IntegerConverter extends AbstractConverter<Integer> {

    @Override
    public Integer toObject(String integer) {
        return Integer.valueOf(integer);
    }
}
