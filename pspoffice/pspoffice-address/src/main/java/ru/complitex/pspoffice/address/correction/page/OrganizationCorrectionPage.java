package ru.complitex.pspoffice.address.correction.page;

import ru.complitex.catalog.entity.Value;
import ru.complitex.pspoffice.address.correction.entity.OrganizationCorrection;

/**
 * @author Ivanov Anatoliy
 */
public class OrganizationCorrectionPage extends CorrectionPage {
    public OrganizationCorrectionPage() {
        super(OrganizationCorrection.CATALOG);
    }

    @Override
    protected boolean isRequired(Value value) {
        if (value.is(OrganizationCorrection.ORGANIZATION_SHORT_NAME)) {
            return false;
        }

        return super.isRequired(value);
    }
}
