package org.complitex.common.util;

import com.google.common.collect.Lists;
import org.complitex.common.entity.StringValue;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * @author inheaven on 015 15.12.14 16:37.
 */
public class StringValueUtil {
    private static class StringValueComparator implements Comparator<StringValue> {

        private final Long systemLocaleId;

        StringValueComparator(Long systemLocaleId) {
            this.systemLocaleId = systemLocaleId;
        }

        @Override
        public int compare(StringValue o1, StringValue o2) {
            if (o1.getLocaleId().equals(systemLocaleId)) {
                return -1;
            }

            if (o2.getLocaleId().equals(systemLocaleId)) {
                return 1;
            }

            return o1.getLocaleId().compareTo(o2.getLocaleId());
        }
    }

    private static StringValueComparator stringValueComparator = new StringValueComparator(Locales.getSystemLocaleId());

    public static StringValue getSystemStringValue(List<StringValue> strings) {
        Long systemLocaleId = Locales.getSystemLocaleId();

        for (StringValue string : strings) {
            if (systemLocaleId.equals(string.getLocaleId())) {
                return string;
            }
        }

        throw new IllegalArgumentException("System string value not found");
    }

    public static String getValue(List<StringValue> stringValues, Locale locale) {
        Long localeId = Locales.getLocaleId(locale);

        for (StringValue stringValue : stringValues){
            if (localeId.equals(stringValue.getLocaleId())){
                return stringValue.getValue();
            }
        }

        return getSystemStringValue(stringValues).getValue();
    }

    public static List<StringValue> newStringValues() {
        List<StringValue> stringValues = Lists.newArrayList();

        for (Long localeId : Locales.getLocaleIds()){
            stringValues.add(new StringValue(localeId));
        }

        //Collections.sort(stringValues, stringValueComparator);

        return stringValues;
    }

    public static void updateForNewLocales(List<StringValue> stringValues) {
        for (Long localeId : Locales.getLocaleIds()){
            boolean found = false;

            for (StringValue stringValue : stringValues){
                if (localeId.equals(stringValue.getLocaleId())){
                    found = true;
                    break;
                }
            }

            if (!found){
                stringValues.add(new StringValue(localeId));
            }
        }

        //Collections.sort(stringValues, stringValueComparator);
    }
}
