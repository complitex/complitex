package org.complitex.osznconnection.organization.strategy.web.edit;

import org.complitex.common.entity.DomainObject;
import org.complitex.organization.strategy.web.edit.OrganizationEditComponent;
import org.complitex.organization.strategy.web.edit.OrganizationValidator;

import java.util.Locale;

public class OsznOrganizationValidator extends OrganizationValidator {

    public OsznOrganizationValidator(Locale systemLocale) {
        super(systemLocale);
    }

    @Override
    protected boolean checkDistrict(DomainObject object, OrganizationEditComponent editComponent) {
        OsznOrganizationEditComponent editComp = (OsznOrganizationEditComponent) editComponent;
        if (editComp.isOszn()) {
            boolean validated = editComponent.isDistrictEntered();
            if (!validated) {
                editComponent.error(editComponent.getString("must_have_district"));
            }
            return validated;
        } else {
            return true;
        }
    }
}
