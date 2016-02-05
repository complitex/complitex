package org.complitex.keconnection.heatmeter.web.correction.component.model;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.apache.wicket.model.Model;
import org.complitex.common.entity.DomainObject;

import java.util.List;

/**
 *
 * @author Artem
 */
public abstract class OrganizationModel extends Model<DomainObject> {

    @Override
    public DomainObject getObject() {
        final Long organizationId = getOrganizationId();
        if (organizationId != null) {
            return Iterables.find(getOrganizations(), new Predicate<DomainObject>() {

                @Override
                public boolean apply(DomainObject object) {
                    return object.getObjectId().equals(organizationId);
                }
            });
        }
        return null;
    }

    @Override
    public void setObject(DomainObject object) {
        if (object != null) {
            setOrganizationId(object.getObjectId());
        } else {
            setOrganizationId(null);
        }
    }

    public abstract Long getOrganizationId();

    public abstract void setOrganizationId(Long organizationId);

    public abstract List<DomainObject> getOrganizations();
}
