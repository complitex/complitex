package ru.complitex.common.converter;

/**
 *
 * @author Artem
 */
public class DoubleConverter extends AbstractConverter<Double> {

    @Override
    public Double toObject(String value) {
        return Double.valueOf(value);
    }
}
