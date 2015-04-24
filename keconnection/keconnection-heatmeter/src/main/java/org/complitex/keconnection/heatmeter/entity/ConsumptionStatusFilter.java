package org.complitex.keconnection.heatmeter.entity;

import org.complitex.keconnection.heatmeter.entity.consumption.ConsumptionStatus;

import java.io.Serializable;

/**
 * @author inheaven on 023 23.04.15 19:25
 */
public class ConsumptionStatusFilter implements Serializable{
    private ConsumptionStatus status;
    private String message;
    private Integer count;

    public ConsumptionStatus getStatus() {
        return status;
    }

    public void setStatus(ConsumptionStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
