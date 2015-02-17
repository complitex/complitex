package org.complitex.keconnection.heatmeter.entity;

import org.complitex.address.strategy.building.entity.BuildingCode;
import org.complitex.common.entity.AbstractEntity;
import org.complitex.common.entity.DomainObject;

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

    private List<BuildingCode> buildingCodes;
    private List<DomainObject> services;

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

    public List<BuildingCode> getBuildingCodes() {
        return buildingCodes;
    }

    public void setBuildingCodes(List<BuildingCode> buildingCodes) {
        this.buildingCodes = buildingCodes;
    }

    public List<DomainObject> getServices() {
        return services;
    }

    public void setServices(List<DomainObject> services) {
        this.services = services;
    }
}