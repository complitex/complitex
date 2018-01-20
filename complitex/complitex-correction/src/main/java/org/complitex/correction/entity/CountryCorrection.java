package org.complitex.correction.entity;

/**
 * @author Anatoly A. Ivanov
 * 20.01.2018 14:04
 */
public class CountryCorrection extends Correction{
    public CountryCorrection() {
    }

    public CountryCorrection(Long externalId, Long objectId, String correction, Long organizationId, Long userOrganizationId) {
        super(externalId, objectId, correction, organizationId, userOrganizationId);
    }

    @Override
    public String getEntity() {
        return "country";
    }
}
