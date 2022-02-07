package ru.flexpay.eirc.service_provider_account.entity;

import ru.complitex.correction.entity.Correction;

/**
 * @author Pavel Sknar
 */
public class ServiceProviderAccountCorrection extends Correction {
    public ServiceProviderAccountCorrection() {
    }

    public ServiceProviderAccountCorrection(Long externalId, Long objectId, String correction, Long organizationId,
                                            Long userOrganizationId, Long moduleId) {
        super("service_provider_account", externalId, objectId, correction, organizationId, userOrganizationId);
    }
}
