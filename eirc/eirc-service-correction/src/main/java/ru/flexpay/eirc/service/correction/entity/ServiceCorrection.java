package ru.flexpay.eirc.service.correction.entity;

import org.complitex.correction.entity.Correction;

/**
 * @author Pavel Sknar
 */
public class ServiceCorrection extends Correction {
    public ServiceCorrection() {
    }

    public ServiceCorrection(Long externalId, Long objectId, String correction, Long organizationId,
                                  Long userOrganizationId, Long moduleId) {
        super(externalId, objectId, correction, organizationId, userOrganizationId, moduleId);
    }

    @Override
    public String getEntity() {
        return "service";
    }
}
