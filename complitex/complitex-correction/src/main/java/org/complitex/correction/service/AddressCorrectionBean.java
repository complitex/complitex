package org.complitex.correction.service;

import com.google.common.collect.ImmutableMap;
import org.complitex.address.entity.ExternalAddress;
import org.complitex.address.entity.LocalAddress;
import org.complitex.address.strategy.apartment.ApartmentStrategy;
import org.complitex.address.strategy.city.CityStrategy;
import org.complitex.address.strategy.district.DistrictStrategy;
import org.complitex.address.strategy.room.RoomStrategy;
import org.complitex.address.strategy.street.StreetStrategy;
import org.complitex.address.strategy.street_type.StreetTypeStrategy;
import org.complitex.common.entity.FilterWrapper;
import org.complitex.correction.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Stateless
public class AddressCorrectionBean extends CorrectionBean {
    private final Logger log = LoggerFactory.getLogger(AddressCorrectionBean.class);

    public static final String NS = AddressCorrectionBean.class.getName();

    //todo add locale
    private List<Long> getObjectIds(String entity, String correction, Long entityAttributeId){
        return sqlSession().selectList(NS + ".selectObjectIds", ImmutableMap.of("entity", entity, "correction",
                correction, "entityAttributeId", entityAttributeId));
    }

    /* CITY */

    public CityCorrection getCityCorrection(Long id) {
        return sqlSession().selectOne(NS + ".selectCityCorrection", id);
    }

    public List<CityCorrection> getCityCorrections(FilterWrapper<CityCorrection> filterWrapper) {
        return sqlSession().selectList(NS + ".selectCityCorrections", filterWrapper);
    }

    public List<CityCorrection> getCityCorrections(Long objectId, String correction, Long organizationId, Long userOrganizationId) {
        return getCityCorrections(FilterWrapper.of(new CityCorrection(null, objectId, correction, organizationId, userOrganizationId, null)));
    }

    public List<CityCorrection> getCityCorrections(String correction, Long organizationId, Long userOrganizationId) {
        return getCityCorrections(null, correction, organizationId, userOrganizationId);
    }

    public List<CityCorrection> getCityCorrections(ExternalAddress address) {
        return getCityCorrections(address.getCity(), address.getOrganizationId(), address.getUserOrganizationId());
    }

    public Long getCityCorrectionsCount(FilterWrapper<CityCorrection> filterWrapper) {
        return sqlSession().selectOne(NS + ".selectCityCorrectionsCount", filterWrapper);
    }


    public boolean save(CityCorrection cityCorrection) {
        if (cityCorrection.getId() == null
                && isCityObjectExists(cityCorrection.getCorrection(), cityCorrection.getObjectId())){
            return false;
        }

        super.save(cityCorrection);

        return true;
    }

    public List<Long> getCityIds(String city) {
        return getObjectIds("city", city, CityStrategy.NAME);
    }

    public boolean isCityObjectExists(String city, Long objectId){
        return getCityIds(city).contains(objectId);
    }

     /* DISTRICT */

    public DistrictCorrection getDistrictCorrection(Long id) {
        return sqlSession().selectOne(NS + ".selectDistrictCorrection", id);
    }

    public List<DistrictCorrection> getDistrictCorrections(FilterWrapper<DistrictCorrection> filterWrapper) {
        return sqlSession().selectList(NS + ".selectDistrictCorrections", filterWrapper);
    }

    public List<DistrictCorrection> getDistrictCorrections(Long cityObjectId, String externalId, Long objectId, String correction,
                                                           Long organizationId, Long userOrganizationId){
        return getDistrictCorrections(FilterWrapper.of(new DistrictCorrection(cityObjectId, externalId, objectId,
                correction, organizationId, userOrganizationId, null)));
    }

    public Long getDistrictCorrectionsCount(FilterWrapper<DistrictCorrection> filterWrapper){
        return sqlSession().selectOne(NS + ".selectDistrictCorrectionsCount", filterWrapper);
    }


    public boolean save(DistrictCorrection districtCorrection){
        if (districtCorrection.getId() == null) {
            if (!isDistrictObjectExists(districtCorrection.getCorrection(), districtCorrection.getObjectId())) {
                sqlSession().insert(NS + ".insertDistrictCorrection", districtCorrection);
            }else {
                return false;
            }
        } else{
            sqlSession().update(NS + ".updateDistrictCorrection", districtCorrection);
        }

        return true;
    }

    public List<Long> getDistrictObjectIds(String district) {
        return getObjectIds("district", district, DistrictStrategy.NAME);
    }

    public boolean isDistrictObjectExists(String district, Long objectId){
        return getDistrictObjectIds(district).contains(objectId);
    }

    /* STREET TYPE */

    public StreetTypeCorrection getStreetTypeCorrection(Long id) {
        return sqlSession().selectOne(NS + ".selectStreetTypeCorrection", id);
    }

    public List<StreetTypeCorrection> getStreetTypeCorrections(FilterWrapper<StreetTypeCorrection> filterWrapper) {
        return sqlSession().selectList(NS + ".selectStreetTypeCorrections", filterWrapper);
    }

    public List<StreetTypeCorrection> getStreetTypeCorrections(Long objectId, String correction, Long organizationId, Long userOrganizationId) {
        return getStreetTypeCorrections(FilterWrapper.of(new StreetTypeCorrection(null, objectId, correction, organizationId,
                userOrganizationId, null)));
    }

    public List<StreetTypeCorrection> getStreetTypeCorrections(String correction, Long organizationId, Long userOrganizationId) {
        return getStreetTypeCorrections(null, correction, organizationId, userOrganizationId);
    }

    public List<StreetTypeCorrection> getStreetTypeCorrections(ExternalAddress address) {
        return getStreetTypeCorrections(address.getStreetType(), address.getOrganizationId(), address.getUserOrganizationId());
    }

    public Long getStreetTypeCorrectionsCount(FilterWrapper<StreetTypeCorrection> filterWrapper) {
        return sqlSession().selectOne(NS + ".selectStreetTypeCorrectionsCount", filterWrapper);
    }

    public boolean save(StreetTypeCorrection streetTypeCorrection) {
        if (streetTypeCorrection.getId() == null
                && isStreetTypeObjectExists(streetTypeCorrection.getCorrection(), streetTypeCorrection.getObjectId())) {
            return false;
        }

        super.save(streetTypeCorrection);

        return true;
    }

    public List<Long> getStreetTypeIds(String streetType) {
        Set<Long> set = new HashSet<>();

        set.addAll(getObjectIds("street_type", streetType, StreetTypeStrategy.SHORT_NAME));
        set.addAll(getObjectIds("street_type", streetType, StreetTypeStrategy.NAME));

        return new ArrayList<>(set);
    }

    public boolean isStreetTypeObjectExists(String streetType, Long objectId){
        return getStreetTypeIds(streetType).contains(objectId);
    }

    /* STREET */

    public StreetCorrection getStreetCorrection(Long id) {
        return sqlSession().selectOne(NS + ".selectStreetCorrection", id);
    }

    public List<StreetCorrection> getStreetCorrections(FilterWrapper<StreetCorrection> filterWrapper) {
        return sqlSession().selectList(NS + ".selectStreetCorrections", filterWrapper);
    }

    public Long getStreetCorrectionsCount(FilterWrapper<StreetCorrection> filterWrapper) {
        return sqlSession().selectOne(NS + ".selectStreetCorrectionsCount", filterWrapper);
    }

    public List<StreetCorrection> getStreetCorrections(Long cityId, Long streetTypeId, String externalId,
                                                       Long objectId,  String street, Long organizationId, Long userOrganizationId) {

        return getStreetCorrections(FilterWrapper.of(new StreetCorrection(cityId, streetTypeId, externalId,
                objectId, street, organizationId, userOrganizationId, null)));
    }

    public List<StreetCorrection> getStreetCorrections(LocalAddress localAddress, ExternalAddress externalAddress) {

        return getStreetCorrections(localAddress.getCityId(), localAddress.getStreetTypeId(), null, null,
                externalAddress.getStreet(), externalAddress.getOrganizationId(), externalAddress.getUserOrganizationId());
    }

    public void save(StreetCorrection streetCorrection) {
        if (streetCorrection.getId() == null) {
            sqlSession().insert(NS + ".insertStreetCorrection", streetCorrection);
        }else {
            sqlSession().update(NS + ".updateStreetCorrection", streetCorrection);
        }
    }

    public List<StreetCorrection> getStreetCorrectionsByBuilding(Long internalStreetId, Long internalBuildingId,
                                                                 Long organizationId) {
        return sqlSession().selectList(NS + ".selectStreetCorrectionsByBuilding",
                ImmutableMap.of("streetId", internalStreetId, "buildingId", internalBuildingId,
                        "calcCenterId", organizationId));
    }

    public boolean isStreetObjectExists(String street, Long objectId){
        return getObjectIds("street", street, StreetStrategy.NAME).contains(objectId);
    }

    /* BUILDING */

    public BuildingCorrection getBuildingCorrection(Long id) {
        return sqlSession().selectOne(NS + ".selectBuildingCorrection", id);
    }

    public List<BuildingCorrection> getBuildingCorrections(FilterWrapper<BuildingCorrection> filterWrapper) {
        return sqlSession().selectList(NS + ".selectBuildingCorrections", filterWrapper);
    }

    public Long getBuildingCorrectionsCount(FilterWrapper<BuildingCorrection> filterWrapper) {
        return sqlSession().selectOne(NS + ".selectBuildingCorrectionsCount", filterWrapper);
    }

    public List<BuildingCorrection> getBuildingCorrections(Long streetId, Long objectId, String buildingNumber,
                                                           String buildingCorp, Long organizationId, Long userOrganizationId) {
        return getBuildingCorrections(FilterWrapper.of(new BuildingCorrection(streetId, null, objectId,
                buildingNumber, buildingCorp, organizationId, userOrganizationId, null)));
    }

    public List<BuildingCorrection> getBuildingCorrections(LocalAddress localAddress, ExternalAddress externalAddress){
        return getBuildingCorrections(localAddress.getStreetId(), null, externalAddress.getBuildingNumber(),
                externalAddress.getBuildingCorp(), externalAddress.getOrganizationId(), externalAddress.getUserOrganizationId());
    }


    public boolean save(BuildingCorrection buildingCorrection){
        if (buildingCorrection.getCorrectionCorp() == null) {
            buildingCorrection.setCorrectionCorp("");
        }

        if (buildingCorrection.getId() == null) {
            if (!isBuildingObjectExists(buildingCorrection.getCorrection(),
                    buildingCorrection.getCorrectionCorp(), buildingCorrection.getObjectId())) {
                sqlSession().insert(NS + ".insertBuildingCorrection", buildingCorrection);
            }else {
                return false;
            }
        }else {
            sqlSession().update(NS + ".updateBuildingCorrection", buildingCorrection);
        }

        return true;
    }

    public boolean isBuildingObjectExists(String buildingNumber, String buildingCorp, Long objectId){
        return sqlSession().selectOne(NS + ".selectBuildingObjectExists",
                ImmutableMap.of("buildingNumber", buildingNumber, "buildingCorp", buildingCorp, "objectId", objectId));
    }
    
     /* APARTMENT */

    public ApartmentCorrection getApartmentCorrection(Long id) {
        return sqlSession().selectOne(NS + ".selectApartmentCorrection", id);
    }

    public List<ApartmentCorrection> getApartmentCorrections(FilterWrapper<ApartmentCorrection> filterWrapper) {
        return sqlSession().selectList(NS + ".selectApartmentCorrections", filterWrapper);
    }

    public List<ApartmentCorrection> getApartmentCorrections(Long buildingId, String externalId, Long objectId, String correction,
                                                           Long organizationId, Long userOrganizationId){
        return getApartmentCorrections(FilterWrapper.of(new ApartmentCorrection(buildingId, externalId, objectId,
                correction, organizationId, userOrganizationId, null)));
    }

    public Long getApartmentCorrectionsCount(FilterWrapper<ApartmentCorrection> filterWrapper){
        return sqlSession().selectOne(NS + ".selectApartmentCorrectionsCount", filterWrapper);
    }


    public boolean save(ApartmentCorrection apartmentCorrection){
        if (apartmentCorrection.getId() == null
                && isApartmentObjectExists(apartmentCorrection.getCorrection(), apartmentCorrection.getObjectId())) {
            return false;
        }

        super.save(apartmentCorrection);

        return true;
    }

    public List<Long> getApartmentObjectIds(String apartment) {
        return getObjectIds("apartment", apartment, ApartmentStrategy.NAME);
    }

    public boolean isApartmentObjectExists(String apartment, Long objectId){
        return getApartmentObjectIds(apartment).contains(objectId);
    }
    
    /* ROOM */

    public RoomCorrection getRoomCorrection(Long id) {
        return sqlSession().selectOne(NS + ".selectRoomCorrection", id);
    }

    public List<RoomCorrection> getRoomCorrections(FilterWrapper<RoomCorrection> filterWrapper) {
        return sqlSession().selectList(NS + ".selectRoomCorrections", filterWrapper);
    }

    public List<RoomCorrection> getRoomCorrections(Long buildingId, Long apartmentId, String externalId,
                                                   Long objectId, String correction,
                                                   Long organizationId, Long userOrganizationId){
        return getRoomCorrections(FilterWrapper.of(new RoomCorrection(buildingId, apartmentId, externalId, objectId,
                correction, organizationId, userOrganizationId, null)));
    }

    public Long getRoomCorrectionsCount(FilterWrapper<RoomCorrection> filterWrapper) {
        return sqlSession().selectOne(NS + ".selectRoomCorrectionsCount", filterWrapper);
    }


    public boolean save(RoomCorrection roomCorrection) {
        if (roomCorrection.getId() == null
                && isRoomObjectExists(roomCorrection.getCorrection(), roomCorrection.getObjectId())) {
            return false;
        }

        super.save(roomCorrection);

        return true;
    }

    public List<Long> getRoomObjectIds(String room) {
        return getObjectIds("room", room, RoomStrategy.NAME);
    }

    public boolean isRoomObjectExists(String room, Long objectId) {
        return getRoomObjectIds(room).contains(objectId);
    }
}
