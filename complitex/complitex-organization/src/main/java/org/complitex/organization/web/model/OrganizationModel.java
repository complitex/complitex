package org.complitex.organization.web.model;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.apache.wicket.model.Model;
import org.complitex.organization.entity.Organization;

import java.util.List;

public abstract class OrganizationModel extends Model<Organization> {

    @Override
    public Organization getObject() {
        final Long organizationId = getOrganizationId();
        if (organizationId != null) {
            return Iterables.find(getOrganizations(), new Predicate<Organization>() {

                @Override
                public boolean apply(Organization object) {
                    return object.getObjectId().equals(organizationId);
                }
            });
        }
        return null;
    }

    @Override
    public void setObject(Organization object) {
        if (object != null) {
            setOrganizationId(object.getObjectId());
        } else {
            setOrganizationId(null);
        }
    }

    public abstract Long getOrganizationId();

    public abstract void setOrganizationId(Long organizationId);

    public abstract List<? extends Organization> getOrganizations();
}
