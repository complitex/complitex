package ru.complitex.keconnection.heatmeter.service.exception;

import ru.complitex.common.exception.AbstractException;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 12.10.12 13:14
 */
public class HeatmeterNotFoundException extends AbstractException {
    public HeatmeterNotFoundException(Integer ls) {
        super("Счетчик по л/с {0} не найден", ls);
    }
}
