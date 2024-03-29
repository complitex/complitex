package ru.complitex.correction.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.util.string.Strings;
import ru.complitex.address.entity.AddressEntity;
import ru.complitex.address.strategy.apartment.ApartmentStrategy;
import ru.complitex.address.strategy.building.BuildingStrategy;
import ru.complitex.address.strategy.city.CityStrategy;
import ru.complitex.address.strategy.district.DistrictStrategy;
import ru.complitex.address.strategy.room.RoomStrategy;
import ru.complitex.address.strategy.street.StreetStrategy;
import ru.complitex.address.strategy.street_type.StreetTypeStrategy;
import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.service.AbstractBean;
import ru.complitex.common.strategy.StringLocaleBean;
import ru.complitex.correction.entity.AddressLinkData;
import ru.complitex.correction.entity.AddressLinkStatus;
import ru.complitex.correction.entity.Correction;
import ru.complitex.correction.service.exception.DuplicateCorrectionException;
import ru.complitex.correction.service.exception.MoreOneCorrectionException;
import ru.complitex.correction.service.exception.NotFoundCorrectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.List;
import java.util.Set;

/**
 * @author Pavel Sknar
 */
@Stateless(name = "AddressService")
public class AddressService extends AbstractBean {
    private final Logger log = LoggerFactory.getLogger(AddressService.class);

    public final static Long MODULE_ID = 0L;

    @EJB
    private CorrectionBean correctionBean;

    @EJB
    private StringLocaleBean stringLocaleBean;

    @EJB
    private CityStrategy cityStrategy;

    @EJB
    private DistrictStrategy districtStrategy;

    @EJB
    private StreetTypeStrategy streetTypeStrategy;

    @EJB
    private StreetStrategy streetStrategy;

    @EJB
    private BuildingStrategy buildingStrategy;

    @EJB
    private ApartmentStrategy apartmentStrategy;

    @EJB
    private RoomStrategy roomStrategy;

    /**
     * Разрешить переход "ОСЗН адрес -> локальная адресная база"
     * Алгоритм:
     * Сначала пытаемся поискать город в таблице коррекций по названию города, пришедшего из ОСЗН и id ОСЗН.
     * Если не успешно, то пытаемся поискать по локальной адресной базе.
     * Если успешно, то записать коррекцию в таблицу коррекций.
     * Если город в итоге нашли, то проставляем его в internalCityId, иначе проставляем статус AddressLinkStatus.CITY_UNRESOLVED
     * и выходим, т.к. без города искать далее не имеет смысла.
     * Улицы ищем только в локальной адресной базе. Причем сначала ищем только по названию улицы. Если нашли ровно одну, т.е. существует только
     * один тип улицы для улицы с таким названием, то поиск успешен, по id улицы узнаем тип улицы, по id типа улицы находим(или создаем
     * если ничего не нашли) коррекцию для типа улицы, находим (или создаем) коррекцию для улицы. Далее обрабатываем дом по схеме аналогичной
     * схеме обработки города.
     * Если по названию улицы ничего не нашли, то проставляем статус AddressLinkStatus.STREET_UNRESOLVED и выходим.
     * Если же нашли более одной, то пытаемся поискать по названию улицы, номеру дома и корпуса(если есть). Если нашли ровно одну улицу, то
     * проставляем в payment id улицы и дома и выходим не создаваю никаких коррекций. Если не нашли ничего или более одной, то проставляем
     * статус AddressLinkStatus.STREET_UNRESOLVED и выходим.
     * Замечание: статус AddressLinkStatus.STREET_UNRESOLVED не позволяет корректировать улицы для payment, см.
     *      AddressLinkStatus.isAddressCorrectableForPayment().
     *
     * Это алгоритм применяется и к поиску домов и с незначительными поправками к поиску улиц.
     */
    public void resolveAddress(AddressLinkData data, Long organizationId, Long userOrganizationId){

        //Связывание города
        List<Correction> cityCorrections = correctionBean.getCorrections(AddressEntity.CITY,
                data.getCity(), organizationId, userOrganizationId);

        if (cityCorrections.size() == 1) {
            Correction cityCorrection = cityCorrections.get(0);
            data.setCityId(cityCorrection.getObjectId());
        } else if (cityCorrections.size() > 1) {
            data.setStatus(AddressLinkStatus.MORE_ONE_CITY_CORRECTION);

            return;
        } else {
            List<Long> cityIds = correctionBean.getObjectIds(AddressEntity.CITY, data.getCity(), CityStrategy.NAME);

            if (cityIds.size() == 1) {
                data.setCityId(cityIds.get(0));
            } else if (cityIds.size() > 1) {
                data.setStatus(AddressLinkStatus.MORE_ONE_CITY);

                return;
            } else {
                data.setStatus(AddressLinkStatus.CITY_UNRESOLVED);

                return;
            }
        }

        //Связывание типа улицы
        if(data.getStreetType() != null){
            List<Correction> streetTypeCorrections = correctionBean.getCorrections(AddressEntity.STREET_TYPE,
                    data.getStreetType(), organizationId, userOrganizationId);

            if (streetTypeCorrections.size() == 1) {
                data.setStreetTypeId(streetTypeCorrections.get(0).getObjectId());
            } else if (streetTypeCorrections.size() > 1) {
                data.setStatus(AddressLinkStatus.MORE_ONE_STREET_TYPE_CORRECTION);

                return;
            } else {
                List<Long> streetTypeIds = correctionBean.getObjectIds(AddressEntity.STREET_TYPE,
                        data.getStreetType(), StreetTypeStrategy.NAME);

                if (streetTypeIds.size() == 1) {
                    data.setStreetTypeId(streetTypeIds.get(0));
                } else if (streetTypeIds.size() > 1) {
                    data.setStatus(AddressLinkStatus.MORE_ONE_STREET_TYPE);

                    return;
                } else {
                    data.setStatus(AddressLinkStatus.STREET_TYPE_UNRESOLVED);

                    return;
                }
            }
        }

        //Связывание улицы
        List<Correction> streetCorrections = correctionBean.getCorrections(AddressEntity.STREET,
                data.getCityId(), data.getStreetTypeId(), data.getStreet(), organizationId, userOrganizationId);

        if (streetCorrections.size() == 1){
            Correction streetCorrection = streetCorrections.get(0);

            data.setCityId(streetCorrection.getParentId());
            data.setStreetTypeId(streetCorrection.getAdditionalParentId());
            data.setStreetId(streetCorrection.getObjectId());
        }else if (streetCorrections.size() > 1) {
            //сформируем множество названий
            Set<String> streetNames = Sets.newHashSet();

            for (Correction sc : streetCorrections) {
                String streetName = streetStrategy.getName(sc.getObjectId());

                if (!Strings.isEmpty(streetName)) {
                    streetNames.add(streetName);
                }
            }

            if (streetNames.size() == 1) { //нашли внутренее название улицы
                String streetName = Lists.newArrayList(streetNames).get(0);

                //находим ids улиц по внутреннему названию
                List<Long> streetIds = streetStrategy.getStreetIds(data.getCityId(),
                        data.getStreetTypeId(), streetName);

                if (streetIds.size() == 1) { //нашли ровно одну улицу
                    Long streetObjectId = streetIds.get(0);
                    data.setStreetId(streetObjectId);

                    DomainObject streetObject = streetStrategy.getDomainObject(streetObjectId, true);
                    data.setStreetTypeId(streetStrategy.getStreetType(streetObject));

                    //перейти к обработке дома
                } else if (streetIds.size() > 1) { // нашли больше одной улицы
                    //пытаемся найти по району
                    streetIds = streetStrategy.getStreetIdsByDistrict(data.getCityId(),
                            data.getStreet(), organizationId);

                    if (streetIds.size() == 1) { //нашли ровно одну улицу по району
                        Long streetObjectId = streetIds.get(0);
                        data.setStreetId(streetObjectId);


                        DomainObject streetObject = streetStrategy.getDomainObject(streetObjectId, true);
                        data.setStreetTypeId(streetStrategy.getStreetType(streetObject));

                        //перейти к обработке дома
                    } else {
                        // пытаемся искать дополнительно по номеру и корпусу дома
                        streetIds = streetStrategy.getStreetObjectIdsByBuilding(data.getCityId(), streetName,
                                data.getBuildingNumber(), data.getBuildingCorp());

                        if (streetIds.size() == 1) { //нашли ровно одну улицу с заданным номером и корпусом дома
                            Long streetObjectId = streetIds.get(0);
                            data.setStreetId(streetObjectId);

                            DomainObject streetObject = streetStrategy.getDomainObject(streetObjectId, true);
                            data.setStreetTypeId(streetStrategy.getStreetType(streetObject));

                            //проставить дом для payment и выйти
                            List<Long> buildingIds = buildingStrategy.getBuildingObjectIds(data.getCityId(),
                                    streetObjectId,data.getBuildingNumber(),data.getBuildingCorp());

                            if (buildingIds.size() == 1) {
                                data.setBuildingId(buildingIds.get(0));
                            } else {
                                throw new IllegalStateException("Building id was not found.");
                            }
                            data.setStatus(AddressLinkStatus.ADDRESS_LINKED);

                            return;
                        } else { // по доп. информации, состоящей из номера и корпуса дома, не смогли однозначно определить улицу
                            data.setStatus(AddressLinkStatus.STREET_AND_BUILDING_UNRESOLVED);
                            return;
                        }
                    }
                } else {
                    throw new IllegalStateException("Street name `" + streetName + "` was not found.");
                }
            } else {
                throw new IllegalStateException("Street `" + data.getStreet() +
                        "` is mapped to more one internal street objects: " + streetNames);
            }
        } else { // в коррекциях не нашли ни одного соответствия на внутренние объекты улиц
            // ищем по внутреннему справочнику улиц
            List<Long> streetIds = streetStrategy.getStreetIds(data.getCityId(),
                    data.getStreetTypeId(), data.getStreet());

            if (streetIds.size() == 1) { // нашли ровно одну улицу
                Long streetId = streetIds.get(0);
                data.setStreetId(streetId);

                DomainObject streetObject = streetStrategy.getDomainObject(streetId, true);
                data.setStreetTypeId(streetStrategy.getStreetType(streetObject));

                // перейти к обработке дома
            } else if (streetIds.size() > 1) { // нашли более одной улицы
                //пытаемся найти по району
                streetIds = streetStrategy.getStreetIdsByDistrict(data.getCityId(), data.getStreet(), organizationId);

                if (streetIds.size() == 1) { //нашли ровно одну улицу по району
                    Long streetId = streetIds.get(0);
                    data.setStreetId(streetId);

                    DomainObject streetObject = streetStrategy.getDomainObject(streetId, true);
                    data.setStreetTypeId(streetStrategy.getStreetType(streetObject));
                    // перейти к обработке дома
                } else {
                    // пытаемся искать дополнительно по номеру и корпусу дома
                    streetIds = streetStrategy.getStreetObjectIdsByBuilding(data.getCityId(), data.getStreet(),
                            data.getBuildingNumber(), data.getBuildingCorp());

                    if (streetIds.size() == 1) {
                        Long streetId = streetIds.get(0);

                        //проставить дом для payment и выйти
                        List<Long> buildingIds = buildingStrategy.getBuildingObjectIds(data.getCityId(), streetId,
                                data.getBuildingNumber(), data.getBuildingCorp());

                        if (buildingIds.size() == 1) {
                            data.setBuildingId(buildingIds.get(0));

                            data.setStreetId(streetId);
                        } else {
                            throw new IllegalStateException("Building id was not found.");
                        }
                        data.setStatus(AddressLinkStatus.ADDRESS_LINKED);
                        return;
                    } else { // по доп. информации, состоящей из номера и корпуса дома, не смогли однозначно определить улицу
                        data.setStatus(AddressLinkStatus.STREET_AND_BUILDING_UNRESOLVED);
                        return;
                    }
                }
            } else { // не нашли ни одной улицы
                data.setStatus(AddressLinkStatus.STREET_UNRESOLVED);
                return;
            }
        }

        //Связывание дома
        List<Correction> buildingCorrections = correctionBean.getCorrections(AddressEntity.BUILDING,
                data.getStreetId(),  data.getBuildingNumber(), data.getBuildingCorp(), organizationId, userOrganizationId);

        if (buildingCorrections.size() == 1) {
            data.setBuildingId(buildingCorrections.get(0).getObjectId());
        } else if (buildingCorrections.size() > 1) {
            data.setStatus(AddressLinkStatus.MORE_ONE_BUILDING_CORRECTION);
        } else {
            List<Long> buildingIds = buildingStrategy.getBuildingObjectIds(data.getCityId(),
                    data.getStreetId(), data.getBuildingNumber(), data.getBuildingCorp());

            if (buildingIds.size() == 1){
                data.setBuildingId(buildingIds.get(0));
            } else if (buildingIds.size() > 1) {
                data.setStatus(AddressLinkStatus.MORE_ONE_BUILDING);
            } else {
                data.setStatus(AddressLinkStatus.BUILDING_UNRESOLVED);
            }
        }

        if (data.getBuildingId() == null) {
            return;
        }

        //Связывание квартиры
        if (StringUtils.isNotEmpty(data.getApartment())) {
            List<Correction> apartmentCorrections = correctionBean.getCorrections(AddressEntity.APARTMENT,
                    data.getBuildingId(), data.getApartment(), organizationId, userOrganizationId);
            if (apartmentCorrections.size() == 1) {
                data.setApartmentId(apartmentCorrections.get(0).getObjectId());
            } else if (apartmentCorrections.size() > 1) {
                data.setStatus(AddressLinkStatus.MORE_ONE_APARTMENT_CORRECTION);
            } else {
                List<Long> apartmentIds = apartmentStrategy.getApartmentObjectIds(data.getBuildingId(),
                        data.getApartment());

                if (apartmentIds.size() == 1){
                    data.setApartmentId(apartmentIds.get(0));
                } else if (apartmentIds.size() > 1) {
                    data.setStatus(AddressLinkStatus.MORE_ONE_APARTMENT);
                } else {
                    data.setStatus(AddressLinkStatus.APARTMENT_UNRESOLVED);
                }
            }

            if (data.getApartmentId() == null) {
                return;
            }
        }

        //Связывание комнаты
        if (StringUtils.isNotEmpty(data.getRoom())) {
            List<Correction> roomCorrections = correctionBean.getCorrections(AddressEntity.ROOM,
                    data.getBuildingId(), data.getApartmentId(), data.getRoom(), organizationId, userOrganizationId);
            if (roomCorrections.size() == 1) {
                data.setRoomId(roomCorrections.get(0).getObjectId());
            } else if (roomCorrections.size() > 1) {
                data.setStatus(AddressLinkStatus.MORE_ONE_ROOM_CORRECTION);
            } else {
                List<Long> roomIds = roomStrategy.getRoomObjectIds(data.getBuildingId(), data.getApartmentId(),
                        data.getRoom());

                if (roomIds.size() == 1) {
                    data.setRoomId(roomIds.get(0));
                } else if (roomIds.size() > 1) {
                    data.setStatus(AddressLinkStatus.MORE_ONE_ROOM);
                } else if (roomIds.isEmpty()){
                    data.setStatus(AddressLinkStatus.ROOM_UNRESOLVED);
                }
            }

            if (data.getRoomId() == null) {
                return;
            }
        }

        //Связанно с внутренней адресной базой
        data.setStatus(AddressLinkStatus.ADDRESS_LINKED);
    }

    /**
     * Корректирование адреса из UI.
     * Алгоритм:
     * Если у payment записи id города NULL и откорректированный город не NULL, то
     * вставить коррекцию для города в таблицу коррекций городов, коррекировать payment(PaymentBean.correctCity())
     * и benefit записи соответствующие данному payment(BenefitBean.addressCorrected()).
     *
     * Алгоритм аналогичен для других составляющих адреса.
     *
     * @param data AddressLinkData
     * @param cityObjectId Откорректированный город
     * @param streetObjectId Откорректированная улица
     * @param streetTypeObjectId Откорректированный тип улицы
     * @param buildingObjectId Откорректированный дом
     * @param apartmentObjectId Откорректированная квартира
     * @param roomObjectId Откорректированная квартира
     */

    public void correctAddress(AddressLinkData data, AddressEntity entity, Long cityObjectId,
                               Long streetTypeObjectId, Long streetObjectId, Long buildingObjectId,
                               Long apartmentObjectId, Long roomObjectId,
                               Long userOrganizationId, Long organizationId)
            throws DuplicateCorrectionException, MoreOneCorrectionException, NotFoundCorrectionException {

        switch (entity) {
            case CITY: {
                List<Correction> cityCorrections = correctionBean.getCorrections(AddressEntity.CITY,
                        data.getCity(), organizationId, userOrganizationId);

                if (cityCorrections.isEmpty()) {
                    Correction cityCorrection = new Correction(AddressEntity.CITY.getEntityName(), null,
                            cityObjectId, data.getCity().toUpperCase(), organizationId, userOrganizationId);
                    correctionBean.save(cityCorrection);
                } else {
                    throw new DuplicateCorrectionException();
                }
            }
            break;

            case STREET_TYPE: {
                List<Correction> streetTypeCorrections = correctionBean.getCorrections(AddressEntity.STREET,
                        null, data.getStreetType(), organizationId, userOrganizationId);

                if (streetTypeCorrections.isEmpty()) {
                    Correction streetTypeCorrection = new Correction(AddressEntity.STREET.getEntityName(),
                            null,
                            streetTypeObjectId,
                            data.getStreetType().toUpperCase(),
                            organizationId, userOrganizationId);
                    correctionBean.save(streetTypeCorrection);
                } else {
                    throw new DuplicateCorrectionException();
                }
            }
            break;

            case STREET:
                Long streetTypeId = data.getStreetTypeId() != null
                        ? data.getStreetTypeId() : streetTypeObjectId;

                List<Correction> streetCorrections = correctionBean.getCorrections(AddressEntity.STREET,
                        data.getCityId(), streetTypeId, data.getStreet(), organizationId, userOrganizationId);

                if (streetCorrections.isEmpty()) {
                    Correction streetCorrection = new Correction(AddressEntity.STREET.getEntityName(), data.getCityId(),
                            streetTypeId, streetObjectId, data.getStreet().toUpperCase(),
                            organizationId, userOrganizationId);

                    correctionBean.save(streetCorrection);
                } else {
                    throw new DuplicateCorrectionException();
                }

                break;

            case BUILDING:
                List<Correction> buildingCorrections = correctionBean.getCorrections(AddressEntity.BUILDING,
                        data.getStreetId(), data.getBuildingNumber(), data.getBuildingCorp(),
                        organizationId, userOrganizationId);

                if (buildingCorrections.isEmpty()) {
                    Correction buildingCorrection = new Correction(AddressEntity.BUILDING.getEntityName(),
                            data.getStreetId(), null,
                            buildingObjectId,
                            data.getBuildingNumber().toUpperCase(),
                            data.getBuildingCorp() != null ? data.getBuildingCorp().toUpperCase() : null,
                            organizationId, userOrganizationId);

                    correctionBean.save(buildingCorrection);
                } else {
                    throw new DuplicateCorrectionException();
                }

                break;

            case APARTMENT:
                List<Correction> apartmentCorrections = correctionBean.getCorrections(AddressEntity.APARTMENT,
                        data.getBuildingId(), apartmentObjectId, data.getApartment(),
                        organizationId, userOrganizationId);

                if (apartmentCorrections.isEmpty()) {
                    Correction apartmentCorrection = new Correction(AddressEntity.APARTMENT.getEntityName(),
                            data.getBuildingId(), null,
                            apartmentObjectId,
                            data.getApartment().toUpperCase(),
                            organizationId, userOrganizationId);

                    correctionBean.save(apartmentCorrection);
                } else {
                    throw new DuplicateCorrectionException();
                }

                break;
            
            case ROOM:
                List<Correction> roomCorrections = correctionBean.getCorrections(AddressEntity.ROOM,
                        data.getBuildingId(), data.getApartmentId(), data.getRoom(), organizationId, userOrganizationId);

                if (roomCorrections.isEmpty()) {
                    Correction roomCorrection = new Correction(AddressEntity.ROOM.getEntityName(), data.getBuildingId(),
                            data.getApartmentId(), null,
                            roomObjectId,
                            data.getRoom().toUpperCase(),
                            organizationId, userOrganizationId);

                    correctionBean.save(roomCorrection);
                } else {
                    throw new DuplicateCorrectionException();
                }

                break;
        }
    }
}
