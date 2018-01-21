package org.complitex.osznconnection.file.service.privilege;

import org.apache.wicket.util.string.Strings;
import org.complitex.address.entity.AddressEntity;
import org.complitex.address.strategy.city.CityStrategy;
import org.complitex.address.strategy.street.StreetStrategy;
import org.complitex.address.strategy.street_type.StreetTypeStrategy;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.entity.FilterWrapper;
import org.complitex.common.entity.Log;
import org.complitex.common.exception.ExecuteException;
import org.complitex.common.service.AbstractBean;
import org.complitex.common.service.ConfigBean;
import org.complitex.common.service.LogBean;
import org.complitex.common.service.ModuleBean;
import org.complitex.common.strategy.StringLocaleBean;
import org.complitex.common.util.ResourceUtil;
import org.complitex.correction.entity.Correction;
import org.complitex.correction.service.CorrectionBean;
import org.complitex.osznconnection.file.Module;
import org.complitex.osznconnection.file.entity.AbstractRequest;
import org.complitex.osznconnection.file.entity.FileHandlingConfig;
import org.complitex.osznconnection.file.entity.RequestFileType;
import org.complitex.osznconnection.file.entity.privilege.FacilityStreet;
import org.complitex.osznconnection.file.entity.privilege.FacilityStreetDBF;
import org.complitex.osznconnection.file.entity.privilege.FacilityStreetType;
import org.complitex.osznconnection.file.entity.privilege.FacilityTarif;
import org.complitex.osznconnection.file.service.privilege.task.FacilityStreetLoadTaskBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.util.List;
import java.util.Locale;

import static com.google.common.collect.ImmutableMap.of;

/**
 *
 * @author Artem
 */
@Stateless
public class FacilityReferenceBookBean extends AbstractBean {

    private static final String RESOURCE_BUNDLE = FacilityReferenceBookBean.class.getName();
    private static final String NS = FacilityReferenceBookBean.class.getName();
    private final Logger log = LoggerFactory.getLogger(FacilityReferenceBookBean.class);

    @EJB
    private CorrectionBean correctionBean;

    @EJB
    private ConfigBean configBean;

    @EJB
    private CityStrategy cityStrategy;

    @EJB
    private StreetStrategy streetStrategy;

    @EJB
    private StreetTypeStrategy streetTypeStrategy;

    @EJB
    private LogBean logBean;

    @EJB
    private StringLocaleBean stringLocaleBean;

    @EJB
    private ModuleBean moduleBean;


    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void insert(List<? extends AbstractRequest> requests) {
        if (requests != null && !requests.isEmpty()) {
            final RequestFileType requestFileType = requests.get(0).getRequestFileType();
            final String table = getTableName(requestFileType);

            for (AbstractRequest request : requests) {
                sqlSession().insert(NS + ".insertFacilityReferences", of("entityName", table, "request", request));
            }
        }
    }

    public String getTableName(RequestFileType requestFileType) {
        switch (requestFileType) {
            case FACILITY_STREET_TYPE_REFERENCE:
                return "facility_street_type_reference";
            case FACILITY_STREET_REFERENCE:
                return "facility_street_reference";
            case FACILITY_TARIF_REFERENCE:
                return "facility_tarif_reference";
            default:
                throw new IllegalStateException("Illegal request file type: " + requestFileType);
        }
    }


    public void delete(long requestFileId, RequestFileType requestFileType) {
        sqlSession().delete(NS + ".deleteFacilityReferences", of("requestFileId", requestFileId, "entityName",
                getTableName(requestFileType)));
    }

    //FacilityStreetType

    public List<FacilityStreetType> getFacilityStreetTypes(FilterWrapper<FacilityStreetType> filterWrapper){
        return sqlSession().selectList(NS + ".selectFacilityStreetTypes", filterWrapper);
    }

    public Long getFacilityStreetTypesCount(FilterWrapper<FacilityStreetType> filterWrapper){
        return sqlSession().selectOne(NS + ".selectFacilityStreetTypesCount", filterWrapper);
    }

    //FacilityStreet

    public List<FacilityStreet> getFacilityStreets(FilterWrapper<FacilityStreet> filterWrapper){
        return sqlSession().selectList(NS + ".selectFacilityStreets", filterWrapper);
    }

    public Long getFacilityStreetsCount(FilterWrapper<FacilityStreet> filterWrapper){
        return sqlSession().selectOne(NS + ".selectFacilityStreetsCount", filterWrapper);
    }

    public FacilityStreet getFacilityStreet(String streetCode, Long osznId, Long userOrganizationId){
        return sqlSession().selectOne(NS + ".selectFacilityStreetByCode", of("streetCode", streetCode, "osznId", osznId,
                "userOrganizationId", userOrganizationId));
    }

    public FacilityStreet getFacilityStreet(Long requestFileId, String streetCode){
        return sqlSession().selectOne(NS + ".selectFacilityStreetByRequestFile", of("requestFileId", requestFileId,
                "streetCode", streetCode));
    }

    //FacilityTarif

    public List<FacilityTarif> getFacilityTarifs(FilterWrapper<FacilityTarif> filterWrapper){
        return sqlSession().selectList(NS + ".selectFacilityTarifs", filterWrapper);
    }

    public Long getFacilityTarifsCount(FilterWrapper<FacilityTarif> filterWrapper){
        return sqlSession().selectOne(NS + ".selectFacilityTarifsCount", filterWrapper);
    }

    private List<String> findStreetTypeNames(String streetTypeCode, long osznId, long userOrganizationId) {
        return sqlSession().selectList(NS + ".findStreetTypeNames", of("streetTypeCode", streetTypeCode, "osznId", osznId,
                        "userOrganizationId", userOrganizationId));
    }

    private String printStringValue(String value, Locale locale) {
        return Strings.isEmpty(value) ? ResourceUtil.getString(RESOURCE_BUNDLE, "empty_string_value", locale) : value;
    }

    public void updateStreetCorrections(String streetCode, Long userOrganizationId, Long osznId)
            throws ExecuteException {
        FacilityStreet facilityStreet = getFacilityStreet(streetCode, osznId, userOrganizationId);

        if (facilityStreet != null) {
            updateStreetCorrections(facilityStreet, userOrganizationId, osznId, "");
        }
    }

    public void updateStreetCorrections(FacilityStreet street, Long userOrganizationId, Long osznId,
            final String streetTypeReferenceFileName) throws ExecuteException {
        Locale locale = stringLocaleBean.getSystemLocale();
        Long moduleId = moduleBean.getModuleId();

        String streetName = street.getStringField(FacilityStreetDBF.KL_NAME);
        String streetCode = street.getStringField(FacilityStreetDBF.KL_CODEUL);
        String streetTypeCode = street.getStringField(FacilityStreetDBF.KL_CODEKUL);

        final String defaultCity = configBean.getString(FileHandlingConfig.DEFAULT_REQUEST_FILE_CITY, true);

        Long cityId;
        Correction cityCorrection;

        List<Correction> cityCorrections = correctionBean.getCorrections(AddressEntity.CITY,
                defaultCity, osznId, userOrganizationId);

        if (cityCorrections.size() == 1) {
            cityCorrection = cityCorrections.get(0);
            cityId = cityCorrection.getObjectId();
        } else {
            final String errorKey = cityCorrections.size() > 1 ? "city_corrections.too_many" : "city_corrections.not_found";
            throw new ExecuteException(ResourceUtil.getFormatString(RESOURCE_BUNDLE, errorKey, locale,
                    printStringValue(defaultCity, locale)));
        }

        String streetTypeName;
        List<String> streetTypeNames = findStreetTypeNames(streetTypeCode, osznId, userOrganizationId);

        if (streetTypeNames.size() == 1) {
            streetTypeName = streetTypeNames.get(0);
        } else {
            final String errorKey = streetTypeNames.size() > 1 ? "facility_street_type.too_many"
                    : "facility_street_type.not_found";
            throw new ExecuteException(ResourceUtil.getFormatString(RESOURCE_BUNDLE, errorKey, locale,
                    printStringValue(streetCode, locale), streetTypeReferenceFileName));
        }

        Long streetTypeId;
        Correction streetTypeCorrection;
        List<Correction> streetTypeCorrections =
                correctionBean.getCorrections(AddressEntity.STREET_TYPE, streetTypeName, osznId, userOrganizationId);

        if (streetTypeCorrections.size() == 1) {
            streetTypeCorrection = streetTypeCorrections.get(0);
            streetTypeId = streetTypeCorrection.getObjectId();
        } else if (streetTypeCorrections.size() > 1) {
            throw new ExecuteException(ResourceUtil.getFormatString(RESOURCE_BUNDLE, "facility_street_type_corrections.too_many",
                    locale, printStringValue(streetTypeName, locale)));
        } else {
            // искать по внутренней базе типов улиц
            List<Long> streetTypeIds = correctionBean.getObjectIds(AddressEntity.STREET_TYPE,
                    streetTypeName, StreetTypeStrategy.NAME);
            if (streetTypeIds.size() == 1) {
                streetTypeId = streetTypeIds.get(0);
                streetTypeCorrection = new Correction(AddressEntity.STREET_TYPE.getEntityName(), null,
                        streetTypeId, streetTypeName.toUpperCase(), osznId, userOrganizationId);

                correctionBean.save(streetTypeCorrection);
            } else if (streetTypeIds.size() > 1) {
                throw new ExecuteException(ResourceUtil.getFormatString(RESOURCE_BUNDLE, "facility_internal_street_type.too_many",
                                locale, printStringValue(streetTypeName, locale)));
            } else {
                logBean.error(Module.NAME, FacilityStreetLoadTaskBean.class, FacilityStreet.class, null, street.getId(),
                        Log.EVENT.CREATE, null,
                        ResourceUtil.getFormatString(RESOURCE_BUNDLE, "facility_internal_street_type.not_found",
                        locale, printStringValue(streetTypeName, locale)));
                log.error("No one internal street type was found in local base. Street type name: '{}', oszn id: {}, "
                        + "user organization id: {}", streetTypeName, osznId, userOrganizationId);
                return;
            }
        }

        List<Correction> streetCorrections = correctionBean.getCorrections(AddressEntity.STREET,
                cityCorrection.getObjectId(), streetTypeCorrection.getObjectId(), streetName, osznId, userOrganizationId);

        if (streetCorrections.size() == 1) {
//            StreetCorrection streetCorrection = streetCorrections.get(0);
//            if (!Strings.isEqual(streetCode, streetCorrection.getExternalId())) {
//                // коды не совпадают, нужно обновить код соответствия.
//                streetCorrection.setExternalId(streetCode);
//
//                addressCorrectionBean.save(streetCorrection);
//            }
        } else if (streetCorrections.size() > 1) {
            throw new ExecuteException(ResourceUtil.getFormatString(RESOURCE_BUNDLE, "facility_street_corrections.too_many",
                            locale, printStringValue(cityCorrection.getCorrection(), locale), cityCorrection.getId(),
                            printStringValue(streetTypeCorrection.getCorrection(), locale), streetTypeCorrection.getId(),
                            printStringValue(streetName, locale)));
        } else {
            // искать по внутренней базе улиц
            List<Long> streetIds = streetStrategy.getStreetIds(cityId, streetTypeId, streetName);

            if (streetIds.size() == 1) {
                long streetId = streetIds.get(0);

                Correction streetCorrection =  new Correction(AddressEntity.STREET.getEntityName(), cityId, streetTypeId,
                        null, streetId, streetName.toUpperCase(), osznId, userOrganizationId);

                correctionBean.save(streetCorrection);
            } else {
                final DomainObject internalCity = cityStrategy.getDomainObject(cityId, true);
                final String internalCityName = cityStrategy.displayDomainObject(internalCity, locale);
                final DomainObject internalStreetType = streetTypeStrategy.getDomainObject(streetTypeId, true);
                final String internalStreetTypeName = streetTypeStrategy.displayDomainObject(internalStreetType, locale);

                if (streetIds.size() > 1) {
                    throw new ExecuteException(ResourceUtil.getFormatString(RESOURCE_BUNDLE, "facility_internal_street.too_many",
                                    locale, internalCityName, cityId, internalStreetTypeName, streetTypeId,
                                    printStringValue(streetName, locale)));
                } else {
                    logBean.error(Module.NAME, FacilityStreetLoadTaskBean.class, FacilityStreet.class, null, street.getId(),
                            Log.EVENT.CREATE, null, ResourceUtil.getFormatString(RESOURCE_BUNDLE, "facility_internal_street.not_found",
                            locale, internalCityName, cityId, internalStreetTypeName, streetTypeId,
                            printStringValue(streetName, locale)));
                    log.error("No one internal street was found in local base. Internal city id: {}, "
                            + "internal street type id: {}, street name: '{}', oszn id: {}, user organization id: {}",
                            cityId, streetTypeId, streetName, osznId, userOrganizationId);
                }
            }
        }
    }
}
