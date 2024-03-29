package ru.complitex.common.service;

import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.entity.Preference;
import ru.complitex.common.strategy.StrategyFactory;
import ru.complitex.common.web.component.search.SearchComponentState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.HashMap;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 29.11.10 17:53
 */
@Stateless
public class PreferenceBean extends AbstractBean{
    private Logger log = LoggerFactory.getLogger(getClass());

    private static final String MAPPING_NAMESPACE = PreferenceBean.class.getName();

    @EJB
    private StrategyFactory strategyFactory;

    public Preference getPreference(final Long userId, final String page, final String key){
        return (Preference) sqlSession().selectOne(MAPPING_NAMESPACE + ".selectPreference",
                new HashMap<String, Object>() {{
                    put("userId", userId);
                    put("page", page);
                    put("key", key);
                }});
    }

    public Preference getOrCreatePreference(Long userId, String page, String key){
        Preference preference = getPreference(userId, page, key);

        if (preference == null){
            preference = new Preference(userId, page, key);
        }

        return preference;
    }

    @SuppressWarnings({"unchecked"})
    public List<Preference> getPreferences(Long userId){
        return sqlSession().selectList(MAPPING_NAMESPACE + ".selectPreferences", userId);
    }

    public List<Preference> getPreferences(){
        return sqlSession().selectList(MAPPING_NAMESPACE + ".selectAllPreferences");
    }

    public void save(Preference preference){
        try {
            if (preference.getId() == null){
                sqlSession().insert(MAPPING_NAMESPACE + ".insertPreference", preference);
            } else if (preference.getValue() != null) {
                sqlSession().update(MAPPING_NAMESPACE + ".updatePreference", preference);
            } else {
                sqlSession().delete(MAPPING_NAMESPACE + ".deletePreference", preference);
            }
        } catch (Exception e) {
            log.error("error update preference", e);
        }
    }

    public void save(Long userId, String page, String key, String value){
        Preference preference = getOrCreatePreference(userId, page, key);

        preference.setValue(value);

        save(preference);
    }

    public DomainObject getPreferenceDomainObject(Long userId, String page, String key){
        Preference p = getPreference(userId, page, key);

        if (p != null){
            try {
                Long domainObjectId = Long.valueOf(p.getValue());

                if (!domainObjectId.equals(SearchComponentState.NOT_SPECIFIED_ID)) {
                    return strategyFactory.getStrategy(p.getKey()).getDomainObject(domainObjectId, true);
                }
            } catch (Exception e) {
                //wtf
            }
        }

        return new DomainObject(SearchComponentState.NOT_SPECIFIED_ID);
    }
}
