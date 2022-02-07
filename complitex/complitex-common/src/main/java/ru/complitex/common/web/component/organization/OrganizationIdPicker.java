package ru.complitex.common.web.component.organization;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.strategy.organization.IOrganizationStrategy;

import javax.ejb.EJB;

/**
 * @author Anatoly Ivanov
 *         Date: 015 15.08.14 18:33
 */
public class OrganizationIdPicker extends Panel{
    @EJB(name = IOrganizationStrategy.BEAN_NAME, beanInterface = IOrganizationStrategy.class)
    protected IOrganizationStrategy organizationStrategy;

    public OrganizationIdPicker(String id, final IModel<Long> model,  Long... organizationTypeIds) {
        super(id, model);

        add(new OrganizationPicker("picker", new IModel<DomainObject>() {
            @Override
            public DomainObject getObject() {
                return organizationStrategy.getDomainObject(model.getObject(), true);
            }

            @Override
            public void setObject(DomainObject object) {
                if (object != null) { //todo fix nested form submit
                    model.setObject(object.getObjectId());
                }
            }

            @Override
            public void detach() {

            }
        }, organizationTypeIds).setEnabled(isEnabled()));
    }
}
