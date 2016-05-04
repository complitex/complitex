package org.complitex.osznconnection.file.web.pages.actualpayment;

import org.apache.wicket.Component;
import org.complitex.common.entity.Cursor;
import org.complitex.osznconnection.file.entity.AccountDetail;
import org.complitex.osznconnection.file.entity.RequestFile;
import org.complitex.osznconnection.file.entity.RequestFileStatus;
import org.complitex.osznconnection.file.entity.subsidy.ActualPayment;
import org.complitex.osznconnection.file.entity.subsidy.ActualPaymentDBF;
import org.complitex.osznconnection.file.service.LookupBean;
import org.complitex.osznconnection.file.service.PersonAccountService;
import org.complitex.osznconnection.file.service.RequestFileBean;
import org.complitex.osznconnection.file.service.subsidy.ActualPaymentBean;
import org.complitex.osznconnection.file.service_provider.exception.DBException;
import org.complitex.osznconnection.file.web.component.lookup.AbstractLookupPanel;

import javax.ejb.EJB;

/**
 *
 * @author Artem
 */
public class ActualPaymentLookupPanel extends AbstractLookupPanel<ActualPayment> {

    @EJB
    private LookupBean lookupBean;

    @EJB
    private PersonAccountService personAccountService;

    @EJB
    private RequestFileBean requestFileBean;

    @EJB
    private ActualPaymentBean actualPaymentBean;

    public ActualPaymentLookupPanel(String id, Component... toUpdate) {
        super(id, toUpdate);
    }

    @Override
    protected void initInternalAddress(ActualPayment actualPayment, Long cityId, Long streetId, Long streetTypeId,
            Long buildingId, String apartment) {
        actualPayment.setCityId(cityId);
        actualPayment.setStreetId(streetId);
        actualPayment.setStreetTypeId(streetTypeId);
        actualPayment.setBuildingId(buildingId);
        actualPayment.putField(ActualPaymentDBF.FLAT, apartment != null ? apartment : "");
    }

    @Override
    protected boolean isInternalAddressCorrect(ActualPayment actualPayment) {
        return actualPayment.getCityId() != null && actualPayment.getCityId() > 0
                && actualPayment.getStreetId() != null && actualPayment.getStreetId() > 0
                && actualPayment.getBuildingId() != null && actualPayment.getBuildingId() > 0;
    }


    @Override
    protected Cursor<AccountDetail> getAccountDetails(ActualPayment actualPayment)throws DBException {
        RequestFile actualPaymentFile = requestFileBean.getRequestFile(actualPayment.getRequestFileId());

        return lookupBean.getAccountDetails(actualPayment.getOutgoingDistrict(), getServiceProviderCode(actualPayment),
                actualPayment.getOutgoingStreetType(), actualPayment.getOutgoingStreet(),
                actualPayment.getOutgoingBuildingNumber(), actualPayment.getOutgoingBuildingCorp(),
                actualPayment.getOutgoingApartment(), actualPaymentBean.getFirstDay(actualPayment, actualPaymentFile),
                actualPayment.getUserOrganizationId());
    }

    @Override
    protected void updateAccountNumber(ActualPayment actualPayment, String accountNumber) {
        personAccountService.updateAccountNumber(actualPayment, accountNumber);

        if (actualPaymentBean.isActualPaymentFileBound(actualPayment.getRequestFileId())) {
            requestFileBean.updateStatus(actualPayment.getRequestFileId(), RequestFileStatus.BOUND);
        }
    }
}
