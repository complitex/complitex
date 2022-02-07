package ru.complitex.osznconnection.file.web.pages.actualpayment;

import org.apache.wicket.Component;
import ru.complitex.common.entity.Cursor;
import ru.complitex.osznconnection.file.entity.AccountDetail;
import ru.complitex.osznconnection.file.entity.RequestFile;
import ru.complitex.osznconnection.file.entity.RequestFileStatus;
import ru.complitex.osznconnection.file.entity.subsidy.ActualPayment;
import ru.complitex.osznconnection.file.entity.subsidy.ActualPaymentDBF;
import ru.complitex.osznconnection.file.service.LookupBean;
import ru.complitex.osznconnection.file.service.PersonAccountService;
import ru.complitex.osznconnection.file.service.RequestFileBean;
import ru.complitex.osznconnection.file.service.subsidy.ActualPaymentBean;
import ru.complitex.osznconnection.file.web.component.lookup.AbstractLookupPanel;

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
    protected Cursor<AccountDetail> getAccountDetails(ActualPayment actualPayment){
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
