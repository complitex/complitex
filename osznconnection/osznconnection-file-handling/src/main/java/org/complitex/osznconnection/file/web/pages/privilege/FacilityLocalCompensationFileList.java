package org.complitex.osznconnection.file.web.pages.privilege;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebPage;
import org.complitex.osznconnection.file.service.process.ProcessManagerBean;
import org.complitex.osznconnection.file.web.AbstractFileList;

import javax.ejb.EJB;
import java.util.List;
import java.util.Map;

import static org.complitex.osznconnection.file.entity.RequestFileType.FACILITY_LOCAL;
import static org.complitex.osznconnection.file.service.process.ProcessType.*;
import static org.complitex.osznconnection.organization_type.strategy.OsznOrganizationTypeStrategy.PRIVILEGE_DEPARTMENT_TYPE;

/**
 * @author inheaven on 009 09.12.16.
 */
@AuthorizeInstantiation("PRIVILEGE_LOCAL")
public class FacilityLocalCompensationFileList extends AbstractFileList {
    @EJB
    private ProcessManagerBean processManagerBean;

    public FacilityLocalCompensationFileList() {
        super(FACILITY_LOCAL, LOAD_FACILITY_LOCAL, BIND_FACILITY_LOCAL, FILL_FACILITY_LOCAL, SAVE_FACILITY_LOCAL,
                new Long[]{PRIVILEGE_DEPARTMENT_TYPE});
    }

    @Override
    protected Class<? extends WebPage> getItemListPageClass() {
        return FacilityLocalList.class;
    }

    @Override
    protected void load(Long serviceProviderId, Long userOrganizationId, Long organizationId, int year, int monthFrom, int monthTo) {
        //todo processManagerBean.loadFacilityLocal(serviceProviderId, userOrganizationId, organizationId, year, monthFrom);
    }

    @Override
    protected void save(List<Long> selectedFileIds, Map<Enum<?>, Object> parameters) {
        //todo processManagerBean.saveFacilityLocal(selectedFileIds, parameters);
    }
}
