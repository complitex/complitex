package org.complitex.osznconnection.file.web.pages.privilege;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.complitex.template.web.security.SecurityRole;
import org.complitex.template.web.template.TemplatePage;

/**
 * inheaven on 05.04.2016.
 */
@AuthorizeInstantiation(SecurityRole.AUTHORIZED)
public class PrivilegeFileGroupList extends TemplatePage{
    public PrivilegeFileGroupList() {
        add(new PrivilegeFileGroupListPanel("panel"));
    }
}
