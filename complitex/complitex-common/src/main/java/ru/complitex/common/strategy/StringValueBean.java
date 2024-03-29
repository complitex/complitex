package ru.complitex.common.strategy;

import com.google.common.collect.ImmutableMap;
import org.apache.wicket.util.string.Strings;
import ru.complitex.common.entity.StringValue;
import ru.complitex.common.mybatis.SqlSessionFactoryBean;
import ru.complitex.common.service.AbstractBean;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            sqlSession().insert(DomainObjectStrategy.NS + ".insertDescriptionData", stringValue);
        } else {
            stringValue.setEntityName(entityName);

            sqlSession().insert(DomainObjectStrategy.NS + ".insertStringValue", stringValue);
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

        return (dataSource == null ? sqlSession() : sqlSession(dataSource)).selectList(DomainObjectStrategy.NS + ".selectStringValues", params);
    }

    public void delete(String entityName, long objectId) {
        Map<String, Object> params = new HashMap<>();
        params.put("entityName", entityName);
        params.put("objectId", objectId);

        sqlSession().delete(DomainObjectStrategy.NS + ".deleteStringValue", params);
    }

    @Override
    public void setSqlSessionFactoryBean(SqlSessionFactoryBean sqlSessionFactoryBean) {
        super.setSqlSessionFactoryBean(sqlSessionFactoryBean);

        stringLocaleBean.setSqlSessionFactoryBean(sqlSessionFactoryBean);
        sequenceBean.setSqlSessionFactoryBean(sqlSessionFactoryBean);
    }
}
