package ru.complitex.common.util;

import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public final class ResourceUtil {
    private ResourceUtil() {
    }

    public static String getString(Component component, String key) {
        return Application.get().getResourceSettings().getLocalizer().getString(key, component);
    }

    public static String getFormatString(Component component, String key, Object... arguments) {
        return MessageFormat.format(Application.get().getResourceSettings().getLocalizer().getString(key, component), arguments);
    }

    public static String getString(String bundle, String key, Locale locale) {
        try {
            return getResourceBundle(bundle, locale).getString(key);
        } catch (Exception e) {
            return key;
        }
    }

    public static String getString(Class _class, String key, Locale locale) {
        try {
            return getResourceBundle(_class.getName(), locale).getString(key);
        } catch (Exception e) {
            return key;
        }
    }

    public static String getString(Class _class, String key) {
        try {
            return getResourceBundle(_class.getName(), Locales.getSystemLocale()).getString(key);
        } catch (Exception e) {
            return key;
        }
    }

    public static String getFormatString(String bundle, String key, Locale locale, Object... parameters) {
        return MessageFormat.format(getResourceBundle(bundle, locale).getString(key), parameters);
    }

    public static ResourceBundle getResourceBundle(String bundle, Locale locale) {
        try {
            return ResourceBundle.getBundle(bundle, locale);
        } catch (MissingResourceException e) {
            LoggerFactory.getLogger(ResourceUtil.class)
                    .warn("Couldn't to find resource bundle. Bundle : '{}', Locale : '{}'", bundle, locale);
            throw e;
        }
    }
}
