/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.importing.legacy.service;

import org.complitex.common.entity.Attribute;
import org.complitex.common.entity.StringCulture;
import org.complitex.common.strategy.StringLocaleBean;
import org.complitex.common.util.EjbBeanLocator;

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

    public static void setValue(List<StringCulture> values, long localeId, String value) {
        for (StringCulture culture : values) {
            if (culture.getLocaleId().equals(localeId)) {
                culture.setValue(value);
            }
        }
    }

    public static void setValue(Attribute attribute, long localeId, String value) {
        setValue(attribute.getStringCultures(), localeId, value);
    }

    public static void setValue(Attribute attribute, String value) {
        for (StringCulture culture : attribute.getStringCultures()) {
            culture.setValue(value);
        }
    }

    public static void setSystemLocaleValue(Attribute attribute, String value) {
        StringLocaleBean stringLocaleBean = EjbBeanLocator.getBean(StringLocaleBean.class);
        setValue(attribute, stringLocaleBean.getSystemStringLocale().getId(), value);
    }
}
