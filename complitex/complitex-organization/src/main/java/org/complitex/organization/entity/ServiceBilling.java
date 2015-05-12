package org.complitex.organization.entity;

import org.complitex.common.entity.AbstractEntity;

public class ServiceBilling extends AbstractEntity {
    private Long serviceId;
    private Long billingId;

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public Long getBillingId() {
        return billingId;
    }

    public void setBillingId(Long billingId) {
        this.billingId = billingId;
    }
}
