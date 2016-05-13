package org.complitex.correction.web.service;

import com.google.common.collect.Lists;
import org.apache.wicket.Page;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.common.entity.FilterWrapper;
import org.complitex.common.web.component.domain.DomainSelectLabel;
import org.complitex.correction.entity.ServiceCorrection;
import org.complitex.correction.service.ServiceCorrectionBean;
import org.complitex.correction.web.component.AbstractCorrectionEditPanel;
import org.complitex.template.web.component.toolbar.DeleteItemButton;
import org.complitex.template.web.component.toolbar.ToolbarButton;
import org.complitex.template.web.security.SecurityRole;
import org.complitex.template.web.template.FormTemplatePage;

import javax.ejb.EJB;
import java.util.List;
import java.util.Locale;

/**
 * @author inheaven on 23.06.2015 16:17.
 */
@AuthorizeInstantiation(SecurityRole.AUTHORIZED)
public class ServiceCorrectionEdit extends FormTemplatePage {

    @EJB
    private ServiceCorrectionBean serviceCorrectionBean;

    private AbstractCorrectionEditPanel correctionEditPanel;

    public ServiceCorrectionEdit(PageParameters params) {
        add(correctionEditPanel = new AbstractCorrectionEditPanel<ServiceCorrection>("service_correction_edit_panel",
                params.get("correction_id").toOptionalLong()) {

            @Override
            protected ServiceCorrection getCorrection(Long correctionId) {
                return serviceCorrectionBean.getServiceCorrection(correctionId);
            }

            @Override
            protected ServiceCorrection newCorrection() {
                return new ServiceCorrection();
            }

            @Override
            protected IModel<String> internalObjectLabel(Locale locale) {
                return new ResourceModel("internal_object");
            }

            @Override
            protected WebMarkupContainer internalObjectPanel(String id) {
                return new DomainSelectLabel(id, "service", new PropertyModel<>(getCorrection(), "objectId"));
            }

            @Override
            protected String getNullObjectErrorMessage() {
                return getString("service_required");
            }

            @Override
            protected boolean validateExistence() {
                return serviceCorrectionBean.getServiceCorrectionsCount(FilterWrapper.of(getCorrection())) > 0;
            }

            @Override
            protected Class<? extends Page> getBackPageClass() {
                return ServiceCorrectionList.class;
            }

            @Override
            protected PageParameters getBackPageParameters() {
                return new PageParameters();
            }

            @Override
            protected void save() {
                serviceCorrectionBean.save(getCorrection());
            }

            @Override
            protected void delete() {
                serviceCorrectionBean.delete(getCorrection());
            }

            @Override
            protected IModel<String> getTitleModel() {
                return new ResourceModel("title");
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
