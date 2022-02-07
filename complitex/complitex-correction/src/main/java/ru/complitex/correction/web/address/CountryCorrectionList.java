package ru.complitex.correction.web.address;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import ru.complitex.template.web.security.SecurityRole;

/**
 * @author Anatoly A. Ivanov
 * 21.01.2018 17:35
 */
@AuthorizeInstantiation(SecurityRole.ADMIN_MODULE_EDIT)
public class CountryCorrectionList extends AddressCorrectionList{
    public CountryCorrectionList() {
        super("country");
    }
}
