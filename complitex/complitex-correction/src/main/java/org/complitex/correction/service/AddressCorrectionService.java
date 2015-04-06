package org.complitex.correction.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.wicket.util.string.Strings;
import org.complitex.address.entity.ExternalAddress;
import org.complitex.address.entity.LocalAddress;
import org.complitex.address.strategy.building.BuildingStrategy;
import org.complitex.address.strategy.building.entity.Building;
import org.complitex.address.strategy.city.CityStrategy;
import org.complitex.address.strategy.district.DistrictStrategy;
import org.complitex.address.strategy.street.StreetStrategy;
import org.complitex.address.strategy.street_type.StreetTypeStrategy;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.strategy.organization.IOrganizationStrategy;
import org.complitex.correction.entity.*;
import org.complitex.correction.exception.ResolveAddressException;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.List;
import java.util.Set;

/**
 * @author inheaven on 002 02.04.15 16:28
 */
@Stateless
public class AddressCorrectionService {

    @EJB
    private AddressCorrectionBean addressCorrectionBean;

    @EJB
    private StreetStrategy streetStrategy;

    @EJB
    private BuildingStrategy buildingStrategy;

    @EJB
    private CityStrategy cityStrategy;

    @EJB(lookup = IOrganizationStrategy.BEAN_LOOKUP)
    private IOrganizationStrategy organizationStrategy;

    @EJB
    private DistrictStrategy districtStrategy;

    @EJB
    private StreetTypeStrategy streetTypeStrategy;

    public LocalAddress resolveLocalAddress(String city, String streetType, String street, String buildingNumber,
                                            String buildingCorp, Long organizationId, Long userOrganizationId)
            throws ResolveAddressException {
        LocalAddress localAddress = new LocalAddress();

        //Связывание города
        List<CityCorrection> cityCorrections = addressCorrectionBean.getCityCorrections(city, organizationId, userOrganizationId);

        if (cityCorrections.size() == 1) {
            CityCorrection cityCorrection = cityCorrections.get(0);
            localAddress.setCityObjectId(cityCorrection.getObjectId());
        } else if (cityCorrections.size() > 1) {

            throw new ResolveAddressException("MORE_ONE_LOCAL_CITY_CORRECTION");
        } else {
            List<Long> cityIds = addressCorrectionBean.getCityObjectIds(city);

            if (cityIds.size() == 1) {
                localAddress.setCityObjectId(cityIds.get(0));
            } else if (cityIds.size() > 1) {

                throw new ResolveAddressException("MORE_ONE_LOCAL_CITY");
            } else {

                return localAddress;
            }
        }

        //Связывание типа улицы
        if(streetType != null){
            List<StreetTypeCorrection> streetTypeCorrections = addressCorrectionBean.getStreetTypeCorrections(streetType,
                    organizationId, userOrganizationId);

            if (streetTypeCorrections.size() == 1) {
                localAddress.setStreetTypeObjectId(streetTypeCorrections.get(0).getObjectId());
            } else if (streetTypeCorrections.size() > 1) {

                throw new ResolveAddressException("MORE_ONE_LOCAL_STREET_TYPE_CORRECTION");
            } else {
                List<Long> streetTypeIds = addressCorrectionBean.getStreetTypeObjectIds(streetType);

                if (streetTypeIds.size() == 1) {
                    localAddress.setStreetTypeObjectId(streetTypeIds.get(0));
                } else if (streetTypeIds.size() > 1) {

                    throw new ResolveAddressException("MORE_ONE_LOCAL_STREET_TYPE");
                } else {

                    return localAddress;
                }
            }
        }

        //Связывание улицы
        List<StreetCorrection> streetCorrections = addressCorrectionBean.getStreetCorrections(localAddress.getCityObjectId(),
                localAddress.getStreetTypeObjectId(), null, null, street, organizationId, userOrganizationId);

        if (streetCorrections.size() == 1){
            StreetCorrection streetCorrection = streetCorrections.get(0);

            localAddress.setCityObjectId(streetCorrection.getCityObjectId());
            localAddress.setStreetTypeObjectId(streetCorrection.getStreetTypeObjectId());
            localAddress.setStreetObjectId(streetCorrection.getObjectId());
        }else if (streetCorrections.size() > 1) {
            //сформируем множество названий
            Set<String> streetNames = Sets.newHashSet();

            for (StreetCorrection sc : streetCorrections) {
                String streetName = streetStrategy.getName(sc.getObjectId());

                if (!Strings.isEmpty(streetName)) {
                    streetNames.add(streetName);
                }
            }

            if (streetNames.size() == 1) { //нашли внутренее название улицы
                String streetName = Lists.newArrayList(streetNames).get(0);

                //находим ids улиц по внутреннему названию
                List<Long> streetIds = streetStrategy.getStreetObjectIds(localAddress.getCityObjectId(),
                        localAddress.getStreetTypeObjectId(), streetName);

                if (streetIds.size() == 1) { //нашли ровно одну улицу
                    Long streetObjectId = streetIds.get(0);
                    localAddress.setStreetObjectId(streetObjectId);

                    DomainObject streetObject = streetStrategy.getDomainObject(streetObjectId, true);
                    localAddress.setStreetTypeObjectId(streetStrategy.getStreetType(streetObject));

                    //перейти к обработке дома
                } else if (streetIds.size() > 1) { // нашли больше одной улицы
                    //пытаемся найти по району
                    streetIds = streetStrategy.getStreetObjectIdsByDistrict(localAddress.getCityObjectId(),
                            street, organizationId);

                    if (streetIds.size() == 1) { //нашли ровно одну улицу по району
                        Long streetObjectId = streetIds.get(0);
                        localAddress.setStreetObjectId(streetObjectId);


                        DomainObject streetObject = streetStrategy.getDomainObject(streetObjectId, true);
                        localAddress.setStreetTypeObjectId(streetStrategy.getStreetType(streetObject));

                        //перейти к обработке дома
                    } else {
                        // пытаемся искать дополнительно по номеру и корпусу дома
                        streetIds = streetStrategy.getStreetObjectIdsByBuilding(localAddress.getCityObjectId(), streetName,
                                buildingNumber, buildingCorp);

                        if (streetIds.size() == 1) { //нашли ровно одну улицу с заданным номером и корпусом дома
                            Long streetObjectId = streetIds.get(0);
                            localAddress.setStreetObjectId(streetObjectId);

                            DomainObject streetObject = streetStrategy.getDomainObject(streetObjectId, true);
                            localAddress.setStreetTypeObjectId(streetStrategy.getStreetType(streetObject));

                            //проставить дом для payment и выйти
                            List<Long> buildingIds = buildingStrategy.getBuildingObjectIds(localAddress.getCityObjectId(),
                                    streetObjectId, buildingNumber, buildingCorp);

                            if (buildingIds.size() == 1) {
                                localAddress.setBuildingObjectId(buildingIds.get(0));
                            } else {

                                throw new ResolveAddressException("BUILDING_ID_NOT_FOUND");
                            }

                            return localAddress;
                        } else { // по доп. информации, состоящей из номера и корпуса дома, не смогли однозначно определить улицу

                            throw new ResolveAddressException("STREET_AND_BUILDING_UNRESOLVED_LOCALLY");
                        }
                    }
                } else {
                    throw new ResolveAddressException("Street name `" + streetName + "` was not found.");
                }
            } else {
                throw new ResolveAddressException("Street `" + street + "` is mapped to more one internal street objects: "
                        + streetNames);
            }
        } else { // в коррекциях не нашли ни одного соответствия на внутренние объекты улиц
            // ищем по внутреннему справочнику улиц
            List<Long> streetIds = streetStrategy.getStreetObjectIds(localAddress.getCityObjectId(),
                    localAddress.getStreetTypeObjectId(), street);

            if (streetIds.size() == 1) { // нашли ровно одну улицу
                Long streetId = streetIds.get(0);
                localAddress.setStreetObjectId(streetId);

                DomainObject streetObject = streetStrategy.getDomainObject(streetId, true);
                localAddress.setStreetTypeObjectId(streetStrategy.getStreetType(streetObject));

                // перейти к обработке дома
            } else if (streetIds.size() > 1) { // нашли более одной улицы
                //пытаемся найти по району
                streetIds = streetStrategy.getStreetObjectIdsByDistrict(localAddress.getCityObjectId(), street, organizationId);

                if (streetIds.size() == 1) { //нашли ровно одну улицу по району
                    Long streetId = streetIds.get(0);
                    localAddress.setStreetObjectId(streetId);

                    DomainObject streetObject = streetStrategy.getDomainObject(streetId, true);
                    localAddress.setStreetTypeObjectId(streetStrategy.getStreetType(streetObject));
                    // перейти к обработке дома
                } else {
                    // пытаемся искать дополнительно по номеру и корпусу дома
                    streetIds = streetStrategy.getStreetObjectIdsByBuilding(localAddress.getCityObjectId(), street,
                            buildingNumber, buildingCorp);

                    if (streetIds.size() == 1) {
                        Long streetId = streetIds.get(0);

                        //проставить дом для payment и выйти
                        List<Long> buildingIds = buildingStrategy.getBuildingObjectIds(localAddress.getCityObjectId(),
                                streetId, buildingNumber, buildingCorp);

                        if (buildingIds.size() == 1) {
                            localAddress.setBuildingObjectId(buildingIds.get(0));

                            localAddress.setStreetObjectId(streetId);
                        } else {

                            throw new ResolveAddressException("BUILDING_ID_NOT_FOUND");
                        }

                        return localAddress;
                    } else { // по доп. информации, состоящей из номера и корпуса дома, не смогли однозначно определить улицу
                        return localAddress;
                    }
                }
            } else { // не нашли ни одной улицы
                return localAddress;
            }
        }

        //Связывание дома
        List<BuildingCorrection> buildingCorrections = addressCorrectionBean.getBuildingCorrections(
                localAddress.getStreetObjectId(), buildingNumber, buildingCorp, organizationId, userOrganizationId);

        if (buildingCorrections.size() == 1) {
            localAddress.setBuildingObjectId(buildingCorrections.get(0).getObjectId());
        } else if (buildingCorrections.size() > 1) {

            throw new ResolveAddressException("MORE_ONE_LOCAL_BUILDING_CORRECTION");
        } else {
            List<Long> buildingIds = buildingStrategy.getBuildingObjectIds(localAddress.getCityObjectId(),
                    localAddress.getStreetObjectId(), buildingNumber, buildingCorp);

            if (buildingIds.size() == 1){
                localAddress.setBuildingObjectId(buildingIds.get(0));
            }else if (buildingIds.size() > 1) {

                throw new ResolveAddressException("MORE_ONE_LOCAL_BUILDING");
            }
        }

        return localAddress;
    }

    public ExternalAddress resolveExternalAddress(LocalAddress localAddress, Long organizationId, Long userOrganizationId)
            throws ResolveAddressException {
        ExternalAddress externalAddress = new ExternalAddress();

        //город
        List<CityCorrection> cityCorrections = addressCorrectionBean.getCityCorrections(localAddress.getCityObjectId(),
                null, organizationId, userOrganizationId);

        if (cityCorrections.isEmpty()){
            DomainObject city = cityStrategy.getDomainObject(localAddress.getCityObjectId(), true);

            if (city != null){
                externalAddress.setCity(cityStrategy.getName(city));
            }else {

                return externalAddress;
            }
        } else if (cityCorrections.size() == 1) {
            externalAddress.setCity(cityCorrections.get(0).getCorrection());
        } else {

            throw new ResolveAddressException("MORE_ONE_EXTERNAL_CITY_CORRECTION");
        }

        // район
        List<DistrictCorrection> districtCorrections = addressCorrectionBean.getDistrictCorrections(
                localAddress.getCityObjectId(), null, null, null, organizationId, userOrganizationId);

        if (districtCorrections.isEmpty() && localAddress.getOrganizationId() != null){
            DomainObject organization = organizationStrategy.getDomainObject(localAddress.getOrganizationId(), true);

            Long districtId = organization.getAttribute(IOrganizationStrategy.DISTRICT).getValueId();
            DomainObject district = districtStrategy.getDomainObject(districtId, true);

            if (district != null){
                externalAddress.setDistrict(districtStrategy.getName(district));
            }else {

                return externalAddress;
            }

        } else if (districtCorrections.size() == 1) {
            externalAddress.setDistrict(districtCorrections.get(0).getCorrection());
        } else if (districtCorrections.size() > 1) {

            throw new ResolveAddressException("MORE_ONE_EXTERNAL_DISTRICT_CORRECTION");
        }

        //тип улицы
        if (localAddress.getStreetTypeObjectId() != null) {
            List<StreetTypeCorrection> streetTypeCorrections = addressCorrectionBean.getStreetTypeCorrections(
                    localAddress.getStreetTypeObjectId(), null, organizationId, userOrganizationId);

            if (streetTypeCorrections.isEmpty()){
                DomainObject streetType = streetTypeStrategy.getDomainObject(localAddress.getStreetTypeObjectId(), true);

                if (streetType != null){
                    externalAddress.setStreetType(streetTypeStrategy.getShortName(streetType));
                }else{

                    return externalAddress;
                }
            }else if (streetTypeCorrections.size() == 1){
                externalAddress.setStreetType(streetTypeCorrections.get(0).getCorrection());
            }else {

                throw new ResolveAddressException("MORE_ONE_EXTERNAL_STREET_TYPE_CORRECTION");
            }
        }

        //улица
        List<StreetCorrection> streetCorrections = addressCorrectionBean.getStreetCorrections(
                null, localAddress.getStreetObjectId(), null, null, null, organizationId, userOrganizationId);

        if (streetCorrections.isEmpty()){
            DomainObject street = streetStrategy.getDomainObject(localAddress.getStreetObjectId(), true);

            if (street != null){
                externalAddress.setStreet(streetStrategy.getName(street));
            }else {

                return externalAddress;
            }
        } else if (streetCorrections.size() == 1) {
            externalAddress.setStreet(streetCorrections.get(0).getCorrection());
        } else {
            streetCorrections = addressCorrectionBean.getStreetCorrectionsByBuilding(localAddress.getStreetObjectId(),
                    localAddress.getBuildingObjectId(), organizationId);

            if (streetCorrections.size() == 1) {
                externalAddress.setStreet(streetCorrections.get(0).getCorrection());
            } else {

                throw new ResolveAddressException("MORE_ONE_EXTERNAL_STREET_CORRECTION");
            }
        }

        //дом
        List<BuildingCorrection> buildingCorrections = addressCorrectionBean.getBuildingCorrections(null,
                localAddress.getBuildingObjectId(), null, null, organizationId, null);

        if (buildingCorrections.isEmpty()){
            Building building = buildingStrategy.getDomainObject(localAddress.getBuildingObjectId(), true);

            if (building != null){
                externalAddress.setBuildingNumber(building.getAccompaniedNumber());
                externalAddress.setBuildingCorp(building.getAccompaniedCorp());
            }else {

                return externalAddress;
            }
        }else  if(buildingCorrections.size() == 1) {
            BuildingCorrection buildingCorrection = buildingCorrections.get(0);
            externalAddress.setBuildingNumber(buildingCorrection.getCorrection());
            externalAddress.setBuildingCorp(buildingCorrection.getCorrectionCorp());
        } else if (buildingCorrections.size() > 1) {

            throw new ResolveAddressException("MORE_ONE_REMOTE_BUILDING_CORRECTION");
        }

        return externalAddress;
    }
}
