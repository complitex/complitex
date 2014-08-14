/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.web.component;

import com.google.common.collect.Lists;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.service.PermissionBean;
import org.complitex.dictionary.strategy.organization.IOrganizationStrategy;
import org.complitex.dictionary.web.component.DisableAwareDropDownChoice;
import org.complitex.dictionary.web.component.DomainObjectDisableAwareRenderer;

import javax.ejb.EJB;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Artem
 */
public final class PermissionPanel extends Panel {
    @EJB(name = IOrganizationStrategy.BEAN_NAME)
    private IOrganizationStrategy organizationStrategy;

    private static final DomainObject VISIBLE_BY_ALL = new DomainObject();

    static {
        VISIBLE_BY_ALL.setId(PermissionBean.VISIBLE_BY_ALL_PERMISSION_ID);
    }

    /**
     * For new objects.
     * @param id
     * @param userOrganizationIds
     * @param subjectIds
     * @param inheritedSubjectIds
     */
    public PermissionPanel(String id, List<Long> userOrganizationIds, Set<Long> subjectIds, Set<Long> inheritedSubjectIds) {
        super(id);
        init(userOrganizationIds, subjectIds, inheritedSubjectIds);
    }

    /**
     * For existing objects.
     * @param id
     * @param subjectIds
     */
    public PermissionPanel(String id, Set<Long> subjectIds) {
        super(id);
        init(subjectIds);
    }

    /**
     * For existing objects.
     * @param subjectIds
     */
    private void init(final Set<Long> subjectIds) {
        final Long existingSubjectId = subjectIds.isEmpty() ? null : subjectIds.iterator().next();
        final IModel<List<DomainObject>> userOrganizationsModel = new AbstractReadOnlyModel<List<DomainObject>>() {

            private List<DomainObject> userOrganizations;

            private List<DomainObject> load() {
                List<DomainObject> list = Lists.newArrayList();
                if (existingSubjectId == PermissionBean.VISIBLE_BY_ALL_PERMISSION_ID) {
                    list.add(VISIBLE_BY_ALL);
                } else {
                    list.add(organizationStrategy.findById(existingSubjectId, true));
                }
                return list;
            }

            @Override
            public List<DomainObject> getObject() {
                if (userOrganizations == null) {
                    userOrganizations = load();
                }
                return userOrganizations;
            }
        };
        IModel<DomainObject> model = newModel(subjectIds);
        model.setObject(userOrganizationsModel.getObject().get(0));
        DisableAwareDropDownChoice<DomainObject> organizationPicker = newOrganizationPicker(model, userOrganizationsModel);
        organizationPicker.setEnabled(false);
        add(organizationPicker);
        setRenderBodyOnly(true);
    }

    /**
     * For new objects.
     * @param userOrganizationIds
     * @param subjectIds
     * @param inheritedSubjectIds
     */
    private void init(final List<Long> userOrganizationIds, final Set<Long> subjectIds, final Set<Long> inheritedSubjectIds) {
        final boolean inheritedObjectVisibleByAll = inheritedSubjectIds.size() == 1
                && inheritedSubjectIds.iterator().next().equals(PermissionBean.VISIBLE_BY_ALL_PERMISSION_ID);
        final IModel<List<DomainObject>> userOrganizationsModel = new AbstractReadOnlyModel<List<DomainObject>>() {

            private List<DomainObject> userOrganizations;

            private List<DomainObject> load() {
                List<DomainObject> list = Lists.newArrayList();
                for (long userOrganizationId : userOrganizationIds) {
                    if (inheritedObjectVisibleByAll || inheritedSubjectIds.contains(userOrganizationId)) {
                        list.add(organizationStrategy.findById(userOrganizationId, true));
                    }
                }
                if (list.isEmpty()) {
                    if (inheritedObjectVisibleByAll) {
                        list.add(VISIBLE_BY_ALL);
                    } else {
                        for (long organizationId : inheritedSubjectIds) {
                            list.add(organizationStrategy.findById(organizationId, true));
                        }
                    }
                }
                return list;
            }

            @Override
            public List<DomainObject> getObject() {
                if (userOrganizations == null) {
                    userOrganizations = load();
                }
                return userOrganizations;
            }
        };

        final IModel<DomainObject> model = newModel(subjectIds);

        DisableAwareDropDownChoice<DomainObject> organizationPicker = newOrganizationPicker(model, userOrganizationsModel);
        add(organizationPicker);

        if (userOrganizationsModel.getObject().size() == 1) {
            model.setObject(userOrganizationsModel.getObject().get(0));
            organizationPicker.setEnabled(false);
        }
        setRenderBodyOnly(true);
    }

    private IModel<DomainObject> newModel(final Set<Long> subjectIds) {
        return new Model<DomainObject>() {

            @Override
            public void setObject(DomainObject userOrganization) {
                super.setObject(userOrganization);
                subjectIds.clear();
                subjectIds.add(userOrganization.getId());
            }
        };
    }

    private DomainObjectDisableAwareRenderer newRenderer() {
        return new DomainObjectDisableAwareRenderer() {

            @Override
            public String getDisplayValue(DomainObject object) {
                if (object.getId().equals(VISIBLE_BY_ALL.getId())) {
                    return getString("visible_by_all");
                } else {
                    return organizationStrategy.displayDomainObject(object, getLocale());
                }
            }
        };
    }

    private DisableAwareDropDownChoice<DomainObject> newOrganizationPicker(IModel<DomainObject> model,
            IModel<List<DomainObject>> userOrganizationsModel) {
        DisableAwareDropDownChoice<DomainObject> organizationPicker =
                new DisableAwareDropDownChoice<DomainObject>("organizationPicker", model, userOrganizationsModel,
                newRenderer());
        organizationPicker.setRequired(true);
        return organizationPicker;
    }
}
