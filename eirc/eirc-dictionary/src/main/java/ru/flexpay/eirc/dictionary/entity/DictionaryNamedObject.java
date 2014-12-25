package ru.flexpay.eirc.dictionary.entity;

import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.complitex.common.entity.StringLocale;

import java.util.Map;

/**
 * @author Pavel Sknar
 */
public class DictionaryNamedObject extends DictionaryObject {

    private Map<StringLocale, String> names = Maps.newHashMap();

    public Map<StringLocale, String> getNames() {
        return names;
    }

    public void setNames(Map<StringLocale, String> names) {
        this.names = names;
    }

    /**
     * MyBatis setter.
     *
     * @param name Content key and value. key is Locale, value is name.
     */
    public void setName(Map<String, Object> name) {
        addName((StringLocale)name.get("key"), (String)name.get("value"));

    }

    public void addName(StringLocale stringLocale, String name) {
        names.put(stringLocale, name);
    }

    public String getName(StringLocale stringLocale) {
        String name = names.get(stringLocale);
        return StringUtils.isNotEmpty(name)? name : "";
    }

    public String getName() {
        return names.size() > 0? names.values().iterator().next() : "";
    }

    public void setNameRu(String name) {
        addName(StringLocale.RU, name);
    }

    public String getNameRu() {
        return getName(StringLocale.RU);
    }

    public void setNameUk(String name) {
        addName(StringLocale.UK, name);
    }

    public String getNameUk() {
        return getName(StringLocale.UK);
    }
}
