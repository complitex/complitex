package org.complitex.osznconnection.file.web.pages.privilege;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebPage;
import org.complitex.osznconnection.file.service.process.ProcessManagerBean;
import org.complitex.osznconnection.file.web.AbstractFileList;
import org.complitex.osznconnection.organization_type.strategy.OsznOrganizationTypeStrategy;

import javax.ejb.EJB;

import static org.complitex.osznconnection.file.entity.RequestFileType.FACILITY_LOCAL;
import static org.complitex.osznconnection.file.service.process.ProcessType.*;

/**
 * @author inheaven on 017 17.11.16.
 */
@AuthorizeInstantiation("PRIVILEGE_LOCAL")
public class FacilityLocalFileList extends AbstractFileList{
    @EJB
    private ProcessManagerBean processManagerBean;

    public FacilityLocalFileList() {
        super(FACILITY_LOCAL, LOAD_FACILITY_LOCAL, BIND_FACILITY_LOCAL, FILL_FACILITY_LOCAL, SAVE_FACILITY_LOCAL,
                new Long[]{OsznOrganizationTypeStrategy.PRIVILEGE_DEPARTMENT_TYPE});
    }

    @Override
    protected Class<? extends WebPage> getItemListPageClass() {
        return FacilityLocalList.class;
    }
}
