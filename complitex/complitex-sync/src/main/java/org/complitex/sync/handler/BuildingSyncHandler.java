package org.complitex.sync.handler;

import org.complitex.address.exception.RemoteCallException;
import org.complitex.address.strategy.building.BuildingStrategy;
import org.complitex.address.strategy.building.entity.Building;
import org.complitex.address.strategy.building.entity.BuildingCode;
import org.complitex.address.strategy.building_address.BuildingAddressStrategy;
import org.complitex.address.strategy.district.DistrictStrategy;
import org.complitex.address.strategy.street.StreetStrategy;
import org.complitex.address.strategy.street_type.StreetTypeStrategy;
import org.complitex.common.entity.Cursor;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.entity.DomainObjectFilter;
import org.complitex.common.strategy.organization.IOrganizationStrategy;
import org.complitex.common.web.component.ShowMode;
import org.complitex.sync.entity.DomainSync;
import org.complitex.sync.service.DomainSyncAdapter;
import org.complitex.sync.service.DomainSyncBean;
import org.complitex.sync.service.IDomainSyncHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.complitex.address.strategy.building_address.BuildingAddressStrategy.CORP;
import static org.complitex.address.strategy.building_address.BuildingAddressStrategy.NUMBER;

/**
 * @author Anatoly Ivanov
 *         Date: 03.08.2014 6:47
 */
@Stateless
public class BuildingSyncHandler implements IDomainSyncHandler {
    private final Logger log = LoggerFactory.getLogger(BuildingSyncHandler.class);

    @EJB
    private DomainSyncAdapter domainSyncAdapter;

    @EJB
    private DomainSyncBean domainSyncBean;

    @EJB
    private DistrictStrategy districtStrategy;

    @EJB
    private StreetStrategy streetStrategy;

    @EJB
    private StreetTypeStrategy streetTypeStrategy;

    @EJB
    private BuildingStrategy buildingStrategy;

    @EJB
    private BuildingAddressStrategy buildingAddressStrategy;

    @EJB(lookup = IOrganizationStrategy.BEAN_LOOKUP)
    private IOrganizationStrategy organizationStrategy;

    @Override
    public Cursor<DomainSync> getCursorDomainSyncs(DomainObject parent, Date date) throws RemoteCallException {
        switch (parent.getEntityName()){
            case "district":
                return domainSyncAdapter.getBuildingSyncs(districtStrategy.getName(parent), "", "", date);
            case "street":
                return domainSyncAdapter.getBuildingSyncs("",
                        streetStrategy.getStreetTypeShortName(parent),
                        streetStrategy.getName(parent), date);
        }

        throw new IllegalArgumentException("parent entity name not district or street");
    }


    public List<? extends DomainObject> getObjects(DomainObject parent) {
        DomainObjectFilter filter = new DomainObjectFilter().setStatus(ShowMode.ACTIVE.name());

        switch (parent.getEntityName()){
            case "district":
                //.addAdditionalParam(DISTRICT_ID, parent.getObjectId())
                break;
            case "street":
                filter.setParent(parent.getEntityName(), parent.getId());
                break;
        }

        return buildingAddressStrategy.getList(filter);
    }


    @Override
    public List<? extends DomainObject> getParentObjects(Map<String, DomainObject> map) {
        if (map.containsKey("street") && map.get("street").getId() > 0){
            return Collections.singletonList(map.get("street"));
        }else if (map.containsKey("district") && map.get("district").getId() > 0){
            return Collections.singletonList(map.get("district"));
        }else {
            return districtStrategy.getList(new DomainObjectFilter());
        }
    }



    public void insert(DomainSync sync) {
        Building building = buildingStrategy.newInstance();
//        building.setExternalId(sync.getUniqueExternalId());
        building.setValue(BuildingStrategy.DISTRICT, sync.getAdditionalParentId());

        Long organizationId = organizationStrategy.getObjectIdByCode(sync.getServicingOrganization());

        if (organizationId != null){
            building.getBuildingCodes().add(new BuildingCode(organizationId, Long.valueOf(sync.getExternalId())));
        }else{
            log.warn("Servicing organization not found by code {}", sync.getServicingOrganization());
        }

        DomainObject buildingAddress = building.getPrimaryAddress();

//        buildingAddress.setExternalId(sync.getUniqueExternalId());
        buildingAddress.setParentEntityId(BuildingAddressStrategy.PARENT_STREET_ENTITY_ID);
        buildingAddress.setParentId(sync.getParentId());

        //building number
        buildingAddress.setStringValue(NUMBER, sync.getName());

        //building part
        if (sync.getAdditionalName() != null) {
            buildingAddress.setStringValue(CORP, sync.getAdditionalName());
        }

        buildingStrategy.insert(building, sync.getDate());
        domainSyncBean.delete(sync.getId());
    }
}
