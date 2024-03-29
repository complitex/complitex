package ru.complitex.osznconnection.file.web.pages.privilege;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.web.component.ShowMode;
import ru.complitex.common.web.component.search.ISearchCallback;
import ru.complitex.common.web.component.search.SearchComponentState;
import ru.complitex.common.web.component.search.WiQuerySearchComponent;
import ru.complitex.correction.entity.Correction;
import ru.complitex.correction.web.address.component.AbstractCorrectionEditPanel;
import ru.complitex.osznconnection.file.service.privilege.PrivilegeCorrectionBean;
import ru.complitex.osznconnection.file.strategy.PrivilegeStrategy;
import ru.complitex.template.web.component.toolbar.DeleteItemButton;
import ru.complitex.template.web.component.toolbar.ToolbarButton;
import ru.complitex.template.web.security.SecurityRole;
import ru.complitex.template.web.template.FormTemplatePage;

import javax.ejb.EJB;
import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Страница для редактирования коррекций привилегий.
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRole.AUTHORIZED)
public final class PrivilegeCorrectionEdit extends FormTemplatePage {
    public static final String CORRECTION_ID = "correction_id";

    @EJB
    private PrivilegeStrategy privilegeStrategy;

    @EJB
    private PrivilegeCorrectionBean privilegeCorrectionBean;

    private class PrivilegeCallback implements ISearchCallback, Serializable {

        private Correction correction;

        PrivilegeCallback(Correction correction) {
            this.correction = correction;
        }

        @Override
        public void found(Component component, Map<String, Long> ids, AjaxRequestTarget target) {
            Long id = ids.get(privilegeStrategy.getEntityName());
            if (id != null && id > 0) {
                correction.setObjectId(id);
            }
        }
    }
    private AbstractCorrectionEditPanel correctionEditPanel;

    public PrivilegeCorrectionEdit(PageParameters params) {
        Long correctionId = params.get(CORRECTION_ID).toOptionalLong();

        add(correctionEditPanel = new AbstractCorrectionEditPanel("privilege", "correctionEditPanel", correctionId) {

            @Override
            protected IModel<String> internalObjectLabel(Locale locale) {
                return new ResourceModel("privilege");
            }

            @Override
            protected Panel internalObjectPanel(String id) {
                SearchComponentState componentState = new SearchComponentState();
                Correction correction = getCorrection();
                if (!isNew()) {
                    componentState.put(privilegeStrategy.getEntityName(), findPrivilege(correction.getObjectId()));
                }

                return new WiQuerySearchComponent(id, componentState, ImmutableList.of(privilegeStrategy.getEntityName()),
                        new PrivilegeCallback(correction), ShowMode.ACTIVE, true);
            }

            @Override
            protected String getNullObjectErrorMessage() {
                return getString("privilege_required");
            }

            @Override
            protected boolean validateExistence() {
                return false;
            }

            private DomainObject findPrivilege(long privilegeId) {
                return privilegeStrategy.getDomainObject(privilegeId, true);
            }

            @Override
            protected boolean isOrganizationCodeRequired() {
                return true;
            }

            @Override
            protected Class<? extends Page> getBackPageClass() {
                return PrivilegeCorrectionList.class;
            }

            @Override
            protected PageParameters getBackPageParameters() {
                return new PageParameters();
            }

            @Override
            protected void save() {
                privilegeCorrectionBean.save(getCorrection().toUpperCase());
            }

            @Override
            protected void delete() {
                privilegeCorrectionBean.delete(getCorrection());
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
