package ru.complitex.common.web.component.permission.organization;

import ru.complitex.common.web.component.permission.DomainObjectPermissionParameters;

import java.io.Serializable;

/**
 *
 * @author Artem
 */
public class OrganizationPermissionParameters implements Serializable {

    private final DomainObjectPermissionParameters parameters;
    private final Long organizationId;

    public OrganizationPermissionParameters(DomainObjectPermissionParameters parameters, Long organizationId) {
        this.parameters = parameters;
        this.organizationId = organizationId;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public DomainObjectPermissionParameters getParameters() {
        return parameters;
    }
}
