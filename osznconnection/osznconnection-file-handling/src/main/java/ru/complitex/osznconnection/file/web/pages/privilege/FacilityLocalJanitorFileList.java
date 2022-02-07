package ru.complitex.osznconnection.file.web.pages.privilege;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebPage;
import ru.complitex.osznconnection.file.service.process.ProcessManagerService;
import ru.complitex.osznconnection.file.web.AbstractFileList;

import javax.ejb.EJB;
import java.util.List;
import java.util.Map;

import static ru.complitex.osznconnection.file.entity.RequestFileSubType.FACILITY_LOCAL_JANITOR;
import static ru.complitex.osznconnection.file.entity.RequestFileType.FACILITY_LOCAL;
import static ru.complitex.osznconnection.file.service.process.ProcessType.*;
import static ru.complitex.osznconnection.organization_type.strategy.OsznOrganizationTypeStrategy.PRIVILEGE_DEPARTMENT_TYPE;

/**
 * @author inheaven on 009 09.12.16.
 */
@AuthorizeInstantiation("PRIVILEGE_LOCAL")
public class FacilityLocalJanitorFileList extends AbstractFileList{
    @EJB
    private ProcessManagerService processManagerService;

    public FacilityLocalJanitorFileList() {
        super(FACILITY_LOCAL, FACILITY_LOCAL_JANITOR,
                LOAD_FACILITY_LOCAL, BIND_FACILITY_LOCAL, FILL_FACILITY_LOCAL, SAVE_FACILITY_LOCAL,
                new Long[]{PRIVILEGE_DEPARTMENT_TYPE});
    }

    @Override
    protected Class<? extends WebPage> getItemListPageClass() {
        return FacilityLocalJanitorList.class;
    }

    @Override
    protected void load(Long serviceProviderId, Long userOrganizationId, Long organizationId, int year, int monthFrom, int monthTo) {
        processManagerService.loadFacilityJanitorLocal(serviceProviderId, userOrganizationId, organizationId, year, monthFrom);
    }

    @Override
    protected void save(List<Long> selectedFileIds, Map<Enum<?>, Object> parameters) {
        processManagerService.saveFacilityLocal(selectedFileIds, parameters);
    }
}
