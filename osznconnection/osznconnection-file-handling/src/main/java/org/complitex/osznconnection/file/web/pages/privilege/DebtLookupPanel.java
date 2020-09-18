package org.complitex.osznconnection.file.web.pages.privilege;

import org.apache.wicket.Component;
import org.complitex.osznconnection.file.entity.RequestFileStatus;
import org.complitex.osznconnection.file.entity.privilege.Debt;
import org.complitex.osznconnection.file.entity.privilege.DebtDBF;
import org.complitex.osznconnection.file.service.PersonAccountService;
import org.complitex.osznconnection.file.service.RequestFileBean;
import org.complitex.osznconnection.file.service.privilege.DebtBean;
import org.complitex.osznconnection.file.web.component.lookup.AbstractLookupPanel;

import javax.ejb.EJB;

/**
 * @author inheaven on 02.07.2016.
 */
public class DebtLookupPanel extends AbstractLookupPanel<Debt> {

    @EJB
    private PersonAccountService personAccountService;

    @EJB
    private DebtBean debtBean;

    @EJB
    private RequestFileBean requestFileBean;

    public DebtLookupPanel(String id, Component... toUpdate) {
        super(id, toUpdate);
    }

    @Override
    protected void initInternalAddress(Debt debt, Long cityId, Long streetId, Long streetTypeId,
                                       Long buildingId, String apartment) {
        debt.setCityId(cityId);
        debt.setStreetId(streetId);
        debt.setStreetTypeId(streetTypeId);
        debt.setBuildingId(buildingId);
        debt.putField(DebtDBF.APT, "_CYR", apartment != null ? apartment : "");
    }

    @Override
    protected boolean isInternalAddressCorrect(Debt debt) {
        return debt.getCityId() != null && debt.getCityId() > 0
                && debt.getStreetId() != null && debt.getStreetId() > 0
                && debt.getBuildingId() != null && debt.getBuildingId() > 0;
    }

    @Override
    protected void updateAccountNumber(Debt debt, String accountNumber) {
        personAccountService.updateAccountNumber(debt, accountNumber);

        if (debtBean.isDebtBound(debt.getRequestFileId())) {
            requestFileBean.updateStatus(debt.getRequestFileId(), RequestFileStatus.BOUND);
        }
    }
}
