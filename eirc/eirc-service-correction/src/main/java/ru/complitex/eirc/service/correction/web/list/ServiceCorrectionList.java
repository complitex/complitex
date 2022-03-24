package ru.complitex.eirc.service.correction.web.list;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.common.strategy.StringLocaleBean;
import ru.complitex.common.strategy.organization.IOrganizationStrategy;
import ru.complitex.correction.entity.Correction;
import ru.complitex.correction.web.AbstractCorrectionList;
import ru.complitex.eirc.service.correction.entity.ServiceCorrection;
import ru.complitex.eirc.service.correction.service.ServiceCorrectionBean;
import ru.complitex.template.web.security.SecurityRole;
import ru.complitex.eirc.organization.strategy.EircOrganizationStrategy;
import ru.complitex.eirc.service.correction.web.edit.ServiceCorrectionEdit;
import ru.complitex.eirc.service.entity.Service;
import ru.complitex.eirc.service.service.ServiceBean;

import javax.ejb.EJB;


/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 28.11.13 15:36
 */
@AuthorizeInstantiation(SecurityRole.AUTHORIZED)
public class ServiceCorrectionList extends AbstractCorrectionList {
    @EJB
    private ServiceCorrectionBean serviceCorrectionBean;

    @EJB
    private ServiceBean serviceBean;

    @EJB
    private StringLocaleBean stringLocaleBean;

    @EJB(name = IOrganizationStrategy.BEAN_NAME, beanInterface = IOrganizationStrategy.class)
    private EircOrganizationStrategy organizationStrategy;

    private static final Long[] ORGANIZATION_TYPES = null;

    public ServiceCorrectionList() {
        super("organization");
    }

    @Override
    protected ServiceCorrection newCorrection() {
        return new ServiceCorrection();
    }

    @Override
    protected Class<? extends WebPage> getEditPage() {
        return ServiceCorrectionEdit.class;
    }

    @Override
    protected PageParameters getEditPageParams(Long objectCorrectionId) {
        PageParameters parameters = new PageParameters();

        if (objectCorrectionId != null) {
            parameters.set(ServiceCorrectionEdit.CORRECTION_ID, objectCorrectionId);
        }

        return parameters;
    }

    @Override
    protected IModel<String> getTitleModel() {
        return new ResourceModel("title");
    }

    @Override
    protected String displayInternalObject(Correction correction) {
        Service service = serviceBean.getService(correction.getObjectId());
        return service == null? "" : service.getName(stringLocaleBean.convert(getLocale()));
    }

    @Override
    protected Long[] getOrganizationTypeIds(){
        return ORGANIZATION_TYPES;
    }
}
