package org.complitex.osznconnection.file.web.menu;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.complitex.correction.web.address.*;
import org.complitex.correction.web.organization.OrganizationCorrectionList;
import org.complitex.correction.web.service.ServiceCorrectionList;
import org.complitex.osznconnection.file.web.pages.account.PersonAccountList;
import org.complitex.osznconnection.file.web.pages.ownership.OwnershipCorrectionList;
import org.complitex.osznconnection.file.web.pages.privilege.PrivilegeCorrectionList;
import org.complitex.template.web.security.SecurityRole;
import org.complitex.template.web.template.ResourceTemplateMenu;

@AuthorizeInstantiation(SecurityRole.ADMIN_MODULE_EDIT)
public class CorrectionMenu extends ResourceTemplateMenu {

    public CorrectionMenu() {
        add(CountryCorrectionList.class);
        add(RegionCorrectionList.class);
        add(CityTypeCorrectionList.class);
        add(CityCorrectionList.class);
        add(DistrictCorrectionList.class);
        add(StreetTypeCorrectionList.class);
        add(StreetCorrectionList.class);
        add(BuildingCorrectionList.class);
        add(OrganizationCorrectionList.class);
        add(OwnershipCorrectionList.class);
        add(PersonAccountList.class);
        add(PrivilegeCorrectionList.class);
        add(ServiceCorrectionList.class);
    }
}
