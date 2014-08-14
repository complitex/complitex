package org.complitex.osznconnection.file.web.component.process;

import org.apache.wicket.model.LoadableDetachableModel;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.strategy.organization.IOrganizationStrategy;
import org.complitex.common.web.component.DisableAwareDropDownChoice;
import org.complitex.common.web.component.DomainObjectDisableAwareRenderer;
import org.complitex.osznconnection.organization.strategy.OsznOrganizationStrategy;

import javax.ejb.EJB;
import java.util.List;

/**
 *
 * @author Artem
 */
public final class OsznFilter extends DisableAwareDropDownChoice<DomainObject> {
    @EJB(name = IOrganizationStrategy.BEAN_NAME, beanInterface = IOrganizationStrategy.class)
    private OsznOrganizationStrategy organizationStrategy;

    public OsznFilter(String id) {
        super(id);
        setChoices(new LoadableDetachableModel<List<DomainObject>>() {

            @Override
            protected List<DomainObject> load() {
                return organizationStrategy.getAllOSZNs(getLocale());
            }
        });
        setChoiceRenderer(new DomainObjectDisableAwareRenderer() {

            @Override
            public Object getDisplayValue(DomainObject object) {
                return organizationStrategy.displayDomainObject(object, getLocale());
            }
        });
        setNullValid(true);
    }
}
