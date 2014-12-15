package org.complitex.common.util;

import org.complitex.common.service.LocaleBean;

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
    private Map<Locale, Long> map = new ConcurrentHashMap<>();

    private static Locales instance = new Locales();

    public Locales() {
        LocaleBean localeBean = EjbBeanLocator.getBean(LocaleBean.class);

        for (org.complitex.common.entity.Locale l : localeBean.getAllLocales()){
            map.put(new Locale(l.getLanguage()), l.getId());

            if (l.isSystem()){
                systemLocaleId = l.getId();
                systemLocale = new Locale(l.getLanguage());
            }
        }
    }

    private static Locales get(){
        return instance;
    }

    public static Locale getSystemLocale() {
        return get().systemLocale;
    }

    public static Long getSystemLocaleId() {
        return get().systemLocaleId;
    }

    public static Long getLocaleId(Locale locale){
        return get().map.get(locale);
    }

    public static Collection<Long> getLocaleIds(){
        return get().map.values();
    }
}
