package org.complitex.osznconnection.file.web.menu;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.complitex.template.web.security.SecurityRole;
import org.complitex.template.web.template.ResourceTemplateMenu;

/**
 * inheaven on 17.03.2016.
 */
@AuthorizeInstantiation(SecurityRole.ADMIN_MODULE_EDIT)
public class OsznDescriptionMenu extends ResourceTemplateMenu{
    public OsznDescriptionMenu() {
        addDescription("country");
        addDescription("region");
        addDescription("city");
        addDescription("district");
        addDescription("street");
        addDescription("building");
        addDescription("organization");
        addDescription("ownership");
        addDescription("privilege");
    }
}
