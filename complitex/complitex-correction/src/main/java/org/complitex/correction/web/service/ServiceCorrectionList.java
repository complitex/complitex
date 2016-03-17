package org.complitex.correction.web.service;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.common.entity.FilterWrapper;
import org.complitex.correction.entity.ServiceCorrection;
import org.complitex.correction.service.ServiceCorrectionBean;
import org.complitex.correction.web.AbstractCorrectionList;
import org.complitex.template.web.security.SecurityRole;

import javax.ejb.EJB;
import java.util.List;

/**
 * @author inheaven on 18.06.2015 16:16.
 */
@AuthorizeInstantiation(SecurityRole.AUTHORIZED)
public class ServiceCorrectionList extends AbstractCorrectionList<ServiceCorrection> {

    @EJB
    private ServiceCorrectionBean serviceCorrectionBean;

    public ServiceCorrectionList() {
        super("service");
    }

    @Override
    protected ServiceCorrection newCorrection() {
        return new ServiceCorrection();
    }

    @Override
    protected List<ServiceCorrection> getCorrections(FilterWrapper<ServiceCorrection> filterWrapper) {
        return serviceCorrectionBean.getServiceCorrections(filterWrapper);
    }

    @Override
    protected Long getCorrectionsCount(FilterWrapper<ServiceCorrection> filterWrapper) {
        return serviceCorrectionBean.getServiceCorrectionsCount(filterWrapper);
    }

    @Override
    protected Class<? extends WebPage> getEditPage() {
        return ServiceCorrectionEdit.class;
    }

    @Override
    protected PageParameters getEditPageParams(Long objectCorrectionId) {
        return new PageParameters().add("correction_id", objectCorrectionId);
    }
}
