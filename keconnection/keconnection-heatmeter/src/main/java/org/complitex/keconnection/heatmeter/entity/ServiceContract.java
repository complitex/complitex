package org.complitex.keconnection.heatmeter.entity;

import org.complitex.common.entity.AbstractEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * inheaven on 13.11.2014 17:02.
 */
public class ServiceContract extends AbstractEntity{
    private Date beginDate;
    private Date endDate;
    private String number;
    private Long serviceProviderId;
    private Long organizationId;

    private List<ServiceContractService> serviceContractServices = new ArrayList<>();
    private List<ServiceContractBuilding> serviceContractBuildings = new ArrayList<>();

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Long getServiceProviderId() {
        return serviceProviderId;
    }

    public void setServiceProviderId(Long serviceProviderId) {
        this.serviceProviderId = serviceProviderId;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public List<ServiceContractService> getServiceContractServices() {
        return serviceContractServices;
    }

    public void setServiceContractServices(List<ServiceContractService> serviceContractServices) {
        this.serviceContractServices = serviceContractServices;
    }

    public List<ServiceContractBuilding> getServiceContractBuildings() {
        return serviceContractBuildings;
    }

    public void setServiceContractBuildings(List<ServiceContractBuilding> serviceContractBuildings) {
        this.serviceContractBuildings = serviceContractBuildings;
    }
}
