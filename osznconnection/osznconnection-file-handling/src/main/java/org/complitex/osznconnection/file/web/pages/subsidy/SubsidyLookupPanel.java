package org.complitex.osznconnection.file.web.pages.subsidy;

import org.apache.wicket.Component;
import org.complitex.common.entity.Cursor;
import org.complitex.osznconnection.file.entity.AccountDetail;
import org.complitex.osznconnection.file.entity.RequestFileStatus;
import org.complitex.osznconnection.file.entity.subsidy.Subsidy;
import org.complitex.osznconnection.file.entity.subsidy.SubsidyDBF;
import org.complitex.osznconnection.file.service.LookupBean;
import org.complitex.osznconnection.file.service.PersonAccountService;
import org.complitex.osznconnection.file.service.RequestFileBean;
import org.complitex.osznconnection.file.service.subsidy.SubsidyBean;
import org.complitex.osznconnection.file.service.subsidy.SubsidyService;
import org.complitex.osznconnection.file.web.component.lookup.AbstractLookupPanel;

import javax.ejb.EJB;
import java.util.Date;

public class SubsidyLookupPanel extends AbstractLookupPanel<Subsidy> {

    @EJB
    private LookupBean lookupBean;

    @EJB
    private PersonAccountService personAccountService;

    @EJB
    private SubsidyService subsidyService;

    @EJB
    private SubsidyBean subsidyBean;

    @EJB
    private RequestFileBean requestFileBean;

    public SubsidyLookupPanel(String id, Component... toUpdate) {
        super(id, toUpdate);
    }

    @Override
    protected void initInternalAddress(Subsidy subsidy, Long cityId, Long streetId, Long streetTypeId,
            Long buildingId, String apartment) {
        subsidy.setCityId(cityId);
        subsidy.setStreetId(streetId);
        subsidy.setStreetTypeId(streetTypeId);
        subsidy.setBuildingId(buildingId);
        subsidy.putField(SubsidyDBF.FLAT, "_CYR", apartment != null ? apartment : "");
    }

    @Override
    protected boolean isInternalAddressCorrect(Subsidy subsidy) {
        return subsidy.getCityId() != null && subsidy.getCityId() > 0
                && subsidy.getStreetId() != null && subsidy.getStreetId() > 0
                && subsidy.getBuildingId() != null && subsidy.getBuildingId() > 0;
    }

    @Override
    protected void resolveOutgoingAddress(Subsidy subsidy) {
        lookupBean.resolveOutgoingAddress(subsidy);
    }

    @Override
    protected Cursor<AccountDetail> getAccountDetails(Subsidy subsidy){
        return lookupBean.getAccountDetails(subsidy.getOutgoingDistrict(), getServiceProviderCode(subsidy),
                subsidy.getOutgoingStreetType(), subsidy.getOutgoingStreet(), subsidy.getOutgoingBuildingNumber(),
                subsidy.getOutgoingBuildingCorp(), subsidy.getOutgoingApartment(), (Date) subsidy.getField(SubsidyDBF.DAT1),
                subsidy.getUserOrganizationId());
    }

    @Override
    protected void updateAccountNumber(Subsidy subsidy, String accountNumber) {
        personAccountService.updateAccountNumber(subsidy, accountNumber);

        //update status
        if (subsidyBean.isSubsidyFileBound(subsidy.getRequestFileId())){
            requestFileBean.updateStatus(subsidy.getRequestFileId(), RequestFileStatus.BOUND);
        }
    }

    @Override
    protected String getTitle(Subsidy subsidy) {
        return subsidy.getFio() + ", " + subsidy.getAddress(getLocale());
    }
}
