/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.complitex.pspoffice.importing.legacy.service;

import ru.complitex.common.entity.Attribute;
import ru.complitex.common.entity.StringValue;
import ru.complitex.common.strategy.StringLocaleBean;
import ru.complitex.common.util.EjbBeanLocator;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Artem
 */
public class Utils {

    public static final int RUSSIAN_LOCALE_ID = 1;
    public static final int UKRAINIAN_LOCALE_ID = 2;
    public static final String NULL_REFERENCE = "[null]";
    public static final String DATE_PATTERN = "dd.MM.yyyy";
    public static final String NONARCHIVE_INDICATOR = "0";
    private static final DateFormat DATE_FORMATTER = new SimpleDateFormat(DATE_PATTERN);

    private Utils() {
    }

    public static String displayDate(Date date) {
        return DATE_FORMATTER.format(date);
    }

    public static void setValue(List<StringValue> values, long localeId, String value) {
        for (StringValue v : values) {
            if (v.getLocaleId().equals(localeId)) {
                v.setValue(value);
            }
        }
    }

    public static void setValue(Attribute attribute, long localeId, String value) {
        setValue(attribute.getStringValues(), localeId, value);
    }

    public static void setValue(Attribute attribute, String value) {
        for (StringValue v : attribute.getStringValues()) {
            v.setValue(value);
        }
    }

    public static void setSystemLocaleValue(Attribute attribute, String value) {
        StringLocaleBean stringLocaleBean = EjbBeanLocator.getBean(StringLocaleBean.class);
        setValue(attribute, stringLocaleBean.getSystemStringLocale().getId(), value);
    }
}
