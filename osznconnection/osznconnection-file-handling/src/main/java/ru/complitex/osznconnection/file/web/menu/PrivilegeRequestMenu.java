package ru.complitex.osznconnection.file.web.menu;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.osznconnection.file.web.pages.facility.FacilityStreetFileList;
import ru.complitex.osznconnection.file.web.pages.facility.FacilityStreetTypeFileList;
import ru.complitex.osznconnection.file.web.pages.facility.FacilityTarifFileList;
import ru.complitex.osznconnection.file.web.pages.privilege.*;
import ru.complitex.template.web.template.ResourceTemplateMenu;

import java.util.Locale;

@AuthorizeInstantiation({"PRIVILEGE_GROUP", "PRIVILEGE_FORM_2", "PRIVILEGE_PROLONGATION_S", "PRIVILEGE_PROLONGATION_P", "PRIVILEGE_LOCAL", "PRIVILEGE_DEBT"})
public class PrivilegeRequestMenu extends ResourceTemplateMenu {
    public PrivilegeRequestMenu() {
        add("privilege_list", PrivilegeFileGroupList.class, new String[]{"PRIVILEGE_GROUP"});
        add("facility_form2_list", FacilityForm2FileList.class, new String[]{"PRIVILEGE_FORM_2"});
        add("privilege_prolongation_s", PrivilegeProlongationFileList.class, new PageParameters().add("type", "s"),
                new String[]{"PRIVILEGE_PROLONGATION_S"});
        add("privilege_prolongation_p", PrivilegeProlongationFileList.class, new PageParameters().add("type", "p"),
                new String[]{"PRIVILEGE_PROLONGATION_P"});

        add("facility_local_list", FacilityLocalFileList.class, new String[]{"PRIVILEGE_LOCAL"});
        add("facility_local__janitor_list", FacilityLocalJanitorFileList.class, new String[]{"PRIVILEGE_LOCAL"});
        add("facility_local_compensation_list", FacilityLocalCompensationFileList.class, new String[]{"PRIVILEGE_LOCAL"});
        add("debt_list", DebtFileList.class, new String[]{"PRIVILEGE_DEBT"});

        add("facility_tarif_file_list", FacilityTarifFileList.class, new String[]{"PRIVILEGE_GROUP"});
        add("facility_street_type_file_list", FacilityStreetTypeFileList.class);
        add("facility_street_file_list", FacilityStreetFileList.class);
    }

    @Override
    public String getTitle(Locale locale) {
        return getString(PrivilegeRequestMenu.class, locale, "title");
    }

    @Override
    public String getTagId() {
        return "facility_request_menu";
    }
}
