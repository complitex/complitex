package org.complitex.osznconnection.file.service;

import com.google.common.collect.Sets;
import org.apache.wicket.util.string.Strings;
import org.complitex.address.entity.AddressEntity;
import org.complitex.address.strategy.building.BuildingStrategy;
import org.complitex.address.strategy.building.entity.Building;
import org.complitex.address.strategy.city.CityStrategy;
import org.complitex.address.strategy.district.DistrictStrategy;
import org.complitex.address.strategy.street.StreetStrategy;
import org.complitex.address.strategy.street_type.StreetTypeStrategy;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.service.AbstractBean;
import org.complitex.common.service.ModuleBean;
import org.complitex.common.strategy.StringLocaleBean;
import org.complitex.common.strategy.organization.IOrganizationStrategy;
import org.complitex.correction.entity.*;
import org.complitex.correction.service.AddressCorrectionBean;
import org.complitex.correction.service.exception.DuplicateCorrectionException;
import org.complitex.correction.service.exception.MoreOneCorrectionException;
import org.complitex.correction.service.exception.NotFoundCorrectionException;
import org.complitex.osznconnection.file.entity.AbstractAccountRequest;
import org.complitex.osznconnection.file.entity.AbstractAddressRequest;
import org.complitex.osznconnection.file.entity.RequestStatus;
import org.complitex.osznconnection.file.service.privilege.FacilityReferenceBookBean;
import org.complitex.osznconnection.file.service_provider.ServiceProviderAdapter;
import org.complitex.osznconnection.organization.strategy.OsznOrganizationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static org.complitex.address.util.AddressUtil.replaceApartmentSymbol;
import static org.complitex.address.util.AddressUtil.replaceBuildingCorpSymbol;
import static org.complitex.address.util.AddressUtil.replaceBuildingNumberSymbol;
import static org.complitex.common.util.StringUtil.removeWhiteSpaces;

@Stateless(name = "OsznAddressService")
public class AddressService extends AbstractBean {
    private final Logger log = LoggerFactory.getLogger(AddressService.class);

    @EJB
    private AddressCorrectionBean addressCorrectionBean;

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
    private ServiceProviderAdapter adapter;

    @EJB
    private FacilityReferenceBookBean facilityReferenceBookBean;

    @EJB
    private OsznOrganizationStrategy organizationStrategy;

    @EJB
    private ModuleBean moduleBean;

    /**
     * Разрешить переход "ОСЗН адрес -> локальная адресная база"
     * Алгоритм:
     * Сначала пытаемся поискать город в таблице коррекций по названию города, пришедшего из ОСЗН и id ОСЗН.
     * Если не успешно, то пытаемся поискать по локальной адресной базе.
     * Если успешно, то записать коррекцию в таблицу коррекций.
     * Если город в итоге нашли, то проставляем его в internalCityId, иначе проставляем статус RequestStatus.CITY_UNRESOLVED_LOCALLY
     * и выходим, т.к. без города искать далее не имеет смысла.
     * Улицы ищем только в локальной адресной базе. Причем сначала ищем только по названию улицы. Если нашли ровно одну, т.е. существует только
     * один тип улицы для улицы с таким названием, то поиск успешен, по id улицы узнаем тип улицы, по id типа улицы находим(или создаем
     * если ничего не нашли) коррекцию для типа улицы, находим (или создаем) коррекцию для улицы. Далее обрабатываем дом по схеме аналогичной
     * схеме обработки города.
     * Если по названию улицы ничего не нашли, то проставляем статус RequestStatus.STREET_UNRESOLVED_LOCALLY и выходим.
     * Если же нашли более одной, то пытаемся поискать по названию улицы, номеру дома и корпуса(если есть). Если нашли ровно одну улицу, то
     * проставляем в payment id улицы и дома и выходим не создаваю никаких коррекций. Если не нашли ничего или более одной, то проставляем
     * статус RequestStatus.STREET_UNRESOLVED_LOCALLY и выходим.
     * Замечание: статус RequestStatus.STREET_UNRESOLVED_LOCALLY не позволяет корректировать улицы для payment, см.
     *      RequestStatus.isAddressCorrectableForPayment().
     *
     * Это алгоритм применяется и к поиску домов и с незначительными поправками к поиску улиц.
     */
    public void resolveLocalAddress(AbstractAddressRequest request){
        //clear internal address ids
        request.setCityId(null);
        request.setStreetTypeId(null);
        request.setStreetId(null);
        request.setBuildingId(null);

        if ("0".equals(request.getBuildingCorp())){
            request.setBuildingCorp(null);
        }

        Long osznId = request.getOrganizationId();
        Long userOrganizationId = request.getUserOrganizationId();

        //Связывание города
        List<CityCorrection> cityCorrections = addressCorrectionBean.getCityCorrections(null, request.getCity(),
                osznId, userOrganizationId);

        if (cityCorrections.size() == 1) {
            CityCorrection cityCorrection = cityCorrections.get(0);
            request.setCityId(cityCorrection.getObjectId());
        } else if (cityCorrections.size() > 1) {
            request.setStatus(RequestStatus.MORE_ONE_LOCAL_CITY_CORRECTION);

            return;
        } else {
            List<Long> cityIds = addressCorrectionBean.getCityIds(request.getCity());

            if (cityIds.size() == 1) {
                request.setCityId(cityIds.get(0));
            } else if (cityIds.size() > 1) {
                request.setStatus(RequestStatus.MORE_ONE_LOCAL_CITY);

                return;
            } else {
                request.setCityId(null);
                request.setStatus(RequestStatus.CITY_UNRESOLVED_LOCALLY);

                return;
            }
        }

        //Связывание типа улицы
        if(request.getStreetType() != null){
            List<StreetTypeCorrection> streetTypeCorrections = addressCorrectionBean.getStreetTypeCorrections(null,
                    request.getStreetType(), osznId, userOrganizationId);

            if (streetTypeCorrections.size() == 1) {
                request.setStreetTypeId(streetTypeCorrections.get(0).getObjectId());
            } else if (streetTypeCorrections.size() > 1) {
                request.setStatus(RequestStatus.MORE_ONE_LOCAL_STREET_TYPE_CORRECTION);

                return;
            } else {
                List<Long> streetTypeIds = addressCorrectionBean.getStreetTypeIds(request.getStreetType());

                if (streetTypeIds.size() == 1) {
                    request.setStreetTypeId(streetTypeIds.get(0));
                } else if (streetTypeIds.size() > 1) {
                    request.setStatus(RequestStatus.MORE_ONE_LOCAL_STREET_TYPE);

                    return;
                } else {
                    request.setStreetTypeId(null);
                    request.setStatus(RequestStatus.STREET_TYPE_UNRESOLVED_LOCALLY);

                    return;
                }
            }
        }

        //Связывание улицы
        if (request.getStreet() == null || request.getStreet().isEmpty()){
            request.setStatus(RequestStatus.STREET_UNRESOLVED_LOCALLY);

            return;
        }

        List<StreetCorrection> streetCorrections = addressCorrectionBean.getStreetCorrections(request.getCityId(),
                request.getStreetTypeId(), null, null,  request.getStreet(),
                osznId, userOrganizationId);

        if (streetCorrections.size() == 1){
            StreetCorrection streetCorrection = streetCorrections.get(0);

            DomainObject street = streetStrategy.getDomainObject(streetCorrection.getObjectId());

            request.setStreetId(streetCorrection.getObjectId());
            request.setStreetTypeId(streetStrategy.getStreetType(street));
            request.setCityId(street.getParentId());
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
                String streetName = streetNames.iterator().next();

                //находим ids улиц по внутреннему названию
                List<Long> streetIds = streetStrategy.getStreetIds(request.getCityId(), request.getStreetTypeId(), streetName);

                if (streetIds.size() == 1) { //нашли ровно одну улицу
                    Long streetObjectId = streetIds.get(0);
                    request.setStreetId(streetObjectId);

                    DomainObject streetObject = streetStrategy.getDomainObject(streetObjectId, true);
                    request.setStreetTypeId(streetStrategy.getStreetType(streetObject));

                    //перейти к обработке дома
                } else if (streetIds.size() > 1) { // нашли больше одной улицы
                    //пытаемся найти по району
                    streetIds = streetStrategy.getStreetIdsByDistrict(request.getCityId(), request.getStreet(), osznId);

                    if (streetIds.size() == 1) { //нашли ровно одну улицу по району
                        Long streetObjectId = streetIds.get(0);

                        request.setStreetId(streetObjectId);
                        request.setStreetTypeId(streetStrategy.getStreetType(streetObjectId));

                        //перейти к обработке дома
                    } else {
                        // пытаемся искать дополнительно по номеру и корпусу дома
                        streetIds = streetStrategy.getStreetObjectIdsByBuilding(request.getCityId(), streetName,
                                request.getBuildingNumber(), request.getBuildingCorp());

                        if (streetIds.size() == 1) { //нашли ровно одну улицу с заданным номером и корпусом дома
                            Long streetObjectId = streetIds.get(0);

                            request.setStreetId(streetObjectId);
                            request.setStreetTypeId(streetStrategy.getStreetType(streetObjectId));

                            //проставить дом для payment и выйти
                            List<Long> buildingIds = buildingStrategy.getBuildingObjectIds(request.getCityId(),
                                    streetObjectId,request.getBuildingNumber(),request.getBuildingCorp());

                            if (buildingIds.size() == 1) {
                                request.setBuildingId(buildingIds.get(0));
                            } else {
                                throw new IllegalStateException("Building id was not found.");
                            }

                            request.setStatus(RequestStatus.CITY_UNRESOLVED);

                            return;
                        } else { // по доп. информации, состоящей из номера и корпуса дома, не смогли однозначно определить улицу

                            request.setStreetId(null);
                            request.setBuildingId(null);
                            request.setStatus(RequestStatus.MORE_ONE_LOCAL_STREET);
                            return;
                        }
                    }
                } else {
                    throw new IllegalStateException("Street name `" + streetName + "` was not found.");
                }
            } else {
                throw new IllegalStateException("Street `" + request.getStreet() +
                        "` is mapped to more one internal street objects: " + streetNames);
            }
        } else { // в коррекциях не нашли ни одного соответствия на внутренние объекты улиц
            // ищем по внутреннему справочнику улиц
            List<Long> streetIds = streetStrategy.getStreetIds(request.getCityId(), request.getStreetTypeId(), request.getStreet());

            if (streetIds.size() == 1) { // нашли ровно одну улицу
                Long streetId = streetIds.get(0);
                request.setStreetId(streetId);

                DomainObject streetObject = streetStrategy.getDomainObject(streetId, true);
                request.setStreetTypeId(streetStrategy.getStreetType(streetObject));

                // перейти к обработке дома
            } else if (streetIds.size() > 1) { // нашли более одной улицы
                //пытаемся найти по району
                streetIds = streetStrategy.getStreetIdsByDistrict(request.getCityId(), request.getStreet(), osznId);

                if (streetIds.size() == 1) { //нашли ровно одну улицу по району
                    Long streetId = streetIds.get(0);
                    request.setStreetId(streetId);
                    request.setStreetTypeId(streetStrategy.getStreetType(streetId));
                    // перейти к обработке дома
                } else {
                    // пытаемся искать дополнительно по номеру и корпусу дома
                    streetIds = streetStrategy.getStreetObjectIdsByBuilding(request.getCityId(), request.getStreet(),
                            request.getBuildingNumber(), request.getBuildingCorp());

                    if (streetIds.size() == 1) {
                        Long streetId = streetIds.get(0);

                        //проставить дом для payment и выйти
                        List<Long> buildingIds = buildingStrategy.getBuildingObjectIds(request.getCityId(), streetId,
                                request.getBuildingNumber(), request.getBuildingCorp());

                        if (buildingIds.size() == 1) {
                            request.setBuildingId(buildingIds.get(0));
                            request.setStreetId(streetId);
                            request.setStreetTypeId(streetStrategy.getStreetType(streetStrategy.getDomainObject(streetId)));
                        } else {
                            throw new IllegalStateException("Building id was not found.");
                        }
                        request.setStatus(RequestStatus.CITY_UNRESOLVED);

                        return;
                    } else { // по доп. информации, состоящей из номера и корпуса дома, не смогли однозначно определить улицу
                        request.setStreetId(null);
                        request.setBuildingId(null);
                        request.setStatus(RequestStatus.MORE_ONE_LOCAL_STREET);

                        return;
                    }
                }
            } else { // не нашли ни одной улицы
                request.setStreetId(null);
                request.setStatus(RequestStatus.STREET_UNRESOLVED_LOCALLY);

                return;
            }
        }

        //Связывание дома
        String buildingNumber = replaceBuildingNumberSymbol(request.getBuildingNumber());
        String buildingCorp = replaceBuildingCorpSymbol(request.getBuildingCorp());

        List<BuildingCorrection> buildingCorrections = addressCorrectionBean.getBuildingCorrections(
                request.getStreetId(), null, buildingNumber, buildingCorp,
                osznId, userOrganizationId);

        if (buildingCorrections.size() == 1) {
            request.setBuildingId(buildingCorrections.get(0).getObjectId());
        } else if (buildingCorrections.size() > 1) {
            request.setStatus(RequestStatus.MORE_ONE_LOCAL_BUILDING_CORRECTION);
        } else {
            List<Long> buildingIds = buildingStrategy.getBuildingObjectIds(request.getCityId(),
                    request.getStreetId(), buildingNumber, buildingCorp);

            if (buildingIds.size() == 1){
                request.setBuildingId(buildingIds.get(0));
            }else if (buildingIds.size() > 1) {
                request.setStatus(RequestStatus.MORE_ONE_LOCAL_BUILDING);
            } else if (buildingIds.isEmpty()){
                request.setBuildingId(null);
                request.setStatus(RequestStatus.BUILDING_UNRESOLVED_LOCALLY);
            }
        }

        //Связанно с внутренней адресной базой
        if (request.getBuildingId() != null){
            request.setStatus(RequestStatus.CITY_UNRESOLVED);
        }
    }

    /**
     * разрешить переход "локальная адресная база -> адрес центра начислений"
     * Алгоритм:
     * Пытаемся найти коррекцию(строковое полное название и код) по id города в локальной адресной базе и текущему ЦН.
     * Если не нашли, то проставляем статус RequestStatus.CITY_UNRESOLVED и выходим, т.к. без города продолжать не имеет смысла.
     * Если нашли, то проставляем в payment информацию из коррекции(полное название, код) посредством метода
     * adapter.prepareCity() в адаптере для взаимодействия с ЦН.
     * См. org.complitex.osznconnection.file.calculation.adapter.DefaultCalculationCenterAdapter - адаптер по умолчанию.
     * Квартиры не ищем, а проставляем напрямую, обрезая пробелы.
     * Алгоритм аналогичен для поиска остальных составляющих адреса.
     */
    public void resolveOutgoingAddress(AbstractAddressRequest request) {
        Long userOrganizationId = request.getUserOrganizationId();
        Long billingId = organizationStrategy.getBillingId(userOrganizationId);

        Locale locale = stringLocaleBean.getSystemLocale();

        //город
        List<CityCorrection> cityCorrections = addressCorrectionBean.getCityCorrections(request.getCityId(),
                null, billingId, userOrganizationId);

        if (cityCorrections.isEmpty()){
            DomainObject city = cityStrategy.getDomainObject(request.getCityId(), true);

            if (city != null){
                request.setOutgoingCity(cityStrategy.getName(city, locale));
            }else {
                request.setStatus(RequestStatus.CITY_UNRESOLVED);

                return;
            }
        } else if (cityCorrections.size() == 1) {
            request.setOutgoingCity(cityCorrections.get(0).getCorrection());
        } else {
            request.setStatus(RequestStatus.MORE_ONE_REMOTE_CITY_CORRECTION);

            return;
        }

        // район
        List<DistrictCorrection> districtCorrections = addressCorrectionBean.getDistrictCorrections(request.getCityId(),
                null,null, null, billingId, userOrganizationId);

        if (districtCorrections.isEmpty()){
            DomainObject organization = organizationStrategy.getDomainObject(request.getOrganizationId(), true);

            Long districtId = organization.getAttribute(IOrganizationStrategy.DISTRICT).getValueId();
            DomainObject district = districtStrategy.getDomainObject(districtId, true);

            if (district != null){
                request.setOutgoingDistrict(districtStrategy.displayDomainObject(district, locale));
            }else {
                request.setStatus(RequestStatus.DISTRICT_UNRESOLVED);

                return;
            }

        } else if (districtCorrections.size() == 1) {
            request.setOutgoingDistrict(districtCorrections.get(0).getCorrection());
        } else if (districtCorrections.size() > 1) {
            request.setStatus(RequestStatus.MORE_ONE_REMOTE_DISTRICT_CORRECTION);

            return;
        }

        //тип улицы
        if (request.getStreetTypeId() != null) {
            List<StreetTypeCorrection> streetTypeCorrections = addressCorrectionBean.getStreetTypeCorrections(
                    request.getStreetTypeId(), null, billingId, userOrganizationId);

            if (streetTypeCorrections.isEmpty()){
                DomainObject streetType = streetTypeStrategy.getDomainObject(request.getStreetTypeId(), true);

                if (streetType != null){
                    request.setOutgoingStreetType(streetTypeStrategy.getShortName(streetType, locale));
                }else{
                    request.setStatus(RequestStatus.STREET_TYPE_UNRESOLVED);

                    return;
                }
            }else if (streetTypeCorrections.size() == 1){
                request.setOutgoingStreetType(streetTypeCorrections.get(0).getCorrection());
            }else {
                request.setStatus(RequestStatus.MORE_ONE_LOCAL_STREET_TYPE_CORRECTION);

                return;
            }
        }

        //улица
        List<StreetCorrection> streetCorrections = addressCorrectionBean.getStreetCorrections(null,
                request.getStreetId(), null, null, null, billingId, userOrganizationId);

        if (streetCorrections.isEmpty()){
            DomainObject street = streetStrategy.getDomainObject(request.getStreetId(), true);

            if (street != null){
                request.setOutgoingStreet(streetStrategy.getName(street, locale));
            }else {
                request.setStatus(RequestStatus.STREET_UNRESOLVED);

                return;
            }
        } else if (streetCorrections.size() == 1) {
            request.setOutgoingStreet(streetCorrections.get(0).getCorrection());
        } else {
            streetCorrections = addressCorrectionBean.getStreetCorrectionsByBuilding(request.getStreetId(),
                    request.getBuildingId(), billingId);

            if (streetCorrections.size() == 1) {
                request.setOutgoingStreet(streetCorrections.get(0).getCorrection());
            } else {
                request.setStatus(RequestStatus.MORE_ONE_REMOTE_STREET_CORRECTION);

                return;
            }
        }

        //дом
        List<BuildingCorrection> buildingCorrections = addressCorrectionBean.getBuildingCorrections(null,
                request.getBuildingId(), null, null, billingId, userOrganizationId);

        if (buildingCorrections.isEmpty()){
            Building building = buildingStrategy.getDomainObject(request.getBuildingId(), true);

            if (building != null){
                request.setOutgoingBuildingNumber(building.getAccompaniedNumber(locale));
                request.setOutgoingBuildingCorp(building.getAccompaniedCorp(locale));
            }else {
                request.setStatus(RequestStatus.BUILDING_UNRESOLVED);

                return;
            }
        }else  if(buildingCorrections.size() == 1) {
            BuildingCorrection buildingCorrection = buildingCorrections.get(0);
            request.setOutgoingBuildingNumber(buildingCorrection.getCorrection());
            request.setOutgoingBuildingCorp(buildingCorrection.getCorrectionCorp());
        } else if (buildingCorrections.size() > 1) {
            request.setStatus(RequestStatus.MORE_ONE_REMOTE_BUILDING_CORRECTION);

            return;
        }

        //квартира
        request.setOutgoingApartment(replaceApartmentSymbol(removeWhiteSpaces(request.getApartment())));

        request.setStatus(RequestStatus.ACCOUNT_NUMBER_NOT_FOUND);
    }

    /**
     * разрешить адрес по схеме "ОСЗН адрес -> локальная адресная база -> адрес центра начислений"
     */

    public void resolveAddress(AbstractAddressRequest request) {
        //разрешить адрес локально
        resolveLocalAddress(request);

        //если адрес локально разрешен, разрешить адрес для ЦН.
        if (request.getStatus().isAddressResolvedLocally()) {
            resolveOutgoingAddress(request);
        }
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
     * @param request AbstractAccountRequest
     * @param cityObjectId Откорректированный город
     * @param streetObjectId Откорректированная улица
     * @param streetTypeObjectId Откорректированный тип улицы
     * @param buildingObjectId Откорректированный дом
     */

    @Deprecated
    public void correctLocalAddress(AbstractAccountRequest request, AddressEntity entity, Long cityObjectId,
                                    Long streetTypeObjectId, Long streetObjectId, Long buildingObjectId,
                                    Long userOrganizationId)
            throws DuplicateCorrectionException, MoreOneCorrectionException, NotFoundCorrectionException {
        Long osznId = request.getOrganizationId();
        Long moduleId = moduleBean.getModuleId();

        switch (entity) {
            case CITY: {
                List<CityCorrection> cityCorrections = addressCorrectionBean.getCityCorrections(null, request.getCity(),
                        osznId, userOrganizationId);

                if (cityCorrections.isEmpty()) {
                    CityCorrection cityCorrection = new CityCorrection(null, cityObjectId, request.getCity().toUpperCase(),
                            osznId, userOrganizationId, moduleId);
                    addressCorrectionBean.save(cityCorrection);
                } else {
                    throw new DuplicateCorrectionException();
                }
            }
            break;

            case STREET_TYPE: {
                List<StreetTypeCorrection> streetTypeCorrections = addressCorrectionBean.getStreetTypeCorrections(
                        null, request.getStreetType(), osznId, userOrganizationId);

                if (streetTypeCorrections.isEmpty()) {
                    StreetTypeCorrection streetTypeCorrection = new StreetTypeCorrection(request.getStreetTypeCode(),
                            streetTypeObjectId,
                            request.getStreetType().toUpperCase(),
                            osznId, userOrganizationId, moduleId);
                    addressCorrectionBean.save(streetTypeCorrection);
                } else {
                    throw new DuplicateCorrectionException();
                }
            }
            break;

            case STREET:
                Long streetTypeId = request.getStreetTypeId() != null
                        ? request.getStreetTypeId() : streetTypeObjectId;

                List<StreetCorrection> streetCorrections = addressCorrectionBean.getStreetCorrections(
                        request.getCityId(), streetTypeId, null, null, request.getStreet(), osznId, userOrganizationId);

                if (streetCorrections.isEmpty()) {
                    StreetCorrection streetCorrection = new StreetCorrection(request.getCityId(), streetTypeId,
                            request.getStreetCode(), streetObjectId, request.getStreet().toUpperCase(),
                            osznId, userOrganizationId, moduleId);

                    addressCorrectionBean.save(streetCorrection);
                } else {
                    throw new DuplicateCorrectionException();
                }

                break;

            case BUILDING:
                List<BuildingCorrection> buildingCorrections = addressCorrectionBean.getBuildingCorrections(
                        request.getStreetId(), null, request.getBuildingNumber(), request.getBuildingCorp(),
                        osznId, userOrganizationId);

                if (buildingCorrections.isEmpty()) {
                    BuildingCorrection buildingCorrection = new BuildingCorrection(request.getStreetId(), null,
                            buildingObjectId,
                            request.getBuildingNumber().toUpperCase(),
                            request.getBuildingCorp() != null ? request.getBuildingCorp().toUpperCase() : null,
                            osznId, userOrganizationId, moduleId);

                    addressCorrectionBean.save(buildingCorrection);
                } else {
                    throw new DuplicateCorrectionException();
                }

                break;
        }
    }

    public String resolveOutgoingDistrict(Long organizationId, Long userOrganizationId) {
        DomainObject organization = organizationStrategy.getDomainObject(organizationId, true);
        Long districtId = organization.getAttribute(IOrganizationStrategy.DISTRICT).getValueId();

        String districtName = districtStrategy.displayDomainObject(districtId, stringLocaleBean.getSystemLocale());

        if (districtName != null){
            List<DistrictCorrection> districtCorrections = addressCorrectionBean.getDistrictCorrections(null, null, null,
                    districtName, organizationId, userOrganizationId);

            return !districtCorrections.isEmpty()
                    ? districtStrategy.displayDomainObject(districtCorrections.get(0).getObjectId(), stringLocaleBean.getSystemLocale())
                    : districtName;
        }

        return null;
    }

    public Set<String> getStreetNames(AbstractAddressRequest request){
        Set<String> streetNames = new HashSet<>();

        streetStrategy.getStreetIds(request.getCityId(), request.getStreetTypeId(), request.getStreet())
                .forEach(id -> streetStrategy.getDomainObject(id)
                        .getAttribute(StreetStrategy.NAME)
                        .getStringCultures()
                        .forEach(s -> streetNames.add(s.getValue().toUpperCase())));

        if (streetNames.isEmpty()){
            addressCorrectionBean.getStreetCorrections(request.getCityId(),request.getStreetTypeId(), null, null,
                    request.getStreet(), request.getOrganizationId(), request.getUserOrganizationId())
                    .forEach(c -> streetStrategy.getDomainObject(c.getObjectId())
                            .getAttribute(StreetStrategy.NAME)
                            .getStringCultures()
                            .forEach(s -> streetNames.add(s.getValue().toUpperCase())));
        }

        if (streetNames.isEmpty()){
            streetNames.add(request.getStreet().toUpperCase());
        }

        return streetNames;
    }
}
