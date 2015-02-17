package org.complitex.keconnection.heatmeter.entity;

import org.complitex.common.entity.ILongId;

/**
 * @author inheaven on 017 17.02.15 20:21
 */
public class ServiceContractService implements ILongId {
    private Long id;
    private Long serviceObjectId;
    private Long serviceContractId;

    public ServiceContractService() {
    }

    public ServiceContractService(Long serviceObjectId, Long serviceContractId) {
        this.serviceObjectId = serviceObjectId;
        this.serviceContractId = serviceContractId;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
