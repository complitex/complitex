package org.complitex.entity;

import java.util.*;

/**
 * @author Anatoly A. Ivanov
 * 21.09.2017 17:08
 */
public class Locales {
    public static final Locale RU = new Locale("ru");
    public static final Locale UA = new Locale("uk");

    private static Map<Locale, Long> map = new HashMap<Locale, Long>(){{
        put(RU, 1L);
        put(UA, 2L);
    }};

    private static Map<Long, Locale> mapId = new HashMap<Long, Locale>(){{
        put(1L, RU);
        put(2L, UA);
    }};

    public static Long getLocaleId(Locale locale){
        return map.get(locale);
    }

    public static Locale getLocale(Long localeId){
        return mapId.get(localeId);
    }

    public static Long getSystemLocaleId(){
        return 1L;
    }

    public static List<Long> getLocaleIds(){
        return Arrays.asList(1L, 2L);
    }
}
