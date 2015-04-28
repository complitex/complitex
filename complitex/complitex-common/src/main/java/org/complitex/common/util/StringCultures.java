package org.complitex.common.util;

import com.google.common.collect.Lists;
import org.complitex.common.entity.StringCulture;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * @author inheaven on 015 15.12.14 16:37.
 */
public class StringCultures {
    private static class StringCultureComparator implements Comparator<StringCulture> {

        private final Long systemLocaleId;

        StringCultureComparator(Long systemLocaleId) {
            this.systemLocaleId = systemLocaleId;
        }

        @Override
        public int compare(StringCulture o1, StringCulture o2) {
            if (o1.getLocaleId().equals(systemLocaleId)) {
                return -1;
            }

            if (o2.getLocaleId().equals(systemLocaleId)) {
                return 1;
            }

            return o1.getLocaleId().compareTo(o2.getLocaleId());
        }
    }

    private static  StringCultureComparator stringCultureComparator = new StringCultureComparator(Locales.getSystemLocaleId());

    public static StringCulture getSystemStringCulture(List<StringCulture> strings) {
        Long systemLocaleId = Locales.getSystemLocaleId();

        for (StringCulture string : strings) {
            if (systemLocaleId.equals(string.getLocaleId())) {
                return string;
            }
        }

        throw new IllegalArgumentException("System string culture not found");
    }

    public static String getValue(List<StringCulture> stringCultures, Locale locale) {
        Long localeId = Locales.getLocaleId(locale);

        for (StringCulture stringCulture : stringCultures){
            if (localeId.equals(stringCulture.getLocaleId())){
                return stringCulture.getValue();
            }
        }

        return getSystemStringCulture(stringCultures).getValue();
    }

    public static List<StringCulture> newStringCultures() {
        List<StringCulture> stringCultures = Lists.newArrayList();

        for (Long localeId : Locales.getLocaleIds()){
            stringCultures.add(new StringCulture(localeId));
        }

        //Collections.sort(stringCultures, stringCultureComparator);

        return stringCultures;
    }

    public static void updateForNewLocales(List<StringCulture> stringCultures) {
        for (Long localeId : Locales.getLocaleIds()){
            boolean found = false;

            for (StringCulture stringCulture : stringCultures){
                if (localeId.equals(stringCulture.getLocaleId())){
                    found = true;
                    break;
                }
            }

            if (!found){
                stringCultures.add(new StringCulture(localeId));
            }
        }

        //Collections.sort(stringCultures, stringCultureComparator);
    }
}
