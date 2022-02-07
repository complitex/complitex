package ru.complitex.osznconnection.file.web.pages.ownership;

import com.google.common.collect.Lists;
import org.apache.wicket.Page;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.web.component.DisableAwareDropDownChoice;
import ru.complitex.common.web.component.DomainObjectDisableAwareRenderer;
import ru.complitex.correction.entity.Correction;
import ru.complitex.correction.web.address.component.AbstractCorrectionEditPanel;
import ru.complitex.osznconnection.file.service.privilege.OwnershipCorrectionBean;
import ru.complitex.osznconnection.file.strategy.OwnershipStrategy;
import ru.complitex.template.web.component.toolbar.DeleteItemButton;
import ru.complitex.template.web.component.toolbar.ToolbarButton;
import ru.complitex.template.web.security.SecurityRole;
import ru.complitex.template.web.template.FormTemplatePage;

import javax.ejb.EJB;
import java.util.List;
import java.util.Locale;

/**
 * Страница для редактирования коррекций форм власти.
 */
@AuthorizeInstantiation(SecurityRole.AUTHORIZED)
public final class OwnershipCorrectionEdit extends FormTemplatePage {
    public static final String CORRECTION_ID = "correction_id";

    @EJB
    private OwnershipCorrectionBean ownershipCorrectionBean;

    private class OwnershipCorrectionEditPanel extends Panel {

        @EJB
        private OwnershipStrategy ownershipStrategy;

        OwnershipCorrectionEditPanel(String id, final Correction ownershipCorrection) {
            super(id);

            List<? extends DomainObject> allOwnerships = ownershipStrategy.getAll();

            IModel<DomainObject> ownershipModel = new Model<DomainObject>() {

                @Override
                public DomainObject getObject() {
                    final Long ownershipId = ownershipCorrection.getObjectId();

                    if (ownershipId != null) {
                        return allOwnerships.stream()
                                .filter(object -> object.getObjectId().equals(ownershipId))
                                .findFirst().get();
                    }

                    return null;
                }

                @Override
                public void setObject(DomainObject object) {
                    ownershipCorrection.setObjectId(object.getObjectId());
                }
            };
            DomainObjectDisableAwareRenderer renderer = new DomainObjectDisableAwareRenderer() {

                @Override
                public Object getDisplayValue(DomainObject object) {
                    return ownershipStrategy.displayDomainObject(object, getLocale());
                }
            };
            DisableAwareDropDownChoice<DomainObject> ownership = new DisableAwareDropDownChoice<DomainObject>("ownership",
                    ownershipModel, allOwnerships, renderer);
            ownership.setRequired(true);
            add(ownership);
        }
    }
    private AbstractCorrectionEditPanel correctionEditPanel;

    public OwnershipCorrectionEdit(PageParameters params) {
        Long correctionId = params.get(CORRECTION_ID).toOptionalLong();
        add(correctionEditPanel = new AbstractCorrectionEditPanel("ownership", "correctionEditPanel", correctionId) {

            @Override
            protected IModel<String> internalObjectLabel(Locale locale) {
                return new ResourceModel("ownership");
            }

            @Override
            protected Panel internalObjectPanel(String id) {
                return new OwnershipCorrectionEditPanel(id, getCorrection());
            }

            @Override
            protected String getNullObjectErrorMessage() {
                return getString("ownership_required");
            }

            @Override
            protected boolean isOrganizationCodeRequired() {
                return true;
            }

            @Override
            protected Class<? extends Page> getBackPageClass() {
                return OwnershipCorrectionList.class;
            }

            @Override
            protected PageParameters getBackPageParameters() {
                return new PageParameters();
            }

            @Override
            protected void save() {
                ownershipCorrectionBean.save(getCorrection().toUpperCase());
            }

            @Override
            protected void delete() {
                ownershipCorrectionBean.delete(getCorrection());
            }

            @Override
            protected IModel<String> getTitleModel() {
                return new StringResourceModel("title", this, null);
            }
        });
    }

    @Override
    protected List<ToolbarButton> getToolbarButtons(String id) {
        List<ToolbarButton> toolbar = Lists.newArrayList();
        toolbar.add(new DeleteItemButton(id) {

            @Override
            protected void onClick() {
                correctionEditPanel.executeDeletion();
            }

            @Override
            public boolean isVisible() {
                return !correctionEditPanel.isNew();
            }
        });
        return toolbar;
    }
}
