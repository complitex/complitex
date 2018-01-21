package org.complitex.correction.web.service;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.correction.web.AbstractCorrectionList;
import org.complitex.template.web.security.SecurityRole;

/**
 * @author inheaven on 18.06.2015 16:16.
 */
@AuthorizeInstantiation(SecurityRole.AUTHORIZED)
public class ServiceCorrectionList extends AbstractCorrectionList{

    public ServiceCorrectionList() {
        super("service");
    }

    @Override
    protected Class<? extends WebPage> getEditPage() {
        return ServiceCorrectionEdit.class;
    }

    @Override
    protected PageParameters getEditPageParams(Long objectCorrectionId) {
        return new PageParameters().set("correction_id", objectCorrectionId);
    }
}
