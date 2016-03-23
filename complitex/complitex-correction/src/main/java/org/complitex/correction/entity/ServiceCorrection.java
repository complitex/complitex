package org.complitex.correction.entity;

/**
 * @author inheaven on 23.06.2015 16:15.
 */
public class ServiceCorrection extends Correction{
    public ServiceCorrection() {
    }

    public ServiceCorrection(String correction, Long organizationId, Long userOrganizationId) {
        super(correction, organizationId, userOrganizationId);
    }

    @Override
    public String getEntity() {
        return "service";
    }
}
