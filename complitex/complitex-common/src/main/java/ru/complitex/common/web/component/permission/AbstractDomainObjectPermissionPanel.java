package ru.complitex.common.web.component.permission;

import org.apache.wicket.markup.html.panel.Panel;

/**
 *
 * @author Artem
 */
public class AbstractDomainObjectPermissionPanel extends Panel {

    private final DomainObjectPermissionParameters parameters;

    public AbstractDomainObjectPermissionPanel(String id, DomainObjectPermissionParameters parameters) {
        super(id);
        this.parameters = parameters;
    }

    protected final DomainObjectPermissionParameters getParameters() {
        return parameters;
    }
}
