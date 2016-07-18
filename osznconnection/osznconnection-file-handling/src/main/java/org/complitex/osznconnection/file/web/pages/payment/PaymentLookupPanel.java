package org.complitex.osznconnection.file.web.pages.payment;

import org.apache.wicket.Component;
import org.complitex.common.strategy.organization.IOrganizationStrategy;
import org.complitex.osznconnection.file.entity.RequestFile;
import org.complitex.osznconnection.file.entity.RequestFileStatus;
import org.complitex.osznconnection.file.entity.subsidy.Payment;
import org.complitex.osznconnection.file.entity.subsidy.PaymentDBF;
import org.complitex.osznconnection.file.entity.subsidy.RequestFileGroup;
import org.complitex.osznconnection.file.service.LookupBean;
import org.complitex.osznconnection.file.service.PersonAccountService;
import org.complitex.osznconnection.file.service.RequestFileBean;
import org.complitex.osznconnection.file.service.subsidy.BenefitBean;
import org.complitex.osznconnection.file.service.subsidy.PaymentBean;
import org.complitex.osznconnection.file.service.subsidy.RequestFileGroupBean;
import org.complitex.osznconnection.file.web.component.lookup.AbstractLookupPanel;
import org.complitex.osznconnection.organization.strategy.OsznOrganizationStrategy;

import javax.ejb.EJB;

/**
 * Панель для поиска номера л/c по различным параметрам: по адресу, по номеру лиц. счета, по номеру в мегабанке.
 * @author Artem
 */
public class PaymentLookupPanel extends AbstractLookupPanel<Payment> {

    @EJB
    private LookupBean lookupBean;

    @EJB
    private PersonAccountService personAccountService;

    @EJB(name = IOrganizationStrategy.BEAN_NAME, beanInterface = IOrganizationStrategy.class)
    private OsznOrganizationStrategy organizationStrategy;

    @EJB
    private PaymentBean paymentBean;

    @EJB
    private BenefitBean benefitBean;

    @EJB
    private RequestFileBean requestFileBean;

    @EJB
    private RequestFileGroupBean requestFileGroupBean;


    public PaymentLookupPanel(String id, Component... toUpdate) {
        super(id, toUpdate);
    }

    @Override
    protected void initInternalAddress(Payment payment, Long cityId, Long streetId, Long streetTypeId, Long buildingId, String apartment) {
        payment.setCityId(cityId);
        payment.setStreetId(streetId);
        payment.setStreetTypeId(streetTypeId);
        payment.setBuildingId(buildingId);
        payment.putField(PaymentDBF.FLAT, "_CYR", apartment != null ? apartment : "");
    }

    @Override
    protected boolean isInternalAddressCorrect(Payment payment) {
        return payment.getCityId() != null && payment.getCityId() > 0
                && payment.getStreetId() != null && payment.getStreetId() > 0
                && payment.getBuildingId() != null && payment.getBuildingId() > 0;
    }

    @Override
    protected void updateAccountNumber(Payment payment, String accountNumber) {
        personAccountService.updateAccountNumber(payment, accountNumber);

        //update file status
        RequestFile paymentFile = requestFileBean.getRequestFile(payment.getRequestFileId());
        RequestFileGroup group = requestFileGroupBean.getRequestFileGroup(paymentFile.getGroupId());

        if (paymentBean.isPaymentFileBound(payment.getRequestFileId())
                && (group.getBenefitFile() == null || benefitBean.isBenefitFileBound(group.getBenefitFile().getId()))){
            group.setStatus(RequestFileStatus.BOUND);
            requestFileGroupBean.save(group);
        }
    }
}
