package org.complitex.organization.entity;

import org.complitex.common.entity.DomainObject;

import java.util.List;

/**
 * @author inheaven on 012 12.05.15 17:53
 */
public class Organization extends DomainObject {
    private List<ServiceBilling> serviceBillings;

    public Organization(List<ServiceBilling> serviceBillings) {
        this.serviceBillings = serviceBillings;
    }

    public Organization(DomainObject copy, List<ServiceBilling> serviceBillings) {
        super(copy);
        this.serviceBillings = serviceBillings;
    }

    public List<ServiceBilling> getServiceBillings() {
        return serviceBillings;
    }

    public void setServiceBillings(List<ServiceBilling> serviceBillings) {
        this.serviceBillings = serviceBillings;
    }
}
