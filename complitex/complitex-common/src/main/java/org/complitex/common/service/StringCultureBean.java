/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.common.service;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.apache.wicket.util.string.Strings;
import org.complitex.common.entity.Parameter;
import org.complitex.common.entity.StringCulture;
import org.complitex.common.mybatis.SqlSessionFactoryBean;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Artem
 */
@Stateless
public class StringCultureBean extends AbstractBean {
    private static final String MAPPING_NAMESPACE = "org.complitex.common.entity.StringCulture";

    @EJB
    private SequenceBean sequenceBean;

    @EJB
    private LocaleBean localeBean;

    public Long insertStrings(List<StringCulture> strings, String entityTable, boolean upperCase) {
        if (strings != null && !strings.isEmpty()) {
            boolean allValuesAreEmpty = true;
            for (StringCulture string : strings) {
                if (!Strings.isEmpty(string.getValue())) {
                    allValuesAreEmpty = false;
                    break;
                }
            }
            if (allValuesAreEmpty) {
                return null;
            }

            long stringId = sequenceBean.nextStringId(entityTable);
            for (StringCulture string : strings) {
                if (!Strings.isEmpty(string.getValue())) {
                    string.setId(stringId);
                    insert(string, entityTable, upperCase);
                }
            }
            return stringId;
        }
        return null;
    }

    /**
     * Inserts strings in upper case by default.
     * @param strings
     * @param entityTable
     * @return String's generated ID.
     */

    public Long insertStrings(List<StringCulture> strings, String entityTable) {
        return insertStrings(strings, entityTable, true);
    }


    protected void insert(StringCulture string, String entityTable, boolean upperCase) {
        //if string should be in upper case:
        if (upperCase) {
            //find given string culture's locale
            final java.util.Locale locale = localeBean.getLocale(string.getLocaleId());

            //upper case string culture's value
            string.setValue(string.getValue().toUpperCase(locale));
        }

        if (Strings.isEmpty(entityTable)) {
            sqlSession().insert(MAPPING_NAMESPACE + ".insertDescriptionData", string);
        } else {
            sqlSession().insert(MAPPING_NAMESPACE + ".insert", new Parameter(entityTable, string));
        }
    }

    public List<StringCulture> findStrings(long id, String entityTable) {
        return findStrings(null, id, entityTable);
    }

    public List<StringCulture> findStrings(String dataSource, long id, String entityTable) {
        Map<String, Object> params = ImmutableMap.<String, Object>builder().
                put("table", entityTable).
                put("id", id).
                build();
        return (dataSource == null? sqlSession() : sqlSession(dataSource)).selectList(MAPPING_NAMESPACE + ".find", params);
    }

    public void delete(String entityTable, long objectId, Set<Long> localizedValueTypeIds) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("table", entityTable);
        params.put("objectId", objectId);
        params.put("localizedValueTypeIds", localizedValueTypeIds);
        sqlSession().delete(MAPPING_NAMESPACE + ".delete", params);
    }

    @Override
    public void setSqlSessionFactoryBean(SqlSessionFactoryBean sqlSessionFactoryBean) {
        super.setSqlSessionFactoryBean(sqlSessionFactoryBean);
        localeBean.setSqlSessionFactoryBean(sqlSessionFactoryBean);
        sequenceBean.setSqlSessionFactoryBean(sqlSessionFactoryBean);
    }
}
