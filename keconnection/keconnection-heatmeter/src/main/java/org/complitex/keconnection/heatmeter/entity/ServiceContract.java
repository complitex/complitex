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
    private Long organizationId;
    private Long servicingOrganizationId;

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

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Long getServicingOrganizationId() {
        return servicingOrganizationId;
    }

    public void setServicingOrganizationId(Long servicingOrganizationId) {
        this.servicingOrganizationId = servicingOrganizationId;
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
