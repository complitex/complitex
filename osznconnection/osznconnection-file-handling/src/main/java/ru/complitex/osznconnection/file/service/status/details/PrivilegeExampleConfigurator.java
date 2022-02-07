package ru.complitex.osznconnection.file.service.status.details;

import ru.complitex.osznconnection.file.entity.StatusDetail;
import ru.complitex.osznconnection.file.entity.example.PrivilegeExample;

/**
 * @author inheaven on 03.07.2016.
 */
public class PrivilegeExampleConfigurator extends AbstractExampleConfigurator<PrivilegeExample> {

    @Override
    public PrivilegeExample createExample(StatusDetail statusDetail) {
        PrivilegeExample example = new PrivilegeExample();

        example.setLastName(statusDetail.getDetail("lastName"));
        example.setFirstName(statusDetail.getDetail("firstName"));
        example.setMiddleName(statusDetail.getDetail("middleName"));
        example.setStreetCode(statusDetail.getDetail("streetCode"));
        example.setStreetType(statusDetail.getDetail("streetType"));
        example.setStreet(statusDetail.getDetail("street"));
        example.setBuilding(statusDetail.getDetail("building"));
        example.setCorp(statusDetail.getDetail("buildingCorp"));
        example.setApartment(statusDetail.getDetail("apartment"));

        return example;
    }
}
