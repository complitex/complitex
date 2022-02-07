package ru.complitex.keconnection.heatmeter.service.exception;

import ru.complitex.keconnection.heatmeter.entity.HeatmeterBindingStatus;

/**
 *
 * @author Artem
 */
public class HeatmeterBindException extends Exception {

    private final HeatmeterBindingStatus status;

    public HeatmeterBindException(HeatmeterBindingStatus status) {
        this.status = status;
    }

    public HeatmeterBindingStatus getStatus() {
        return status;
    }
}
