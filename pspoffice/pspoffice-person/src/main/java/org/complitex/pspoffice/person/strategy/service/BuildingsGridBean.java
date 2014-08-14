/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.complitex.address.strategy.building.BuildingStrategy;
import org.complitex.address.strategy.building.entity.Building;
import org.complitex.address.strategy.building_address.BuildingAddressStrategy;
import org.complitex.address.strategy.district.DistrictStrategy;
import org.complitex.address.strategy.street.StreetStrategy;
import org.complitex.address.strategy.street_type.StreetTypeStrategy;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.service.AbstractBean;
import org.complitex.dictionary.service.LocaleBean;
import org.complitex.dictionary.service.SessionBean;
import org.complitex.dictionary.strategy.organization.IOrganizationStrategy;
import org.complitex.pspoffice.person.strategy.entity.grid.BuildingsGridEntity;
import org.complitex.pspoffice.person.strategy.entity.grid.BuildingsGridFilter;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.*;

/**
 *
 * @author Artem
 */
@Stateless
public class BuildingsGridBean extends AbstractBean {
    private static final String MAPPING = BuildingsGridBean.class.getName();

    @EJB
    private BuildingStrategy buildingStrategy;

    @EJB
    private StreetStrategy streetStrategy;

    @EJB
    private DistrictStrategy districtStrategy;

    @EJB
    private SessionBean sessionBean;

    @EJB(lookup = IOrganizationStrategy.BEAN_LOOKUP)
    private IOrganizationStrategy organizationStrategy;

    @EJB
    private LocaleBean localeBean;

    public BuildingsGridFilter newFilter(long cityId, Long streetId, Locale locale) {
        final boolean isAdmin = sessionBean.isAdmin();
        return new BuildingsGridFilter(cityId, streetId,
                !isAdmin ? sessionBean.getPermissionString("building") : null,
                !isAdmin ? sessionBean.getPermissionString("apartment") : null,
                !isAdmin ? sessionBean.getPermissionString("room") : null,
                locale);
    }

    private Map<String, Object> newParamsMap(BuildingsGridFilter filter) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("additionalAddressAT", BuildingStrategy.BUILDING_ADDRESS);
        params.put("buildingAddressNumberAT", BuildingAddressStrategy.NUMBER);
        params.put("buildingDistrictAT", BuildingStrategy.DISTRICT);
        params.put("cityId", filter.getCityId());
        params.put("districtId", filter.getDistrictId());
        params.put("streetId", filter.getStreetId());
        params.put("buildingNumber", filter.getBuildingNumber());
        params.put("buildingPermissionString", filter.getBuildingPermissionString());
        params.put("apartmentPermissionString", filter.getApartmentPermissionString());
        params.put("roomPermissionString", filter.getRoomPermissionString());
        params.put("start", filter.getStart());
        params.put("size", filter.getSize());
        return params;
    }

    private Map<String, Object> enhanceParams(Map<String, Object> params, Locale locale) {
        params.put("sortLocaleId", localeBean.convert(locale).getId());
        params.put("buildingAddressCorpAT", BuildingAddressStrategy.CORP);
        params.put("buildingAddressStructureAT", BuildingAddressStrategy.STRUCTURE);
        params.put("streetNameAT", StreetStrategy.NAME);
        params.put("streetTypeAT", StreetStrategy.STREET_TYPE);
        params.put("streetTypeNameAT", StreetTypeStrategy.NAME);
        return params;
    }

    public int count(BuildingsGridFilter filter) {
        return (Integer) sqlSession().selectOne(MAPPING + ".count", newParamsMap(filter));
    }

    public List<BuildingsGridEntity> find(BuildingsGridFilter filter) {
        @SuppressWarnings("unchecked")
        List<Map<String, Long>> data = sqlSession().selectList(MAPPING + ".find",
                enhanceParams(newParamsMap(filter), filter.getLocale()));
        final List<BuildingsGridEntity> result = Lists.newArrayList();
        if (data != null && !data.isEmpty()) {
            for (Map<String, Long> item : data) {
                //building
                final long buildingId = item.get("buildingId");
                final Building buildingObject = buildingStrategy.findById(buildingId, true);
                String building = null;

                //find appropriate address
                {
                    DomainObject buildingAddress = buildingObject.getPrimaryAddress();
                    if (filter.getStreetId() != null) {
                        for (DomainObject address : buildingObject.getAllAddresses()) {
                            if (address.getParentEntityId().equals(BuildingAddressStrategy.PARENT_STREET_ENTITY_ID)
                                    && address.getParentId().equals(filter.getStreetId())) {
                                buildingAddress = address;
                                break;
                            }
                        }
                    }
                    buildingObject.setAccompaniedAddress(buildingAddress);
                    building = buildingStrategy.displayDomainObject(buildingObject, filter.getLocale());
                }

                //street
                Long streetId = null;
                if (filter.getStreetId() != null) {
                    streetId = filter.getStreetId();
                } else {
                    DomainObject buildingAddress = buildingObject.getPrimaryAddress();
                    if (buildingAddress.getParentEntityId().equals(BuildingAddressStrategy.PARENT_STREET_ENTITY_ID)) {
                        streetId = buildingAddress.getParentId();
                    }
                }
                String street = null;
                if (streetId != null) {
                    DomainObject streetObject = streetStrategy.findById(streetId, true);
                    street = streetStrategy.displayDomainObject(streetObject, filter.getLocale());
                }

                //district
                final Long districtId = buildingStrategy.getDistrictId(buildingObject);
                String district = null;
                if (districtId != null) {
                    DomainObject districtObject = districtStrategy.findById(districtId, true);
                    district = districtStrategy.displayDomainObject(districtObject, filter.getLocale());
                }

                final int apartments = item.get("apartments").intValue();

                final List<DomainObject> organizations = Lists.newArrayList();
                final Set<Long> organizationIds = buildingObject.getSubjectIds();
                for (long organizationId : organizationIds) {
                    if (organizationId > 0) {
                        final DomainObject organization = organizationStrategy.findById(organizationId, true);
                        organizations.add(organization);
                    }
                }

                result.add(new BuildingsGridEntity(district, districtId, street, streetId, building, buildingId,
                        apartments, Collections.unmodifiableList(organizations)));
            }
        }
        return result;
    }
}
