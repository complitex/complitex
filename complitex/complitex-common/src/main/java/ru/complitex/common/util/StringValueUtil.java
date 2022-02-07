package ru.complitex.common.util;

import ru.complitex.common.entity.StringValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author inheaven on 015 15.12.14 16:37.
 */
public class StringValueUtil {

    public static StringValue getSystemStringValue(List<StringValue> strings) {
        Long systemLocaleId = Locales.getSystemLocaleId();

        for (StringValue string : strings) {
            if (systemLocaleId.equals(string.getLocaleId())) {
                return string;
            }
        }

        return null;
    }

    public static String getValue(List<StringValue> stringValues, Locale locale) {
        Long localeId = Locales.getLocaleId(locale);

        for (StringValue stringValue : stringValues){
            if (localeId.equals(stringValue.getLocaleId())){
                return stringValue.getValue();
            }
        }

        StringValue stringValue = getSystemStringValue(stringValues);

        return stringValue != null ? stringValue.getValue() : null;
    }

    public static List<StringValue> newStringValues() {
        List<StringValue> stringValues = new ArrayList<>();

        for (Long localeId : Locales.getLocaleIds()){
            stringValues.add(new StringValue(localeId));
        }

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
    }
}
