package org.complitex.osznconnection.file.web.pages.subsidy;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebPage;
import org.complitex.osznconnection.file.entity.RequestFile;
import org.complitex.osznconnection.file.service.process.ProcessManagerService;
import org.complitex.osznconnection.file.service.subsidy.OschadbankResponseBean;
import org.complitex.osznconnection.file.service.subsidy.OschadbankResponseFileBean;
import org.complitex.osznconnection.file.web.AbstractFileList;
import org.complitex.template.web.security.SecurityRole;

import javax.ejb.EJB;

import static org.complitex.osznconnection.file.entity.RequestFileType.OSCHADBANK_RESPONSE;
import static org.complitex.osznconnection.file.service.process.ProcessType.LOAD_OSCHADBANK_RESPONSE;
import static org.complitex.osznconnection.organization_type.strategy.OsznOrganizationTypeStrategy.SUBSIDY_DEPARTMENT_TYPE;

/**
 * @author Anatoly A. Ivanov
 * 25.04.2019 18:37
 */
@AuthorizeInstantiation(SecurityRole.AUTHORIZED)
public class OschadbankResponseFileList extends AbstractFileList {
    @EJB
    private ProcessManagerService processManagerService;

    @EJB
    private OschadbankResponseBean oschadbankResponseBean;

    @EJB
    private OschadbankResponseFileBean oschadbankResponseFileBean;

    public OschadbankResponseFileList() {
        super(OSCHADBANK_RESPONSE, null, LOAD_OSCHADBANK_RESPONSE, null, null,
                null, new Long[]{SUBSIDY_DEPARTMENT_TYPE});
    }

    @Override
    protected Class<? extends WebPage> getItemListPageClass() {
        return OschadbankResponseList.class;
    }

    @Override
    protected void load(Long serviceProviderId, Long userOrganizationId, Long organizationId, int year, int monthFrom, int monthTo) {
        processManagerService.loadOschadbankResponse(serviceProviderId, userOrganizationId, organizationId, year, monthFrom);
    }

    @Override
    protected void delete(RequestFile requestFile) {
        oschadbankResponseBean.delete(requestFile.getId());
        oschadbankResponseFileBean.delete(requestFile.getId());

        super.delete(requestFile);
    }
}
