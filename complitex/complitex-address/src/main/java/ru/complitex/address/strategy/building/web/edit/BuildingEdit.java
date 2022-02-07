package ru.complitex.address.strategy.building.web.edit;

import com.google.common.collect.Lists;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.template.web.component.toolbar.ToolbarButton;
import ru.complitex.template.web.component.toolbar.search.CollapsibleInputSearchToolbarButton;
import ru.complitex.template.web.pages.DomainObjectEdit;

import java.util.List;

public class BuildingEdit extends DomainObjectEdit {

    public BuildingEdit(PageParameters parameters) {
        super(parameters);
    }

    @Override
    protected List<ToolbarButton> getToolbarButtons(String id) {
        List<ToolbarButton> toolbarButtons = Lists.newArrayList();
        toolbarButtons.addAll(super.getToolbarButtons(id));
        toolbarButtons.add(new CollapsibleInputSearchToolbarButton(id));

        return toolbarButtons;
    }
}
