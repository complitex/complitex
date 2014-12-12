package ru.flexpay.eirc.service_provider_account.web.list;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.common.entity.Correction;
import org.complitex.common.entity.FilterWrapper;
import org.complitex.common.service.LocaleBean;
import org.complitex.common.strategy.organization.IOrganizationStrategy;
import org.complitex.correction.web.AbstractCorrectionList;
import org.complitex.template.web.security.SecurityRole;
import ru.flexpay.eirc.dictionary.entity.OrganizationType;
import ru.flexpay.eirc.organization.strategy.EircOrganizationStrategy;
import ru.flexpay.eirc.service_provider_account.entity.ServiceProviderAccount;
import ru.flexpay.eirc.service_provider_account.entity.ServiceProviderAccountCorrection;
import ru.flexpay.eirc.service_provider_account.service.ServiceProviderAccountBean;
import ru.flexpay.eirc.service_provider_account.service.ServiceProviderAccountCorrectionBean;
import ru.flexpay.eirc.service_provider_account.web.edit.ServiceProviderAccountCorrectionEdit;

import javax.ejb.EJB;
import java.util.List;


/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 28.11.13 15:36
 */
@AuthorizeInstantiation(SecurityRole.AUTHORIZED)
public class ServiceProviderAccountCorrectionList extends AbstractCorrectionList<ServiceProviderAccountCorrection> {
    @EJB
    private ServiceProviderAccountCorrectionBean serviceProviderAccountCorrectionBean;

    @EJB
    private ServiceProviderAccountBean serviceProviderAccountBean;

    @EJB
    private LocaleBean localeBean;

    @EJB(name = IOrganizationStrategy.BEAN_NAME, beanInterface = IOrganizationStrategy.class)
    private EircOrganizationStrategy organizationStrategy;

    private static Long[] ORGANIZATION_TYPES = {OrganizationType.PAYMENT_COLLECTOR.getId()};

    public ServiceProviderAccountCorrectionList() {
        super("organization");
    }

    @Override
    protected ServiceProviderAccountCorrection newCorrection() {
        return new ServiceProviderAccountCorrection();
    }

    @Override
    protected List<ServiceProviderAccountCorrection> getCorrections(FilterWrapper<ServiceProviderAccountCorrection> filterWrapper) {
        return serviceProviderAccountCorrectionBean.getServiceProviderAccountCorrections(filterWrapper);
    }

    @Override
    protected Long getCorrectionsCount(FilterWrapper<ServiceProviderAccountCorrection> filterWrapper) {
        return serviceProviderAccountCorrectionBean.getServiceProviderAccountCorrectionsCount(filterWrapper);
    }

    @Override
    protected Class<? extends WebPage> getEditPage() {
        return ServiceProviderAccountCorrectionEdit.class;
    }

    @Override
    protected PageParameters getEditPageParams(Long objectCorrectionId) {
        PageParameters parameters = new PageParameters();

        if (objectCorrectionId != null) {
            parameters.set(ServiceProviderAccountCorrectionEdit.CORRECTION_ID, objectCorrectionId);
        }

        return parameters;
    }

    @Override
    protected IModel<String> getTitleModel() {
        return new ResourceModel("title");
    }

    @Override
    protected String displayInternalObject(Correction correction) {
        ServiceProviderAccount service = serviceProviderAccountBean.getServiceProviderAccount(correction.getObjectId());
        return service == null? "" : service.getAccountNumber();
    }

    @Override
    protected Long[] getOrganizationTypeIds(){
        return ORGANIZATION_TYPES;
    }
}
