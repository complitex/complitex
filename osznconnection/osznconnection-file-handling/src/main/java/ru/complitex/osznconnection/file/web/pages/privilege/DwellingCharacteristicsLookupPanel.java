package ru.complitex.osznconnection.file.web.pages.privilege;

import org.apache.wicket.Component;
import ru.complitex.osznconnection.file.entity.RequestFileStatus;
import ru.complitex.osznconnection.file.entity.privilege.DwellingCharacteristics;
import ru.complitex.osznconnection.file.entity.privilege.DwellingCharacteristicsDBF;
import ru.complitex.osznconnection.file.entity.privilege.FacilityServiceType;
import ru.complitex.osznconnection.file.entity.privilege.PrivilegeGroup;
import ru.complitex.osznconnection.file.service.LookupBean;
import ru.complitex.osznconnection.file.service.PersonAccountService;
import ru.complitex.osznconnection.file.service.RequestFileBean;
import ru.complitex.osznconnection.file.service.privilege.DwellingCharacteristicsBean;
import ru.complitex.osznconnection.file.service.privilege.FacilityServiceTypeBean;
import ru.complitex.osznconnection.file.service.privilege.PrivilegeGroupService;
import ru.complitex.osznconnection.file.web.component.lookup.AbstractLookupPanel;

import javax.ejb.EJB;

public class DwellingCharacteristicsLookupPanel extends AbstractLookupPanel<DwellingCharacteristics> {

    @EJB
    private LookupBean lookupBean;

    @EJB
    private PersonAccountService personAccountService;

    @EJB
    private PrivilegeGroupService privilegeGroupService;

    @EJB
    private DwellingCharacteristicsBean dwellingCharacteristicsBean;

    @EJB
    private FacilityServiceTypeBean facilityServiceTypeBean;

    @EJB
    private RequestFileBean requestFileBean;

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
        dwellingCharacteristics.putField(DwellingCharacteristicsDBF.APT, "_CYR", apartment != null ? apartment : "");
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

        if (dwellingCharacteristicsBean.isDwellingCharacteristicsFileBound(dwellingCharacteristics.getRequestFileId())) {
            requestFileBean.updateStatus(dwellingCharacteristics.getRequestFileId(), RequestFileStatus.BOUND);
        }

        //facilityServiceType
        PrivilegeGroup privilegeGroup = privilegeGroupService.getPrivilegeGroup(
                dwellingCharacteristics.getRequestFileId(),
                dwellingCharacteristics.getStringField(DwellingCharacteristicsDBF.IDPIL),
                dwellingCharacteristics.getStringField(DwellingCharacteristicsDBF.PASPPIL),
                dwellingCharacteristics.getStringField(DwellingCharacteristicsDBF.FIO),
                dwellingCharacteristics.getStringField(DwellingCharacteristicsDBF.CDUL),
                dwellingCharacteristics.getStringField(DwellingCharacteristicsDBF.HOUSE),
                dwellingCharacteristics.getStringField(DwellingCharacteristicsDBF.BUILD),
                dwellingCharacteristics.getStringField(DwellingCharacteristicsDBF.APT));

        if (privilegeGroup.getFacilityServiceType() != null){
            FacilityServiceType facilityServiceType = privilegeGroup.getFacilityServiceType();

            personAccountService.updateAccountNumber(facilityServiceType, accountNumber);

            if (facilityServiceTypeBean.isFacilityServiceTypeFileBound(facilityServiceType.getRequestFileId())) {
                requestFileBean.updateStatus(facilityServiceType.getRequestFileId(), RequestFileStatus.BOUND);
            }
        }
    }
}
