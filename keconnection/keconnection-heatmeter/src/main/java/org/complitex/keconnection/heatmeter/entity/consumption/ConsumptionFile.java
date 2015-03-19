package org.complitex.keconnection.heatmeter.entity.consumption;

import org.complitex.common.entity.AbstractEntity;

import java.util.Date;

/**
 * @author inheaven on 016 16.03.15 19:03
 */
public class ConsumptionFile extends AbstractEntity{
    private String name;
    private Date om;
    private Long serviceProviderId;
    private Long serviceId;
    private Long userOrganizationId;
    private Long type = 0L;
    private ConsumptionFileStatus status;
    private Date loaded;
    private String checkSum;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getOm() {
        return om;
    }

    public void setOm(Date om) {
        this.om = om;
    }

    public Long getServiceProviderId() {
        return serviceProviderId;
    }

    public void setServiceProviderId(Long serviceProviderId) {
        this.serviceProviderId = serviceProviderId;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public Long getUserOrganizationId() {
        return userOrganizationId;
    }

    public void setUserOrganizationId(Long userOrganizationId) {
        this.userOrganizationId = userOrganizationId;
    }

    public Long getType() {
        return type;
    }

    public void setType(Long type) {
        this.type = type;
    }

    public ConsumptionFileStatus getStatus() {
        return status;
    }

    public void setStatus(ConsumptionFileStatus status) {
        this.status = status;
    }

    public Date getLoaded() {
        return loaded;
    }

    public void setLoaded(Date loaded) {
        this.loaded = loaded;
    }

    public String getCheckSum() {
        return checkSum;
    }

    public void setCheckSum(String checkSum) {
        this.checkSum = checkSum;
    }
}
