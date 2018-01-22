package org.complitex.sync.service;

import org.complitex.address.exception.RemoteCallException;
import org.complitex.address.exception.SyncException;
import org.complitex.common.entity.Cursor;
import org.complitex.common.entity.DictionaryConfig;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.service.AbstractBean;
import org.complitex.common.service.ConfigBean;
import org.complitex.common.strategy.organization.IOrganizationStrategy;
import org.complitex.sync.entity.DomainSync;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.complitex.common.util.MapUtil.of;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 31.10.13 15:59
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class DomainSyncAdapter extends AbstractBean {
    private final static String NS = DomainSyncAdapter.class.getName();
    private final Logger log = LoggerFactory.getLogger(DomainSyncAdapter.class);

    @EJB
    private ConfigBean configBean;

    @EJB(lookup = IOrganizationStrategy.BEAN_LOOKUP)
    private IOrganizationStrategy organizationStrategy;

    public DomainObject getOrganization(){
        Long organizationId = configBean.getLong(DictionaryConfig.SYNC_DATA_SOURCE, true);

        DomainObject organization = organizationStrategy.getDomainObject(organizationId, true);

        if (organization == null){
            throw new SyncException("Не выбран JDBC ресурс синхронизации");
        }

        return organization;
    }

    public String getDataSource(){
        String dataSource = getOrganization().getStringValue(IOrganizationStrategy.DATA_SOURCE);

        if (dataSource == null){
            throw new SyncException("JDBC ресурс не найден");
        }

        return dataSource;
    }

    @SuppressWarnings("unchecked")
    private Cursor<DomainSync> getDomainSyncCursor(String statement, Date date, Map<String, Object> param) throws RemoteCallException{
        param.put("date", date);
        param.put("okCode", 0);

        try {
            sqlSession(getDataSource()).selectOne(NS + "." + statement, param);
        } catch (Exception e) {
            throw new RemoteCallException(e);
        }

        log.info("{}: {}", statement, param);

        return new Cursor<>((Integer)param.get("resultCode"), (List<DomainSync>) param.get("out"));
    }

    private Cursor<DomainSync> getDomainSyncCursor(String statement, Date date) throws RemoteCallException{
        return getDomainSyncCursor(statement, date, null);
    }

        /**
         * z$runtime_sz_utl.getCountries
         * Типы улиц.
         * Возвращает: 1 либо код ошибки (INTEGER):
         * -10 Неопознанная ошибка
         */
    @SuppressWarnings("unchecked")
    public Cursor<DomainSync> getCountrySyncs(Date date) throws RemoteCallException {
        return getDomainSyncCursor("selectCountrySyncs", date);
    }

    /**
     * z$runtime_sz_utl.getRegions
     * Районы населённого пункта.
     * Возвращает: 1 либо код ошибки (INTEGER):
     * -10 Неопознанная ошибка
     * -1 Не найдена страна
     */
    @SuppressWarnings("unchecked")
    public Cursor<DomainSync> getRegionSyncs(String countryName, Date date) throws RemoteCallException {
        return getDomainSyncCursor("selectRegionSyncs", date, of("countryName", countryName));
    }

    @SuppressWarnings("unchecked")
    public Cursor<DomainSync> getCityTypeSyncs(Date date) throws RemoteCallException {
        return getDomainSyncCursor("selectCityTypeSyncs", date);
    }

    /**
     * z$runtime_sz_utl.getCities
     * Районы населённого пункта.
     * Возвращает: 1 либо код ошибки (INTEGER):
     * -10 Неопознанная ошибка
     * -1 Не найден регион
     */
    @SuppressWarnings("unchecked")
    public Cursor<DomainSync> getCitySyncs(String regionName, Date date) throws RemoteCallException {
        return getDomainSyncCursor("selectCitySyncs", date, of("regionName", regionName));
    }

    /**
     * function z$runtime_sz_utl.getDistricts(
     *     pCityName varchar2,    -- Название нас.пункта
     *     pCityType varchar2,    -- Тип нас.пункта (краткое название)
     *     pDate date,            -- Дата актуальности
     *     Cur out TCursor
     * ) return integer;
     * поля курсора:
     * DistrName - varchar2,          -- Название района
     * DistrID - varchar2,            -- Код района (ID)
     * возвращаемое значение: 0 - все хорошо, -1 - неизвестный тип нас.пункта, -2 - неизвестный нас.пункт
     */
    @SuppressWarnings("unchecked")
    public Cursor<DomainSync> getDistrictSyncs(String cityTypeName, String cityName, Date date) throws RemoteCallException {
        return getDomainSyncCursor("selectDistrictSyncs", date, of("cityName", cityName, "cityTypeName", cityTypeName));
    }

    /**
     * function z$runtime_sz_utl.getStreetTypes(
     * Cur out TCursor
     * ) return integer;
     * поля курсора:
     * StrTypeName - varchar2,          -- Полное название типа улицы
     * ShStrTypeName - varchar2,       -- Краткое название типа улицы
     * StreetTypeID - varchar2,          -- Код типа улицы (ID)
     * возвращаемое значение: 0 - все хорошо, -1 - ошибка
     */
    @SuppressWarnings("unchecked")
    public Cursor<DomainSync> getStreetTypeSyncs(Date date) throws RemoteCallException {
        return getDomainSyncCursor("selectStreetTypeSyncs", date);
    }

    /**
     * function z$runtime_sz_utl.getStreets(
     * pCityName varchar2,    -- Название нас.пункта
     * pCityType varchar2,     -- Название типа нас.пункта
     * pDate date,                -- Дата актуальности
     * Cur out TCursor
     * ) return integer;
     * поля курсора:
     * StreetName - varchar2,          -- Название улицы
     * StreetType - varchar2,          -- Тип улицы (краткое название)
     * StreetID - varchar2,              -- Код улицы (ID)
     * возвращаемое значение: 0 - все хорошо, -1 - неизвестный тип нас.пункта, -2 - неизвестный нас.пункт
     */
    @SuppressWarnings("unchecked")
    public Cursor<DomainSync> getStreetSyncs(String cityName, String cityTypeName, Date date) throws RemoteCallException {
        return getDomainSyncCursor("selectStreetSyncs", date, of("cityName", cityName, "cityTypeName", cityTypeName));
    }

    /**
     * z$runtime_sz_utl.getBuildings Дома.
     * Возвращает: 1 либо код ошибки (INTEGER):
     * -10 Неопознанная ошибка
     * -7 Не найдена улица
     * -6 Не найден тип улицы
     * -5 Не найден район
     * -4 Не найден нас.пункт
     * -3 Не найден тип нас.пункта
     * Параметры функции:
     * pCityName VARCHAR2 Название населённого пункта
     * pCityType VARCHAR2 Название типа населённого пункта
     * pDistrName VARCHAR2 Название района
     * pStreetName VARCHAR2 Название улицы
     * pStreetType VARCHAR2 Название типа улицы pDate
     * DATE Дата актуальности
     * Cur ref CURSOR Возвращаемый курсор со списком домов
     */
    @SuppressWarnings("unchecked")
    public Cursor<DomainSync> getBuildingSyncs(String cityName, String cityTypeName, String districtName,
                                               String streetTypeName, String streetName, Date date) throws RemoteCallException {
        return getDomainSyncCursor("selectBuildingSyncs", date, of("cityName", cityName,
                "cityTypeName", cityTypeName, "districtName", districtName, "streetName", streetName,
                "streetTypeName", streetTypeName));
    }

    @SuppressWarnings("unchecked")
    public Cursor<DomainSync> getOrganizationSyncs(Date date) throws RemoteCallException {
        return getDomainSyncCursor("selectOrganizationSyncs", date);
    }
}
