package org.complitex.osznconnection.file.web.pages.privilege;

import org.apache.wicket.Component;
import org.complitex.osznconnection.file.entity.RequestFileStatus;
import org.complitex.osznconnection.file.entity.privilege.PrivilegeProlongation;
import org.complitex.osznconnection.file.entity.privilege.PrivilegeProlongationDBF;
import org.complitex.osznconnection.file.service.LookupBean;
import org.complitex.osznconnection.file.service.PersonAccountService;
import org.complitex.osznconnection.file.service.RequestFileBean;
import org.complitex.osznconnection.file.service.privilege.PrivilegeProlongationBean;
import org.complitex.osznconnection.file.web.component.lookup.AbstractLookupPanel;

import javax.ejb.EJB;

/**
 * @author inheaven on 02.07.2016.
 */
public class PrivilegeProlongationLookupPanel extends AbstractLookupPanel<PrivilegeProlongation> {

    @EJB
    private LookupBean lookupBean;

    @EJB
    private PersonAccountService personAccountService;

    @EJB
    private PrivilegeProlongationBean privilegeProlongationBean;

    @EJB
    private RequestFileBean requestFileBean;

    public PrivilegeProlongationLookupPanel(String id, Component... toUpdate) {
        super(id, toUpdate);
    }

    @Override
    protected void initInternalAddress(PrivilegeProlongation privilegeProlongation, Long cityId, Long streetId, Long streetTypeId,
                                       Long buildingId, String apartment) {
        privilegeProlongation.setCityId(cityId);
        privilegeProlongation.setStreetId(streetId);
        privilegeProlongation.setStreetTypeId(streetTypeId);
        privilegeProlongation.setBuildingId(buildingId);
        privilegeProlongation.putField(PrivilegeProlongationDBF.APT, apartment != null ? apartment : "");
    }

    @Override
    protected boolean isInternalAddressCorrect(PrivilegeProlongation privilegeProlongation) {
        return privilegeProlongation.getCityId() != null && privilegeProlongation.getCityId() > 0
                && privilegeProlongation.getStreetId() != null && privilegeProlongation.getStreetId() > 0
                && privilegeProlongation.getBuildingId() != null && privilegeProlongation.getBuildingId() > 0;
    }

    @Override
    protected void updateAccountNumber(PrivilegeProlongation privilegeProlongation, String accountNumber) {
        personAccountService.updateAccountNumber(privilegeProlongation, accountNumber);

        if (privilegeProlongationBean.isPrivilegeProlongationBound(privilegeProlongation.getRequestFileId())) {
            requestFileBean.updateStatus(privilegeProlongation.getRequestFileId(), RequestFileStatus.BOUND);
        }
    }
}
