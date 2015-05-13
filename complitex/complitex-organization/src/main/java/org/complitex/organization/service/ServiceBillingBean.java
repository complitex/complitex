package org.complitex.organization.service;

import com.google.common.collect.ImmutableMap;
import org.complitex.common.service.AbstractBean;
import org.complitex.common.strategy.organization.IOrganizationStrategy;
import org.complitex.organization.entity.ServiceBilling;

import java.util.Collections;
import java.util.List;

/**
 * @author inheaven on 012 12.05.15 17:26
 */
public class ServiceBillingBean extends AbstractBean {
    public List<ServiceBilling> getServiceBillings(List<Long> ids){
        if (ids.isEmpty()){
            return Collections.emptyList();
        }

        return selectList("selectServiceBillings", ids);
    }

    public void save(ServiceBilling serviceBilling) {
        insert("insertServiceAssociation", serviceBilling);
    }

    public void deleteByOrganizationId(Long organizationId){
        delete("deleteServiceAssociations", ImmutableMap.of("objectId", organizationId,
                "serviceAssociationsAT", IOrganizationStrategy.SERVICE_BILLING));

    }

}
