package org.complitex.correction.entity;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 28.11.13 15:41
 */
public class OrganizationCorrection extends Correction {
    public OrganizationCorrection() {
    }

    public OrganizationCorrection(Long externalId, Long objectId, String correction, Long organizationId,
                                  Long userOrganizationId, Long moduleId) {
        super(externalId, objectId, correction, organizationId, userOrganizationId, moduleId);
    }

    @Override
    public String getEntity() {
        return "organization";
    }
}
