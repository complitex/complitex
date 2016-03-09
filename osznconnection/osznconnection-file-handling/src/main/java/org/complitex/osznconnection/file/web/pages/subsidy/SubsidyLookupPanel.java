package org.complitex.osznconnection.file.web.pages.subsidy;

import org.apache.wicket.Component;
import org.complitex.common.entity.Cursor;
import org.complitex.osznconnection.file.entity.AccountDetail;
import org.complitex.osznconnection.file.entity.Subsidy;
import org.complitex.osznconnection.file.entity.SubsidyDBF;
import org.complitex.osznconnection.file.service.LookupBean;
import org.complitex.osznconnection.file.service.PersonAccountService;
import org.complitex.osznconnection.file.service.SubsidyService;
import org.complitex.osznconnection.file.service_provider.exception.DBException;
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
        subsidy.setField(SubsidyDBF.FLAT + "_CYR", apartment != null ? apartment : "");
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
    protected Cursor<AccountDetail> getAccountDetails(Subsidy subsidy) throws DBException {
        return lookupBean.getAccountDetails(subsidy.getOutgoingDistrict(), getServiceProviderCode(subsidy),
                subsidy.getOutgoingStreetType(), subsidy.getOutgoingStreet(), subsidy.getOutgoingBuildingNumber(),
                subsidy.getOutgoingBuildingCorp(), subsidy.getOutgoingApartment(), (Date) subsidy.getField(SubsidyDBF.DAT1),
                subsidy.getUserOrganizationId());
    }

    @Override
    protected void updateAccountNumber(Subsidy subsidy, String accountNumber) {
        personAccountService.updateAccountNumber(subsidy, accountNumber);
    }

    @Override
    protected String getTitle(Subsidy subsidy) {
        return subsidy.getFio() + ", " + subsidy.getAddress(getLocale());
    }
}
