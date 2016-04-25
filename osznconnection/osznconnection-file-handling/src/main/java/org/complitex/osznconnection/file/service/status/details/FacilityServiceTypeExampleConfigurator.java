package org.complitex.osznconnection.file.service.status.details;

import org.complitex.osznconnection.file.entity.StatusDetail;
import org.complitex.osznconnection.file.entity.example.PrivilegeExample;

/**
 *
 * @author Artem
 */
public class FacilityServiceTypeExampleConfigurator extends AbstractExampleConfigurator<PrivilegeExample> {

    @Override
    public PrivilegeExample createExample(StatusDetail statusDetail) {
        PrivilegeExample example = new PrivilegeExample();
        example.setLastName(statusDetail.getDetail("lastName"));
        example.setFirstName(statusDetail.getDetail("firstName"));
        example.setMiddleName(statusDetail.getDetail("middleName"));
        example.setStreetCode(statusDetail.getDetail("streetCode"));
        example.setBuilding(statusDetail.getDetail("building"));
        example.setCorp(statusDetail.getDetail("buildingCorp"));
        example.setApartment(statusDetail.getDetail("apartment"));
        return example;
    }
}
