package org.complitex.common.strategy;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.apache.wicket.util.string.Strings;
import org.complitex.common.entity.StringValue;
import org.complitex.common.mybatis.SqlSessionFactoryBean;
import org.complitex.common.service.AbstractBean;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.complitex.common.strategy.DomainObjectStrategy.NS;

@Stateless
public class StringValueBean extends AbstractBean {
    @EJB
    private SequenceBean sequenceBean;

    @EJB
    private StringLocaleBean stringLocaleBean;

    public Long save(List<StringValue> strings, String entityName, boolean upperCase) {
        if (strings != null && !strings.isEmpty()) {
            boolean allValuesAreEmpty = true;
            for (StringValue string : strings) {
                if (!Strings.isEmpty(string.getValue())) {
                    allValuesAreEmpty = false;
                    break;
                }
            }
            if (allValuesAreEmpty) {
                return null;
            }

            long stringId = sequenceBean.nextStringId(entityName);
            for (StringValue string : strings) {
                if (!Strings.isEmpty(string.getValue())) {
                    string.setId(stringId);
                    save(string, entityName, upperCase);
                }
            }
            return stringId;
        }
        return null;
    }

    public Long save(List<StringValue> strings, String entityName) {
        return save(strings, entityName, false);
    }

    protected void save(StringValue stringValue, String entityName, boolean upperCase) {
        //if string should be in upper case:
        if (upperCase) {
            //find given string value's locale
            final java.util.Locale locale = stringLocaleBean.getLocale(stringValue.getLocaleId());

            //upper case string value's value
            stringValue.setValue(stringValue.getValue().toUpperCase(locale));
        }

        if (Strings.isEmpty(entityName)) {
            sqlSession().insert(NS + ".insertDescriptionData", stringValue);
        } else {
            stringValue.setEntity(entityName);

            sqlSession().insert(NS + ".insertStringValue", stringValue);
        }
    }

    public List<StringValue> getStringValues(long id, String entityName) {
        return getStringValues(null, id, entityName);
    }

    public List<StringValue> getStringValues(String dataSource, long id, String entityName) {
        Map<String, Object> params = ImmutableMap.<String, Object>builder().
                put("entityName", entityName).
                put("id", id).
                build();

        return (dataSource == null ? sqlSession() : sqlSession(dataSource)).selectList(NS + ".selectStringValues", params);
    }

    public void delete(String entityName, long objectId, Set<Long> localizedValueTypeIds) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("entityName", entityName);
        params.put("objectId", objectId);
        params.put("localizedValueTypeIds", localizedValueTypeIds);

        sqlSession().delete(NS + ".deleteStringValue", params);
    }

    @Override
    public void setSqlSessionFactoryBean(SqlSessionFactoryBean sqlSessionFactoryBean) {
        super.setSqlSessionFactoryBean(sqlSessionFactoryBean);

        stringLocaleBean.setSqlSessionFactoryBean(sqlSessionFactoryBean);
        sequenceBean.setSqlSessionFactoryBean(sqlSessionFactoryBean);
    }
}
