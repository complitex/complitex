package ru.flexpay.eirc.service.correction.entity;

import ru.complitex.correction.entity.Correction;

/**
 * @author Pavel Sknar
 */
public class ServiceCorrection extends Correction {
    public ServiceCorrection() {
    }

    public ServiceCorrection(Long externalId, Long objectId, String correction, Long organizationId,
                                  Long userOrganizationId, Long moduleId) {
        super("service", externalId, objectId, correction, organizationId, userOrganizationId);
    }
}
