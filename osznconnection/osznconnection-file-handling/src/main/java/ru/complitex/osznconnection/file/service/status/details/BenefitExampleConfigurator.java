package ru.complitex.osznconnection.file.service.status.details;

import ru.complitex.osznconnection.file.entity.StatusDetail;
import ru.complitex.osznconnection.file.entity.example.BenefitExample;

/**
 *
 * @author Artem
 */
public class BenefitExampleConfigurator extends AbstractExampleConfigurator<BenefitExample> {

    @Override
    public BenefitExample createExample(StatusDetail statusDetail) {
        BenefitExample example = new BenefitExample();
        example.setAccount(statusDetail.getDetail("account"));
        example.setCity(statusDetail.getDetail("city"));
        example.setStreet(statusDetail.getDetail("street"));
        example.setBuilding(statusDetail.getDetail("building"));
        example.setCorp(statusDetail.getDetail("buildingCorp"));
        example.setApartment(statusDetail.getDetail("apartment"));
        return example;
    }
}
