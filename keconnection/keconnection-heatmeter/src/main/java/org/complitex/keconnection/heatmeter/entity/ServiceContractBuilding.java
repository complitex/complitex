package org.complitex.keconnection.heatmeter.entity;

import org.complitex.common.entity.AbstractEntity;

/**
 * inheaven on 13.11.2014 18:32.
 */
public class ServiceContractBuilding extends AbstractEntity {
    private Long buildingCodeId;
    private Long serviceContractId;

    public ServiceContractBuilding() {
    }

    public ServiceContractBuilding(Long serviceContractId) {
        this.serviceContractId = serviceContractId;
    }

    public Long getBuildingCodeId() {
        return buildingCodeId;
    }

    public void setBuildingCodeId(Long buildingCodeId) {
        this.buildingCodeId = buildingCodeId;
    }

    public Long getServiceContractId() {
        return serviceContractId;
    }

    public void setServiceContractId(Long serviceContractId) {
        this.serviceContractId = serviceContractId;
    }
}
