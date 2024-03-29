package ru.complitex.common.web.component.permission.organization;

import ru.complitex.common.web.component.factory.WebComponentFactoryUtil;
import ru.complitex.common.web.component.permission.AbstractDomainObjectPermissionPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Artem
 */
public class OrganizationPermissionPanelFactory {
    private final Logger log = LoggerFactory.getLogger(OrganizationPermissionPanelFactory.class);

    public static final String WEB_COMPONENT_NAME = "OrganizationPermissionPanel";

    public static AbstractDomainObjectPermissionPanel create(String id, OrganizationPermissionParameters parameters) {
        Class<? extends AbstractDomainObjectPermissionPanel> organizationPermissionPanelClass =
                (Class) WebComponentFactoryUtil.getComponentClass(WEB_COMPONENT_NAME);
        try {
            return organizationPermissionPanelClass.getConstructor(
                    String.class, OrganizationPermissionParameters.class).newInstance(id, parameters);
        } catch (Exception e) {
            LoggerFactory.getLogger(OrganizationPermissionPanelFactory.class)
                    .warn("Couldn't to instantiate organization permission panel. Default one will be used.", e);
            return new OrganizationPermissionsPanel(id, parameters);
        }
    }

    private OrganizationPermissionPanelFactory() {
    }
}
