package org.complitex.osznconnection.file.web.pages.privilege;

import org.complitex.template.web.template.TemplatePage;

/**
 * inheaven on 05.04.2016.
 */
public class PrivilegeFileGroupList extends TemplatePage{
    public PrivilegeFileGroupList() {
        add(new PrivilegeFileGroupListPanel("panel"));
    }
}
