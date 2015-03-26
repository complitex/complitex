package org.complitex.common.strategy;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.apache.wicket.util.string.Strings;
import org.complitex.common.entity.StringCulture;
import org.complitex.common.mybatis.SqlSessionFactoryBean;
import org.complitex.common.service.AbstractBean;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.complitex.common.strategy.DomainObjectStrategy.NS;

@Stateless
public class StringCultureBean extends AbstractBean {
    @EJB
    private SequenceBean sequenceBean;

    @EJB
    private StringLocaleBean stringLocaleBean;

    public Long save(List<StringCulture> strings, String entityName, boolean upperCase) {
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

            long stringId = sequenceBean.nextStringId(entityName);
            for (StringCulture string : strings) {
                if (!Strings.isEmpty(string.getValue())) {
                    string.setId(stringId);
                    save(string, entityName, upperCase);
                }
            }
            return stringId;
        }
        return null;
    }

    public Long save(List<StringCulture> strings, String entityName) {
        return save(strings, entityName, false);
    }

    protected void save(StringCulture stringCulture, String entityName, boolean upperCase) {
        //if string should be in upper case:
        if (upperCase) {
            //find given string culture's locale
            final java.util.Locale locale = stringLocaleBean.getLocale(stringCulture.getLocaleId());

            //upper case string culture's value
            stringCulture.setValue(stringCulture.getValue().toUpperCase(locale));
        }

        if (Strings.isEmpty(entityName)) {
            sqlSession().insert(NS + ".insertDescriptionData", stringCulture);
        } else {
            stringCulture.setEntityName(entityName);

            sqlSession().insert(NS + ".insertStringCulture", stringCulture);
        }
    }

    public List<StringCulture> getStringCultures(long id, String entityName) {
        return getStringCultures(null, id, entityName);
    }

    public List<StringCulture> getStringCultures(String dataSource, long id, String entityName) {
        Map<String, Object> params = ImmutableMap.<String, Object>builder().
                put("entityName", entityName).
                put("id", id).
                build();

        return (dataSource == null ? sqlSession() : sqlSession(dataSource)).selectList(NS + ".selectStringCultures", params);
    }

    public void delete(String entityName, long objectId, Set<Long> localizedValueTypeIds) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("entityName", entityName);
        params.put("objectId", objectId);
        params.put("localizedValueTypeIds", localizedValueTypeIds);

        sqlSession().delete(NS + ".deleteStringCulture", params);
    }

    @Override
    public void setSqlSessionFactoryBean(SqlSessionFactoryBean sqlSessionFactoryBean) {
        super.setSqlSessionFactoryBean(sqlSessionFactoryBean);

        stringLocaleBean.setSqlSessionFactoryBean(sqlSessionFactoryBean);
        sequenceBean.setSqlSessionFactoryBean(sqlSessionFactoryBean);
    }
}
