package ru.complitex.eirc.service.correction.web.edit;

import org.apache.wicket.Page;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.common.strategy.organization.IOrganizationStrategy;
import ru.complitex.correction.web.address.component.AbstractCorrectionEditPanel;
import ru.complitex.eirc.service.correction.entity.ServiceCorrection;
import ru.complitex.eirc.service.correction.service.ServiceCorrectionBean;
import ru.complitex.eirc.service.correction.web.list.ServiceCorrectionList;
import ru.complitex.template.web.security.SecurityRole;
import ru.complitex.template.web.template.FormTemplatePage;
import ru.complitex.eirc.organization.strategy.EircOrganizationStrategy;
import ru.complitex.eirc.service.entity.Service;
import ru.complitex.eirc.service.service.ServiceBean;
import ru.complitex.eirc.service.web.component.ServicePicker;

import javax.ejb.EJB;
import java.util.Locale;

/**
 * @author Anatoly Ivanov java@inheaven.ru
 *         Date: 29.11.13 18:44
 */
@AuthorizeInstantiation(SecurityRole.AUTHORIZED)
public class ServiceCorrectionEdit extends FormTemplatePage {
    public static final String CORRECTION_ID = "correction_id";

    @EJB
    private ServiceCorrectionBean serviceCorrectionBean;

    @EJB
    private ServiceBean serviceBean;

    @EJB(name = IOrganizationStrategy.BEAN_NAME, beanInterface = IOrganizationStrategy.class)
    private EircOrganizationStrategy organizationStrategy;

    private static final Long[] ORGANIZATION_TYPES = null;

    public ServiceCorrectionEdit(PageParameters params) {
        add(new AbstractCorrectionEditPanel("service_edit_panel", "service",
                params.get(CORRECTION_ID).toOptionalLong()) {

            @Override
            protected Long[] getOrganizationTypeIds() {
                return ORGANIZATION_TYPES;
            }

            @Override
            protected ServiceCorrection getCorrection(Long correctionId) {
                return serviceCorrectionBean.geServiceCorrection(correctionId);
            }

            @Override
            protected ServiceCorrection newCorrection() {
                return new ServiceCorrection();
            }

            @Override
            protected IModel<String> internalObjectLabel(Locale locale) {
                return new ResourceModel("service");
            }

            @Override
            protected ServicePicker internalObjectPanel(String id) {
                return new ServicePicker(id, new Model<Service>() {
                    @Override
                    public Service getObject() {
                        if (getCorrection().getObjectId() != null) {
                            return serviceBean.getService(getCorrection().getObjectId());
                        }

                        return null;
                    }

                    @Override
                    public void setObject(Service object) {
                        getCorrection().setObjectId(object.getId());
                    }
                });
            }

            @Override
            protected String getNullObjectErrorMessage() {
                return getString("service_required");
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
            protected IModel<String> getTitleModel() {
                return new ResourceModel("title");
            }
        });
    }
}
