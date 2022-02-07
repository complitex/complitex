package ru.complitex.organization.web.component;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import ru.complitex.common.web.component.organization.OrganizationIdPicker;
import ru.complitex.organization_type.strategy.OrganizationTypeStrategy;

public class UserOrganizationPicker extends Panel {

    public UserOrganizationPicker(String id, final IModel<Long> organizationIdModel) {
        super(id);

        add(new OrganizationIdPicker("object", organizationIdModel, OrganizationTypeStrategy.USER_ORGANIZATION_TYPE));
    }
}
