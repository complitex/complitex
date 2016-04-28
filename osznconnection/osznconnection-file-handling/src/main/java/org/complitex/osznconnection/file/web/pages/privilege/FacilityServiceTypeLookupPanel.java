package org.complitex.osznconnection.file.web.pages.privilege;

import org.apache.wicket.Component;
import org.complitex.osznconnection.file.entity.privilege.FacilityServiceType;
import org.complitex.osznconnection.file.entity.privilege.FacilityServiceTypeDBF;
import org.complitex.osznconnection.file.entity.privilege.PrivilegeGroup;
import org.complitex.osznconnection.file.service.LookupBean;
import org.complitex.osznconnection.file.service.PersonAccountService;
import org.complitex.osznconnection.file.service.privilege.PrivilegeGroupService;
import org.complitex.osznconnection.file.web.component.lookup.AbstractLookupPanel;

import javax.ejb.EJB;

/**
 *
 * @author Artem
 */
public class FacilityServiceTypeLookupPanel extends AbstractLookupPanel<FacilityServiceType> {

    @EJB
    private LookupBean lookupBean;

    @EJB
    private PersonAccountService personAccountService;

    @EJB
    private PrivilegeGroupService privilegeGroupService;

    public FacilityServiceTypeLookupPanel(String id, Component... toUpdate) {
        super(id, toUpdate);
    }

    @Override
    protected void initInternalAddress(FacilityServiceType facilityServiceType, Long cityId, Long streetId,
                                       Long streetTypeId, Long buildingId, String apartment) {
        facilityServiceType.setCityId(cityId);
        facilityServiceType.setStreetId(streetId);
        facilityServiceType.setStreetTypeId(streetTypeId);
        facilityServiceType.setBuildingId(buildingId);
        facilityServiceType.putField(FacilityServiceTypeDBF.APT, apartment != null ? apartment : "");
    }

    @Override
    protected boolean isInternalAddressCorrect(FacilityServiceType facilityServiceType) {
        return facilityServiceType.getCityId() != null && facilityServiceType.getCityId() > 0
                && facilityServiceType.getStreetId() != null && facilityServiceType.getStreetId() > 0
                && facilityServiceType.getBuildingId() != null && facilityServiceType.getBuildingId() > 0;
    }


    @Override
    protected void updateAccountNumber(FacilityServiceType facilityServiceType, String accountNumber) {
        personAccountService.updateAccountNumber(facilityServiceType, accountNumber);

        PrivilegeGroup privilegeGroup = privilegeGroupService.getPrivilegeGroup(
                facilityServiceType.getRequestFileId(),
                facilityServiceType.getStringField(FacilityServiceTypeDBF.IDPIL),
                facilityServiceType.getStringField(FacilityServiceTypeDBF.PASPPIL),
                facilityServiceType.getStringField(FacilityServiceTypeDBF.FIO),
                facilityServiceType.getStringField(FacilityServiceTypeDBF.CDUL),
                facilityServiceType.getStringField(FacilityServiceTypeDBF.HOUSE),
                facilityServiceType.getStringField(FacilityServiceTypeDBF.BUILD),
                facilityServiceType.getStringField(FacilityServiceTypeDBF.APT));

        if (privilegeGroup.getDwellingCharacteristics() != null){
            personAccountService.updateAccountNumber(privilegeGroup.getDwellingCharacteristics(), accountNumber);
        }
    }
}
