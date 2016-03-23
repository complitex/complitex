package org.complitex.osznconnection.file.web.pages.dwelling_charact;

import org.apache.wicket.Component;
import org.complitex.osznconnection.file.entity.DwellingCharacteristics;
import org.complitex.osznconnection.file.entity.DwellingCharacteristicsDBF;
import org.complitex.osznconnection.file.service.LookupBean;
import org.complitex.osznconnection.file.service.PersonAccountService;
import org.complitex.osznconnection.file.web.component.lookup.AbstractLookupPanel;

import javax.ejb.EJB;

public class DwellingCharacteristicsLookupPanel extends AbstractLookupPanel<DwellingCharacteristics> {

    @EJB
    private LookupBean lookupBean;

    @EJB
    private PersonAccountService personAccountService;

    public DwellingCharacteristicsLookupPanel(String id, Component... toUpdate) {
        super(id, toUpdate);
    }

    @Override
    protected void initInternalAddress(DwellingCharacteristics dwellingCharacteristics, Long cityId, Long streetId, Long streetTypeId,
                                       Long buildingId, String apartment) {
        dwellingCharacteristics.setCityId(cityId);
        dwellingCharacteristics.setStreetId(streetId);
        dwellingCharacteristics.setStreetTypeId(streetTypeId);
        dwellingCharacteristics.setBuildingId(buildingId);
        dwellingCharacteristics.putField(DwellingCharacteristicsDBF.APT, apartment != null ? apartment : "");
    }

    @Override
    protected boolean isInternalAddressCorrect(DwellingCharacteristics dwellingCharacteristics) {
        return dwellingCharacteristics.getCityId() != null && dwellingCharacteristics.getCityId() > 0
                && dwellingCharacteristics.getStreetId() != null && dwellingCharacteristics.getStreetId() > 0
                && dwellingCharacteristics.getBuildingId() != null && dwellingCharacteristics.getBuildingId() > 0;
    }

    @Override
    protected void updateAccountNumber(DwellingCharacteristics dwellingCharacteristics, String accountNumber) {
        personAccountService.updateAccountNumber(dwellingCharacteristics, accountNumber);
    }
}
