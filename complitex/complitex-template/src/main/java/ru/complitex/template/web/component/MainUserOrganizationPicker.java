package ru.complitex.template.web.component;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.service.SessionBean;
import ru.complitex.common.strategy.StrategyFactory;
import ru.complitex.common.strategy.organization.IOrganizationStrategy;
import ru.complitex.common.web.component.DisableAwareDropDownChoice;
import ru.complitex.common.web.component.DomainObjectDisableAwareRenderer;

import javax.ejb.EJB;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Artem
 */
public class MainUserOrganizationPicker extends Panel implements IMainUserOrganizationPicker {

    @EJB
    private SessionBean sessionBean;

    @EJB
    private StrategyFactory strategyFactory;

    @EJB(name = "OrganizationStrategy")
    private IOrganizationStrategy organizationStrategy;

    private final boolean visible;

    public MainUserOrganizationPicker(String id, final IModel<DomainObject> model) {
        super(id);

        final List<DomainObject> userOrganizations = getUserOrganizationObjects();
        final IModel<DomainObject> mainUserOrganizationModel = new Model<DomainObject>() {

            @Override
            public DomainObject getObject() {
                DomainObject object = model.getObject();
                if (object != null && object.getObjectId() != null) {
                    for (DomainObject o : userOrganizations) {
                        if (o.getObjectId().equals(object.getObjectId())) {
                            return o;
                        }
                    }
                }
                return null;
            }

            @Override
            public void setObject(DomainObject object) {
                model.setObject(object);
            }
        };

        DisableAwareDropDownChoice<DomainObject> mainUserOrganizationPicker =
                new DisableAwareDropDownChoice<DomainObject>("select", mainUserOrganizationModel, userOrganizations,
                new DomainObjectDisableAwareRenderer() {

                    @Override
                    public Object getDisplayValue(DomainObject userOrganization) {
                        return displayOrganization(userOrganization);
                    }
                });

        if (userOrganizations.isEmpty()) {
            mainUserOrganizationPicker.setVisible(false);
        } else if (userOrganizations.size() == 1) {
            mainUserOrganizationPicker.setEnabled(false);
        } else {
            mainUserOrganizationPicker.add(new AjaxFormComponentUpdatingBehavior("change") {

                @Override
                protected void onUpdate(AjaxRequestTarget target) {
                }
            });
        }
        visible = mainUserOrganizationPicker.isVisible();
        add(mainUserOrganizationPicker);
    }

    protected String displayOrganization(DomainObject organization) {
        return organizationStrategy.displayDomainObject(organization, getLocale());
    }

    private List<DomainObject> getUserOrganizationObjects() {
        List<DomainObject> results = new ArrayList<>();
        List<Long> ids = sessionBean.getUserOrganizationObjectIds();
        if (!ids.isEmpty()) {
            for (long id : ids) {
                results.add(organizationStrategy.getDomainObject(id, true));
            }
        }

        return results;
    }


    public boolean visible() {
        return visible;
    }
}
