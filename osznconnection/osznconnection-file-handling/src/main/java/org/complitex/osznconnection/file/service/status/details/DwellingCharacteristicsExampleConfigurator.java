package org.complitex.osznconnection.file.service.status.details;

import org.complitex.osznconnection.file.entity.StatusDetail;
import org.complitex.osznconnection.file.entity.example.DwellingCharacteristicsExample;

/**
 *
 * @author Artem
 */
public class DwellingCharacteristicsExampleConfigurator extends AbstractExampleConfigurator<DwellingCharacteristicsExample> {

    @Override
    public DwellingCharacteristicsExample createExample(StatusDetail statusDetail) {
        DwellingCharacteristicsExample example = new DwellingCharacteristicsExample();

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
