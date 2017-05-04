package org.complitex.common.util;

import org.complitex.common.entity.StringLocale;
import org.complitex.common.strategy.StringLocaleBean;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Anatoly Ivanov
 *         Date: 004 04.08.14 10:45
 */
public class Locales {
    private Long systemLocaleId;

    private Locale systemLocale;
    private Locale alternativeLocale;

    private Map<Locale, Long> map = new ConcurrentHashMap<>();
    private Map<Long, Locale> mapId = new ConcurrentHashMap<>();

    private static Locales instance = new Locales();

    public Locales() {
        StringLocaleBean stringLocaleBean = EjbBeanLocator.getBean(StringLocaleBean.class);

        for (StringLocale l : stringLocaleBean.getAllLocales()){
            Locale locale = new Locale(l.getLanguage());

            map.put(locale, l.getId());
            mapId.put(l.getId(), locale);

            if (l.isSystem()){
                systemLocaleId = l.getId();
                systemLocale = new Locale(l.getLanguage());
            }

            if (l.isAlternative()){
                alternativeLocale = new Locale(l.getLanguage());
            }
        }
    }

    private static Locales get(){
        return instance;
    }

    public static Locale getSystemLocale() {
        return instance.systemLocale;
    }

    public static Long getSystemLocaleId() {
        return instance.systemLocaleId;
    }

    public static Locale getAlternativeLocale() {
        return instance.alternativeLocale;
    }

    public static Long getLocaleId(Locale locale){
        return instance.map.get(locale);
    }

    public static Collection<Long> getLocaleIds(){
        return instance.map.values();
    }

    public static Locale getLocale(Long localeId){
        return instance.mapId.get(localeId);
    }

    public static String getLanguage(Long localeId){
        return instance.mapId.get(localeId).getLanguage();
    }
}
