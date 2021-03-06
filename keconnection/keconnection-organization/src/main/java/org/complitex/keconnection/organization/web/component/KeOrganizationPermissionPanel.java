package org.complitex.keconnection.organization.web.component;

import org.complitex.common.entity.DomainObject;
import org.complitex.common.strategy.organization.IOrganizationStrategy;
import org.complitex.common.web.component.permission.organization.OrganizationPermissionParameters;
import org.complitex.keconnection.organization.strategy.KeOrganizationStrategy;

import javax.ejb.EJB;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Artem
 */
public class KeOrganizationPermissionPanel extends KeDomainObjectPermissionPanel {
    @EJB(name = IOrganizationStrategy.BEAN_NAME, beanInterface = IOrganizationStrategy.class)
    private KeOrganizationStrategy organizationStrategy;

    private final Long organizationId;

    public KeOrganizationPermissionPanel(String id,
                                         OrganizationPermissionParameters organizationPermissionParameters) {
        super(id, organizationPermissionParameters.getParameters());
        this.organizationId = organizationPermissionParameters.getOrganizationId();
    }

    @Override
    protected boolean isAllowModifyOrganization(int index, Long subjectId) {
        return !(organizationId != null && organizationId > 0) || index != 0;
    }

    @Override
    protected List<DomainObject> initializeSelectedSubjects(Set<Long> selectedSubjectIds) {
        final List<DomainObject> superSelectedSubjects = super.initializeSelectedSubjects(selectedSubjectIds);

        DomainObject itself = organizationStrategy.getDomainObject(organizationId, true);
        if (itself != null && organizationStrategy.isUserOrganization(itself)) {
            if (organizationId != null && organizationId > 0) {
                List<DomainObject> selectedSubjects = new ArrayList<>();

                for (DomainObject o : superSelectedSubjects) {
                    if (!o.getObjectId().equals(organizationId)) {
                        selectedSubjects.add(o);
                    }
                }
                selectedSubjects.add(0, itself);
                return selectedSubjects;
            }
        }
        return superSelectedSubjects;
    }
}
