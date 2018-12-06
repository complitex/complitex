package org.complitex.common.web;

import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.util.string.Strings;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.entity.FilterWrapper;
import org.complitex.common.entity.Preference;
import org.complitex.common.entity.PreferenceKey;
import org.complitex.common.strategy.StrategyFactory;
import org.complitex.common.strategy.StringLocaleBean;
import org.complitex.common.util.EjbBeanLocator;
import org.complitex.common.web.component.search.SearchComponentState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 *
 * @author Artem
 */
public class DictionaryFwSession extends WebSession {
    private final Logger log = LoggerFactory.getLogger(DictionaryFwSession.class);

    public final static String GLOBAL_PAGE = "global";
    public final static String LOCALE_KEY = "locale";
    public final static String GLOBAL_STATE_PAGE = "global#search_component_state";
    public final static String GLOBAL_STATE_KEY = "SEARCH_COMPONENT_STATE";
    public final static String DEFAULT_STATE_PAGE = "default#search_component_state";
    public final static String IS_USE_DEFAULT_STATE_KEY = "is_use_default_search_component_state";
    private Map<String, SearchComponentState> searchComponentSessionState = new HashMap<String, SearchComponentState>();
    private Map<String, Map<String, Preference>> preferences = new HashMap<String, Map<String, Preference>>();
    private ISessionStorage sessionStorage;
    private DomainObject mainUserOrganization;

    public DictionaryFwSession(Request request, ISessionStorage sessionStorage) {
        super(request);

        this.sessionStorage = sessionStorage;

        List<Preference> list = sessionStorage.load();

        for (Preference p : list) {
            putPreference(p.getPage(), p.getKey(), p);
        }

        //locale
        final String language = getPreferenceString(GLOBAL_PAGE, LOCALE_KEY);
        final StringLocaleBean stringLocaleBean = EjbBeanLocator.getBean(StringLocaleBean.class);
        super.setLocale(language != null ? new Locale(language) : stringLocaleBean.getSystemLocale());
    }

    public Map<String, Preference> getPreferenceMap(String page) {
        Map<String, Preference> map = preferences.get(page);

        if (map == null) {
            map = new HashMap<>();
            preferences.put(page, map);
        }

        return map;
    }

    public void putPreference(String page, String key, Preference value) {
        getPreferenceMap(page).put(key, value);
    }

    public Preference putPreference(String page, String key, String value, Object object, boolean store) {
        Preference preference = getPreferenceMap(page).get(key);

        if (preference == null) {
            preference = new Preference(sessionStorage.getUserId(), page, key, value, object);
            putPreference(page, key, preference);

            if (store) {
                sessionStorage.save(preference);
            }
        } else if ((value == null && preference.getValue() != null)
                || (value != null && !value.equals(preference.getValue()))) {

            preference.setValue(value);

            if (store) {
                sessionStorage.save(preference);
            }

            if (value == null) {
                getPreferenceMap(page).remove(key);
            }
        }

        preference.setObject(object);

        return preference;
    }

    public void putPreference(String page, String key, String value, boolean store) {
        putPreference(page, key, value, null, store);
    }

    public void putPreference(String page, Enum key, String value, boolean store) {
        putPreference(page, key.name(), value, null, store);
    }

    public void putPreference(String page, Enum key, Number value, boolean store) {
        putPreference(page, key.name(), value != null ? value.toString() : null, null, store);
    }

    public void putPreference(String page, Enum key, Boolean value, boolean store) {
        putPreference(page, key.name(), value != null ? value.toString() : null, null, store);
    }

    public void putPreferenceObject(String page, Enum key, Object object) {
        putPreference(page, key.name(), null, object, false);
    }

    public void storeGlobalSearchComponentState() {
        SearchComponentState state = searchComponentSessionState.get(GLOBAL_STATE_KEY);

        if (state != null) {
            for (String key : state.keySet()) {
                DomainObject object = state.get(key);
                long objectId = object == null ? SearchComponentState.NOT_SPECIFIED_ID
                        : (object.getObjectId() != null ? object.getObjectId() : SearchComponentState.NOT_SPECIFIED_ID);
                putPreference(GLOBAL_STATE_PAGE, key, String.valueOf(objectId), true);
            }
        }
    }

    public Preference getPreference(String page, String key) {
        Preference preference = getPreferenceMap(page).get(key);

        if (preference == null) {
            preference = putPreference(page, key, null, null, false);
        }

        return preference;
    }

    //String key
    public String getPreferenceString(String page, String key) {
        return getPreference(page, key).getValue();
    }

    public String getPreferenceString(String page, Enum key) {
        return getPreference(page, key.name()).getValue();
    }

    public Integer getPreferenceInteger(String page, String key) {
        try {
            return Integer.valueOf(getPreferenceString(page, key));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public Long getPreferenceLong(String page, String key) {
        try {
            return Long.valueOf(getPreferenceString(page, key));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public Long getPreferenceLong(String page, Enum key) {
        return getPreferenceLong(page, key.name());
    }

    public Boolean getPreferenceBoolean(String page, String key) {
        final String preferenceString = getPreferenceString(page, key);
        return !Strings.isEmpty(preferenceString) ? Boolean.valueOf(preferenceString) : null;
    }

    //Enum key
    public Preference getPreference(String page, Enum key) {
        return getPreference(page, key.name());
    }

    private <T> T getNotNullOrDefault(T object, T _default) {
        return object != null ? object : _default;
    }

    @SuppressWarnings("unchecked")
    public <T> T getPreferenceObject(String page, Enum key, T _default) {
        T object = (T) getPreference(page, key).getObject();

        if (object == null){
            object = _default;

            getPreference(page, key).setObject(object);
        }

        return object;
    }

    @SuppressWarnings("unchecked")
    public <T extends Serializable> FilterWrapper<T> getPreferenceFilter(String page, FilterWrapper<T> _default) {
        FilterWrapper<T> filterWrapper =  (FilterWrapper<T>) getPreference(page, PreferenceKey.FILTER_OBJECT.name()).getObject();

        if (filterWrapper == null){
            filterWrapper = _default;

            getPreference(page, PreferenceKey.FILTER_OBJECT.name()).setObject(filterWrapper);
        }

        return filterWrapper;
    }

    public String getPreferenceString(String page, Enum key, String _default) {
        return getNotNullOrDefault(getPreferenceString(page, key.name()), _default);
    }

    public Integer getPreferenceInteger(String page, Enum key, Integer _default) {
        return getNotNullOrDefault(getPreferenceInteger(page, key.name()), _default);
    }

    public Long getPreferenceLong(String page, Enum key, Long _default) {
        return getNotNullOrDefault(getPreferenceLong(page, key.name()), _default);
    }

    public Boolean getPreferenceBoolean(String page, Enum key, Boolean _default) {
        return getNotNullOrDefault(getPreferenceBoolean(page, key.name()), _default);
    }

    //Component state
    @SuppressWarnings({"ConstantConditions"})
    public SearchComponentState getGlobalSearchComponentState() {
        SearchComponentState componentState = searchComponentSessionState.get(GLOBAL_STATE_KEY);

        //load state
        if (componentState == null) {
            componentState = new SearchComponentState();

            Boolean useDefault = getPreferenceBoolean(GLOBAL_PAGE, IS_USE_DEFAULT_STATE_KEY);
            if (useDefault == null) {
                useDefault = false;
            }

            for (Preference p : getPreferenceMap(useDefault ? DEFAULT_STATE_PAGE : GLOBAL_STATE_PAGE).values()) {
                componentState.put(p.getKey(), getPreferenceDomainObject(p.getPage(), p.getKey()));
            }

            searchComponentSessionState.put(GLOBAL_STATE_KEY, componentState);
        }

        return componentState;
    }

    public DomainObject getPreferenceDomainObject(String page, String key) {
        Preference p = getPreference(page, key);

        if (p != null) {
            try {
                Long domainObjectId = Long.valueOf(p.getValue());
                if (domainObjectId != null && domainObjectId > 0) {
                    final StrategyFactory strategyFactory = EjbBeanLocator.getBean(StrategyFactory.class);
                    return strategyFactory.getStrategy(p.getKey()).getDomainObject(domainObjectId, true);
                }
            } catch (Exception e) {
                //wtf
            }
        }

        return null;
    }

    public Preference getOrCreatePreference(String page, String key) {
        Preference preference = getPreference(page, key);

        if (preference == null) {
            preference = new Preference(sessionStorage.getUserId(), page, key);
        }

        return preference;
    }

    @Override
    public Locale getLocale() {
        if (getPreferenceString(GLOBAL_PAGE, LOCALE_KEY) == null) {
            final StringLocaleBean stringLocaleBean = EjbBeanLocator.getBean(StringLocaleBean.class);
            setLocale(stringLocaleBean.getSystemLocale());
        }
        return super.getLocale();
    }

    @Override
    public Session setLocale(Locale locale) {
        putPreference(GLOBAL_PAGE, LOCALE_KEY, locale.getLanguage(), true);
        return super.setLocale(locale);
    }

    public DomainObject getMainUserOrganization() {
        return mainUserOrganization;
    }

    public void setMainUserOrganization(DomainObject mainUserOrganization) {
        this.mainUserOrganization = mainUserOrganization;
    }

    @Override
    public void invalidate() {
        super.invalidate();

        try {
            ((HttpServletRequest) RequestCycle.get().getRequest().getContainerRequest()).logout();
        } catch (ServletException e) {
            log.error("Couldn't to log out user.", e);
        }
    }
}
