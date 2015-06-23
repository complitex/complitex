package org.complitex.keconnection.heatmeter.web.correction;

import org.complitex.correction.web.address.*;
import org.complitex.correction.web.organization.OrganizationCorrectionList;
import org.complitex.correction.web.service.ServiceCorrectionList;
import org.complitex.template.web.template.ResourceTemplateMenu;

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
