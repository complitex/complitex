package ru.complitex.keconnection.heatmeter.entity;

import ru.complitex.common.entity.AbstractEntity;

/**
 * @author inheaven on 017 17.02.15 20:21
 */
public class ServiceContractService extends AbstractEntity {
    private Long serviceId;
    private Long serviceContractId;

    public ServiceContractService() {
    }

    public ServiceContractService(Long serviceContractId) {
        this.serviceContractId = serviceContractId;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public Long getServiceContractId() {
        return serviceContractId;
    }

    public void setServiceContractId(Long serviceContractId) {
        this.serviceContractId = serviceContractId;
    }
}
