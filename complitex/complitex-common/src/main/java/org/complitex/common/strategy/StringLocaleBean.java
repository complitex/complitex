package org.complitex.common.strategy;

import org.complitex.common.entity.StringLocale;
import org.complitex.common.service.AbstractBean;

import javax.annotation.PostConstruct;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class StringLocaleBean extends AbstractBean {
    private ConcurrentHashMap<Long, StringLocale> idTolocaleMap = new ConcurrentHashMap<Long, StringLocale>();
    private ConcurrentHashMap<java.util.Locale, StringLocale> localesMap = new ConcurrentHashMap<java.util.Locale, StringLocale>();
    private StringLocale systemStringLocale;
    private java.util.Locale systemLocale;

    @PostConstruct
    private void init() {
        for (StringLocale stringLocale : loadAllLocales()) {
            idTolocaleMap.put(stringLocale.getId(), stringLocale);

            java.util.Locale l = new java.util.Locale(stringLocale.getLanguage());

            localesMap.put(l, stringLocale);

            if(stringLocale.isSystem()){
                systemStringLocale = stringLocale;
                systemLocale = l;
            }
        }
    }

    public Collection<StringLocale> getAllLocales() {
        return idTolocaleMap.values();
    }

    public StringLocale convert(java.util.Locale locale) {
        if (locale == null){
            return systemStringLocale;
        }

        return localesMap.get(locale);
    }

    public java.util.Locale convert(StringLocale stringLocale) {
        if (stringLocale == null){
            return systemLocale;
        }

        for(Entry<java.util.Locale, StringLocale> entry : localesMap.entrySet()){
            if(entry.getValue().getId().equals(stringLocale.getId())){
                return entry.getKey();
            }
        }

        return systemLocale;
    }

    public StringLocale getLocaleObject(Long localeId) {
        return idTolocaleMap.get(localeId);
    }

    public java.util.Locale getLocale(Long localeId) {
        if (localeId == null){
            return systemLocale;
        }

        return convert(getLocaleObject(localeId));
    }


    protected List<StringLocale> loadAllLocales() {
        return sqlSession().selectList(DomainObjectStrategy.NS + ".loadAllLocales");
    }

    public StringLocale getSystemStringLocale() {
        return systemStringLocale;
    }

    public java.util.Locale getSystemLocale(){
        return systemLocale;
    }

    public Long getSystemLocaleId(){
        return systemStringLocale.getId();
    }
}
