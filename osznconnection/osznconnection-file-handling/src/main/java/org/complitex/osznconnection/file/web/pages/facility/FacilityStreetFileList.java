package org.complitex.osznconnection.file.web.pages.facility;

import org.apache.wicket.Page;
import org.complitex.osznconnection.file.entity.RequestFileType;
import org.complitex.osznconnection.file.service.process.ProcessManagerService;
import org.complitex.osznconnection.file.service.process.ProcessType;

import javax.ejb.EJB;

public final class FacilityStreetFileList extends AbstractReferenceBookFileList {

    @EJB
    private ProcessManagerService processManagerService;

    public FacilityStreetFileList() {
    }

    @Override
    protected RequestFileType getRequestFileType() {
        return RequestFileType.FACILITY_STREET_REFERENCE;
    }

    @Override
    protected void load(Long userOrganizationId, Long organizationId, int year, int monthFrom, int monthTo) {
        processManagerService.loadFacilityStreetReferences(userOrganizationId, organizationId, year, monthFrom, getLocale());
    }

    @Override
    protected ProcessType getLoadProcessType() {
        return ProcessType.LOAD_FACILITY_STREET_REFERENCE;
    }

    @Override
    protected Class<? extends Page> getItemsPage() {
        return FacilityStreetList.class;
    }
}
