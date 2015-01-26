package org.complitex.keconnection.organization.web.component;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.strategy.organization.IOrganizationStrategy;
import org.complitex.keconnection.organization.strategy.KeConnectionOrganizationStrategy;
import org.complitex.organization_type.strategy.OrganizationTypeStrategy;

import javax.ejb.EJB;

/**
 *
 * @author Artem
 */
@Deprecated
public class KeConnectionUserOrganizationPicker extends Panel {

    @EJB(name = IOrganizationStrategy.BEAN_NAME, beanInterface = IOrganizationStrategy.class)
    private KeConnectionOrganizationStrategy organizationStrategy;

    public KeConnectionUserOrganizationPicker(String id, final IModel<Long> organizationIdModel) {
        super(id);

        add(new OrganizationPicker("picker", new Model<DomainObject>() {

            @Override
            public DomainObject getObject() {
                Long id = organizationIdModel.getObject();
                if (id != null) {
                    return organizationStrategy.getDomainObject(id, true);
                }
                return null;
            }

            @Override
            public void setObject(DomainObject object) {
                organizationIdModel.setObject(object != null ? object.getObjectId() : null);
            }
        }, OrganizationTypeStrategy.USER_ORGANIZATION_TYPE));
    }
}
