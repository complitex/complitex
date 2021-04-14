package org.complitex.correction.web.address;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.complitex.template.web.security.SecurityRole;

@AuthorizeInstantiation(SecurityRole.ADMIN_MODULE_EDIT)
public class CityCorrectionList extends AddressCorrectionList {
    public CityCorrectionList() {
        super("city");
    }
}
