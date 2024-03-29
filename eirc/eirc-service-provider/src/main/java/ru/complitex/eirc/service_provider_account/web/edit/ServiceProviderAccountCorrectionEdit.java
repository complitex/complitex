package ru.complitex.eirc.service_provider_account.web.edit;

import org.apache.wicket.Page;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.common.strategy.organization.IOrganizationStrategy;
import ru.complitex.correction.web.address.component.AbstractCorrectionEditPanel;
import ru.complitex.template.web.security.SecurityRole;
import ru.complitex.template.web.template.FormTemplatePage;
import ru.complitex.eirc.dictionary.entity.OrganizationType;
import ru.complitex.eirc.organization.strategy.EircOrganizationStrategy;
import ru.complitex.eirc.service_provider_account.entity.ServiceProviderAccount;
import ru.complitex.eirc.service_provider_account.entity.ServiceProviderAccountCorrection;
import ru.complitex.eirc.service_provider_account.service.ServiceProviderAccountBean;
import ru.complitex.eirc.service_provider_account.service.ServiceProviderAccountCorrectionBean;
import ru.complitex.eirc.service_provider_account.web.component.ServiceProviderAccountPicker;
import ru.complitex.eirc.service_provider_account.web.list.ServiceProviderAccountCorrectionList;

import javax.ejb.EJB;
import java.util.Locale;

/**
 * @author Anatoly Ivanov java@inheaven.ru
 *         Date: 29.11.13 18:44
 */
@AuthorizeInstantiation(SecurityRole.AUTHORIZED)
public class ServiceProviderAccountCorrectionEdit extends FormTemplatePage {
    public static final String CORRECTION_ID = "correction_id";

    @EJB
    private ServiceProviderAccountCorrectionBean serviceProviderAccountCorrectionBean;

    @EJB
    private ServiceProviderAccountBean serviceProviderAccountBean;

    @EJB(name = IOrganizationStrategy.BEAN_NAME, beanInterface = IOrganizationStrategy.class)
    private EircOrganizationStrategy organizationStrategy;

    private static final Long[] ORGANIZATION_TYPES = {Long.valueOf(OrganizationType.PAYMENT_COLLECTOR.getId())};

    public ServiceProviderAccountCorrectionEdit(PageParameters params) {
        add(new AbstractCorrectionEditPanel("service_provider_account_edit_panel", "service_provider_account",
                params.get(CORRECTION_ID).toOptionalLong()) {

            @Override
            protected Long[] getOrganizationTypeIds() {
                return ORGANIZATION_TYPES;
            }

            @Override
            protected ServiceProviderAccountCorrection getCorrection(Long correctionId) {
                return serviceProviderAccountCorrectionBean.geServiceProviderAccountCorrection(correctionId);
            }

            @Override
            protected ServiceProviderAccountCorrection newCorrection() {
                return new ServiceProviderAccountCorrection();
            }

            @Override
            protected IModel<String> internalObjectLabel(Locale locale) {
                return new ResourceModel("service_provider_account");
            }

            @Override
            protected ServiceProviderAccountPicker internalObjectPanel(String id) {
                return new ServiceProviderAccountPicker(id, new Model<ServiceProviderAccount>() {
                    @Override
                    public ServiceProviderAccount getObject() {
                        if (getCorrection().getObjectId() != null) {
                            return serviceProviderAccountBean.getServiceProviderAccount(getCorrection().getObjectId());
                        }

                        return null;
                    }

                    @Override
                    public void setObject(ServiceProviderAccount object) {
                        getCorrection().setObjectId(object.getId());
                    }
                });
            }

            @Override
            protected String getNullObjectErrorMessage() {
                return getString("service_provider_account_required");
            }

            @Override
            protected Class<? extends Page> getBackPageClass() {
                return ServiceProviderAccountCorrectionList.class;
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
