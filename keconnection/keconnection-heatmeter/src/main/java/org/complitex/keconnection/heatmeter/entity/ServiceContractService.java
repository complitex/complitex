package org.complitex.keconnection.heatmeter.entity;

import org.complitex.common.entity.AbstractEntity;

/**
 * @author inheaven on 017 17.02.15 20:21
 */
public class ServiceContractService extends AbstractEntity {
    private Long serviceObjectId;
    private Long serviceContractId;

    public ServiceContractService() {
    }

    public ServiceContractService(Long serviceContractId) {
        this.serviceContractId = serviceContractId;
    }

    public Long getServiceObjectId() {
        return serviceObjectId;
    }

    public void setServiceObjectId(Long serviceObjectId) {
        this.serviceObjectId = serviceObjectId;
    }

    public Long getServiceContractId() {
        return serviceContractId;
    }

    public void setServiceContractId(Long serviceContractId) {
        this.serviceContractId = serviceContractId;
    }
}
