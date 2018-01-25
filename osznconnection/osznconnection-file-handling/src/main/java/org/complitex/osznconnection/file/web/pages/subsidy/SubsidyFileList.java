package org.complitex.osznconnection.file.web.pages.subsidy;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.osznconnection.file.web.AbstractFileListPanel;
import org.complitex.template.web.component.toolbar.ToolbarButton;
import org.complitex.template.web.template.TemplatePage;

import java.util.List;

@AuthorizeInstantiation("SUBSIDY_FILE")
public final class SubsidyFileList extends TemplatePage {

    private final AbstractFileListPanel fileListPanel;

    public SubsidyFileList(PageParameters parameters) {
        super();

        add(new Label("title", new ResourceModel("title")));
        add(fileListPanel = new SubsidyFileListPanel("fileListPanel"));
    }

    @Override
    protected List<ToolbarButton> getToolbarButtons(String id) {
        return fileListPanel.getToolbarButtons(id);
    }
}
