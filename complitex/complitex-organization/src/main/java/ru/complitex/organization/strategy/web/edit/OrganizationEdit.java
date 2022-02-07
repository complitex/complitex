package ru.complitex.organization.strategy.web.edit;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.common.web.component.domain.DomainObjectAccessUtil;
import ru.complitex.common.web.component.domain.DomainObjectEditPanel;
import ru.complitex.common.web.component.permission.AbstractDomainObjectPermissionPanel;
import ru.complitex.common.web.component.permission.DomainObjectPermissionParameters;
import ru.complitex.common.web.component.permission.organization.OrganizationPermissionPanelFactory;
import ru.complitex.common.web.component.permission.organization.OrganizationPermissionParameters;
import ru.complitex.template.web.pages.DomainObjectEdit;

import java.util.Set;

/**
 *
 * @author Artem
 */
public final class OrganizationEdit extends DomainObjectEdit {

    public OrganizationEdit(PageParameters parameters) {
        super(parameters);
    }

    @Override
    protected DomainObjectEditPanel newEditPanel(String id, final String entity, final String strategy, Long objectId, Long parentId,
            String parentEntity, String backInfoSessionKey) {
        return new DomainObjectEditPanel(id, entity, strategy, objectId, parentId, parentEntity, backInfoSessionKey) {

            @Override
            protected AbstractDomainObjectPermissionPanel newPermissionsPanel(String id, Set<Long> parentSubjectIds) {
                return OrganizationPermissionPanelFactory.create(id, new OrganizationPermissionParameters(
                        new DomainObjectPermissionParameters(getNewObject().getSubjectIds(), parentSubjectIds,
                        DomainObjectAccessUtil.canEdit(strategy, entity, getNewObject())),
                        getNewObject().getObjectId()));
            }

            @Override
            protected Set<Long> initParentPermissions() {
                return null;
            }
        };
    }
}
