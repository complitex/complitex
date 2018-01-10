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
import org.complitex.sync.entity.SyncEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public Cursor<DomainSync> getDistrictSyncs(String cityName, String cityTypeName, Date date) throws RemoteCallException {
        Map<String, Object> param = new HashMap<>();

        param.put("cityName", cityName);
        param.put("cityTypeName", cityTypeName);
        param.put("date", date);
        param.put("okCode", 0);

        try {
            sqlSession(getDataSource()).selectOne(NS + ".selectDistrictSyncs", param);
        } catch (Exception e) {
            throw new RemoteCallException(e);
        }

        log.info("getDistrictSyncs: " + param);

        return new Cursor<>((Integer)param.get("resultCode"), (List<DomainSync>) param.get("out"));
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
    public Cursor<DomainSync> getStreetTypeSyncs() throws RemoteCallException {
        Map<String, Object> param = new HashMap<>();
        param.put("okCode", 0);

        try {
            sqlSession(getDataSource()).selectOne(NS + ".selectStreetTypeSyncs", param);
        } catch (Exception e) {
            throw new RemoteCallException(e);
        }

        log.info("getStreetTypeSyncs: " + param);

        return new Cursor<>((Integer)param.get("resultCode"), (List<DomainSync>) param.get("out"));
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
        Map<String, Object> param = new HashMap<>();

        param.put("cityName", cityName);
        param.put("cityTypeName", cityTypeName);
        param.put("date", date);
        param.put("okCode", 0);

        try {
            sqlSession(getDataSource()).selectOne(NS + ".selectStreetSyncs", param);
        } catch (Exception e) {
            throw new RemoteCallException(e);
        }

        List<DomainSync> list = (List<DomainSync>) param.get("out");

        if (list != null){
            for (DomainSync sync : list){
                sync.setType(SyncEntity.STREET);
            }
        }

        return new Cursor<>((Integer)param.get("resultCode"), list);
    }

    /**
     * function z$runtime_sz_utl.getBuildings(
     * DistrName varchar2,        -- Название района нас.пункта
     * pStreetName varchar2,    -- Название улицы
     * pStreetType varchar2,     -- Тип улицы (краткое название)
     * pDate date,                   -- Дата актуальности
     * Cur out TCursor
     * ) return integer;
     * поля курсора:
     * BldNum - varchar2,          -- Номер дома
     * BldPart - varchar2,          -- Номер корпуса
     * BldID - varchar2,             -- Код дома (ID)
     * StreetID - varchar2,         -- Код улицы (ID)
     * возвращаемое значение: 0 - все хорошо, -3 - неизвестный район нас.пункта, -4 - неизвестный тип улицы, -5 - неизвестная улица
     */
    @SuppressWarnings("unchecked")
    public Cursor<DomainSync> getBuildingSyncs(String districtName, String streetTypeName,
                                               String streetName, Date date) throws RemoteCallException {
        Map<String, Object> param = new HashMap<>();

        param.put("districtName", districtName);
        param.put("streetName", streetName);
        param.put("streetTypeName", streetTypeName);
        param.put("date", date);
        param.put("okCode", 0);

        try {
            sqlSession(getDataSource()).selectOne(NS + ".selectBuildingSyncs", param);
        } catch (Exception e) {
            throw new RemoteCallException(e);
        }

        return new Cursor<>((Integer)param.get("resultCode"), (List<DomainSync>) param.get("out"));
    }

    @SuppressWarnings("unchecked")
    public Cursor<DomainSync> getOrganizationSyncs(Date date) throws RemoteCallException {
        Map<String, Object> param = new HashMap<>();

        param.put("okCode", 0);
        param.put("date", date);

        try {
            sqlSession(getDataSource()).selectOne(NS + ".selectOrganizationSyncs", param);
        } catch (Exception e) {
            throw new RemoteCallException(e);
        }

        log.info("getOrganizationSyncs: " + param);

        return new Cursor<>((Integer)param.get("resultCode"), (List<DomainSync>) param.get("out"));
    }
}
