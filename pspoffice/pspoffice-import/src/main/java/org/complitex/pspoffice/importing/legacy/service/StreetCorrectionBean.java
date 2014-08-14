/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.importing.legacy.service;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ejb.Stateless;
import org.complitex.address.strategy.street.StreetStrategy;
import org.complitex.address.strategy.street_type.StreetTypeStrategy;
import org.complitex.dictionary.service.AbstractBean;
import org.complitex.pspoffice.importing.legacy.entity.StreetCorrection;

/**
 *
 * @author Artem
 */
@Stateless
public class StreetCorrectionBean extends AbstractBean {

    private static final String MAPPING_NAMESPACE = StreetCorrectionBean.class.getName();

    public void insert(StreetCorrection streetCorrection) {
        sqlSession().insert(MAPPING_NAMESPACE + ".insert", streetCorrection);
    }

    public void update(StreetCorrection streetCorrection) {
        sqlSession().update(MAPPING_NAMESPACE + ".update", streetCorrection);
    }

    public void clearProcessingStatus(Set<String> jekIds) {
        sqlSession().update(MAPPING_NAMESPACE + ".clearProcessingStatus", ImmutableMap.of("value", jekIds));
    }

    public StreetCorrection getById(String id, String idjek) {
        return (StreetCorrection) sqlSession().selectOne(MAPPING_NAMESPACE + ".getById",
                ImmutableMap.of("id", id, "idjek", idjek));
    }

    public boolean exists(String idjek) {
        return (Integer) sqlSession().selectOne(MAPPING_NAMESPACE + ".exists", idjek) > 0;
    }

    public void cleanData(Set<String> jekIds) {
        sqlSession().delete(MAPPING_NAMESPACE + ".delete", ImmutableMap.of("value", jekIds));
    }

    public Long findSystemStreetType(String ukrStreetType, String rusStreetType) {
        Map<String, Object> params = Maps.newHashMap(
                ImmutableMap.<String, Object>of("streetTypeShortNameAT", StreetTypeStrategy.SHORT_NAME));

        params.put("localeId", Utils.UKRAINIAN_LOCALE_ID);
        params.put("name", trimTrailingDot(ukrStreetType));
        List<Long> ids = sqlSession().selectList(MAPPING_NAMESPACE + ".findSystemStreetType", params);
        if (ids.isEmpty() || ids.size() > 1) {
            params.put("name", ukrStreetType);
            ids = sqlSession().selectList(MAPPING_NAMESPACE + ".findSystemStreetType", params);
            if (ids.isEmpty() || ids.size() > 1) {
                params.put("localeId", Utils.RUSSIAN_LOCALE_ID);
                params.put("name", trimTrailingDot(rusStreetType));
                ids = sqlSession().selectList(MAPPING_NAMESPACE + ".findSystemStreetType", params);
                if (ids.isEmpty() || ids.size() > 1) {
                    params.put("name", rusStreetType);
                    ids = sqlSession().selectList(MAPPING_NAMESPACE + ".findSystemStreetType", params);
                }
            }
        }
        return ids.size() == 1 ? ids.get(0) : null;
    }

    public Long findSystemStreet(long cityId, long streetTypeId, String ukrName, String rusName) {
        Map<String, Object> params = Maps.newHashMap(
                ImmutableMap.<String, Object>of(
                "streetTypeAT", StreetStrategy.STREET_TYPE,
                "streetNameAT", StreetStrategy.NAME,
                "streetTypeId", streetTypeId,
                "cityId", cityId));

        params.put("localeId", Utils.RUSSIAN_LOCALE_ID);
        params.put("name", rusName);
        List<Long> ids = sqlSession().selectList(MAPPING_NAMESPACE + ".findSystemStreet", params);
        if (ids.isEmpty() || ids.size() > 1) {
            params.put("localeId", Utils.UKRAINIAN_LOCALE_ID);
            params.put("name", ukrName);
            ids = sqlSession().selectList(MAPPING_NAMESPACE + ".findSystemStreet", params);
        }
        return ids.size() == 1 ? ids.get(0) : null;
    }

    private static String trimTrailingDot(String value) {
        if (value != null && value.endsWith(".")) {
            return value.substring(0, value.length() - 1);
        }
        return value;
    }
}
