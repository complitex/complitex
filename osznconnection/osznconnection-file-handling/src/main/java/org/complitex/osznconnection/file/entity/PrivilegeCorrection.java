package org.complitex.osznconnection.file.entity;

import org.complitex.correction.entity.Correction;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 29.07.13 14:39
 */
public class PrivilegeCorrection extends Correction {
    public PrivilegeCorrection() {
    }

    public PrivilegeCorrection(String externalId, Long objectId, String correction, Long organizationId,
                               Long userOrganizationId, Long moduleId) {
        super(externalId, objectId, correction, organizationId, userOrganizationId, moduleId);
    }

    public PrivilegeCorrection(String correction, Long organizationId, Long userOrganizationId) {
        super(correction, organizationId, userOrganizationId);
    }

    @Override
    public String getEntity() {
        return "privilege";
    }
}
