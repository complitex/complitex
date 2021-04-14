package org.complitex.osznconnection.file.web.menu;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.complitex.template.web.security.SecurityRole;
import org.complitex.template.web.template.ResourceTemplateMenu;

/**
 * inheaven on 17.03.2016.
 */
@AuthorizeInstantiation(SecurityRole.ADMIN_MODULE_EDIT)
public class OsznDictionaryMenu extends ResourceTemplateMenu{
    public OsznDictionaryMenu() {
        addDictionary("organization");
        addDictionary("organization_type");
        addDictionary("ownership");
        addDictionary("service");
    }
}
