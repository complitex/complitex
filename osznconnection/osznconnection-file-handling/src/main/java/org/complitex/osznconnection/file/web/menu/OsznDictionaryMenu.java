package org.complitex.osznconnection.file.web.menu;

import org.complitex.template.web.template.ResourceTemplateMenu;

/**
 * inheaven on 17.03.2016.
 */
public class OsznDictionaryMenu extends ResourceTemplateMenu{
    public OsznDictionaryMenu() {
        addStrategy("organization");
        addStrategy("organization_type");
        addStrategy("ownership");
        addStrategy("privilege");
        addStrategy("service");
    }
}
