package org.complitex.common.web.component.organization;

import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.strategy.organization.IOrganizationStrategy;

import javax.ejb.EJB;

/**
 * @author Anatoly Ivanov
 *         Date: 015 15.08.14 18:33
 */
public class OrganizationIdPicker extends FormComponentPanel{
    @EJB(name = IOrganizationStrategy.BEAN_NAME, beanInterface = IOrganizationStrategy.class)
    protected IOrganizationStrategy organizationStrategy;

    public OrganizationIdPicker(String id, final IModel<Long> model, Long... organizationTypeIds) {
        super(id);

        add(new OrganizationPicker("picker", new IModel<DomainObject>() {
            @Override
            public DomainObject getObject() {
                return organizationStrategy.findById(model.getObject(), true);
            }

            @Override
            public void setObject(DomainObject object) {
                model.setObject(object.getId());
            }

            @Override
            public void detach() {

            }
        }, organizationTypeIds).setLabel(getLabel()).setEnabled(isEnabled()));
    }
}
