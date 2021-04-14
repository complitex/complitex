package org.complitex.correction.web.organization;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.common.strategy.organization.IOrganizationStrategy;
import org.complitex.correction.entity.Correction;
import org.complitex.correction.web.AbstractCorrectionList;
import org.complitex.template.web.security.SecurityRole;

import javax.ejb.EJB;


/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 28.11.13 15:36
 */
@AuthorizeInstantiation(SecurityRole.ADMIN_MODULE_EDIT)
public class OrganizationCorrectionList extends AbstractCorrectionList {

    @EJB(name = IOrganizationStrategy.BEAN_NAME, beanInterface = IOrganizationStrategy.class)
    private IOrganizationStrategy organizationStrategy;

    public OrganizationCorrectionList() {
        super("organization");
    }

    @Override
    protected Class<? extends WebPage> getEditPage() {
        return OrganizationCorrectionEdit.class;
    }

    @Override
    protected PageParameters getEditPageParams(Long objectCorrectionId) {
        PageParameters parameters = new PageParameters();

        if (objectCorrectionId != null) {
            parameters.set(OrganizationCorrectionEdit.CORRECTION_ID, objectCorrectionId);
        }

        return parameters;
    }

    @Override
    protected IModel<String> getTitleModel() {
        return new ResourceModel("title");
    }

    @Override
    protected String displayInternalObject(Correction correction) {
        return organizationStrategy.displayShortNameAndCode(organizationStrategy.getDomainObject(correction.getObjectId(), true),
                getLocale());
    }

    @Override
    protected boolean isDeleteVisible() {
        return true;
    }
}
