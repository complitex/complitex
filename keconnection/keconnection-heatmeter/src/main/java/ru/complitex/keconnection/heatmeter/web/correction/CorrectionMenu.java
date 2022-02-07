package ru.complitex.keconnection.heatmeter.web.correction;

import ru.complitex.correction.web.address.*;
import ru.complitex.correction.web.organization.OrganizationCorrectionList;
import ru.complitex.correction.web.service.ServiceCorrectionList;
import ru.complitex.template.web.template.ResourceTemplateMenu;

public class CorrectionMenu extends ResourceTemplateMenu {
    public CorrectionMenu() {
        add(HeatmeterCorrectionList.class);
        add(OrganizationCorrectionList.class);
        add(ServiceCorrectionList.class);
        add(CityCorrectionList.class);
        add(DistrictCorrectionList.class);
        add(StreetCorrectionList.class);
        add(StreetTypeCorrectionList.class);
        add(BuildingCorrectionList.class);
    }
}
