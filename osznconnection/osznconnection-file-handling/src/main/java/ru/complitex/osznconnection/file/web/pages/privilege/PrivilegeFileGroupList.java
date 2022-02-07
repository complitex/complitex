package ru.complitex.osznconnection.file.web.pages.privilege;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import ru.complitex.template.web.component.toolbar.ToolbarButton;
import ru.complitex.template.web.template.TemplatePage;

import java.util.List;

/**
 * inheaven on 05.04.2016.
 */
@AuthorizeInstantiation("PRIVILEGE_GROUP")
public class PrivilegeFileGroupList extends TemplatePage{
    private PrivilegeFileGroupListPanel privilegeFileGroupListPanel;

    public PrivilegeFileGroupList() {
        add(privilegeFileGroupListPanel = new PrivilegeFileGroupListPanel("panel"));
    }

    @Override
    protected List<ToolbarButton> getToolbarButtons(String id) {
        return privilegeFileGroupListPanel.getToolbarButtons(id);
    }
}
