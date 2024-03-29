package ru.complitex.common.util;

import ru.complitex.common.entity.StringLocale;
import ru.complitex.common.strategy.StringLocaleBean;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * @author Anatoly Ivanov
 *         Date: 004 04.08.14 10:45
 */
public class Locales {
    private Long systemLocaleId;
    private Locale systemLocale;

    private Long alternativeLocaleId;
    private Locale alternativeLocale;

    private Map<Locale, Long> map = new ConcurrentHashMap<>();
    private Map<Long, Locale> mapId = new ConcurrentHashMap<>();

    private static Locales instance = new Locales();

    public static final Locale RU = new Locale("ru");
    public static final Locale UA = new Locale("uk");

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
                alternativeLocaleId = l.getId();
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

    public static Long getAlternativeLocaleId(){
        return instance.alternativeLocaleId;
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

    public static <T, U extends Comparable<? super U>> Comparator<T> comparing(
            Function<? super T, ? extends U> keyExtractor, U first)
    {
        Objects.requireNonNull(keyExtractor);
        return (Comparator<T> & Serializable)
                (c1, c2) -> {
                    if (keyExtractor.apply(c1).equals(first)){
                        return -1;
                    }

                    if (keyExtractor.apply(c2).equals(first)){
                        return 1;
                    }

                    return keyExtractor.apply(c1).compareTo(keyExtractor.apply(c2));
                };
    }
}
