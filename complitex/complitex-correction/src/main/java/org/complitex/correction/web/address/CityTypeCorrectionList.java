package org.complitex.correction.web.address;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.complitex.template.web.security.SecurityRole;

/**
 * @author Anatoly A. Ivanov
 * 21.01.2018 17:40
 */
@AuthorizeInstantiation(SecurityRole.ADMIN_MODULE_EDIT)
public class CityTypeCorrectionList extends AddressCorrectionList{
    public CityTypeCorrectionList() {
        super("city_type");
    }
}
