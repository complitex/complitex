package ru.complitex.common.web.component.permission;

import java.io.Serializable;

import java.io.Serializable;
import java.util.Set;

/**
 *
 * @author Artem
 */
public class DomainObjectPermissionParameters implements Serializable {

    private final Set<Long> subjectIds;
    private final Set<Long> parentSubjectIds;
    private final boolean enabled;

    public DomainObjectPermissionParameters(Set<Long> subjectIds, Set<Long> parentSubjectIds, boolean enabled) {
        this.subjectIds = subjectIds;
        this.parentSubjectIds = parentSubjectIds;
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Set<Long> getParentSubjectIds() {
        return parentSubjectIds;
    }

    public Set<Long> getSubjectIds() {
        return subjectIds;
    }
}
