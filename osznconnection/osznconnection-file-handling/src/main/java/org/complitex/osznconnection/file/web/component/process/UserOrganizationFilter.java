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
public class UserOrganizationFilter extends DisableAwareDropDownChoice<DomainObject> {
    @EJB(name = IOrganizationStrategy.BEAN_NAME, beanInterface = IOrganizationStrategy.class)
    private OsznOrganizationStrategy organizationStrategy;

    public UserOrganizationFilter(String id) {
        super(id);

        setChoices(new LoadableDetachableModel<List<? extends DomainObject>>() {

            @Override
            protected List<? extends DomainObject> load() {
                return organizationStrategy.getUserOrganizations(getLocale());
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
