/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.importing.legacy.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.complitex.dictionary.entity.Attribute;
import org.complitex.dictionary.entity.StringCulture;
import org.complitex.dictionary.service.LocaleBean;
import org.complitex.dictionary.util.EjbBeanLocator;

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
        setValue(attribute.getLocalizedValues(), localeId, value);
    }

    public static void setValue(Attribute attribute, String value) {
        for (StringCulture culture : attribute.getLocalizedValues()) {
            culture.setValue(value);
        }
    }

    public static void setSystemLocaleValue(Attribute attribute, String value) {
        LocaleBean localeBean = EjbBeanLocator.getBean(LocaleBean.class);
        setValue(attribute, localeBean.getSystemLocaleObject().getId(), value);
    }
}
