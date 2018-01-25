package org.complitex.osznconnection.file.entity;

import com.google.common.base.MoreObjects;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Anatoly A. Ivanov
 * 25.01.2018 17:57
 */
public class AbstractFieldMapEntity<E extends Enum> {
    private Map<String, Object> fieldMap = new TreeMap<>();

    public Object getField(String fieldName) {
        return fieldMap.get(fieldName);
    }

    public void putField(String fieldName, Object object) {
        fieldMap.put(fieldName, object);
    }

    public void putField(E e, Object object) {
        putField(e.name(), object);
    }

    public void putField(E e, String postfix, Object object) {
        putField(e.name() + postfix, object);
    }

    public Object getField(E e) {
        return getField(e.name());
    }

    public String getUpStringField(E e, String postfix) {
        Object o = fieldMap.get(e.name() + postfix);

        return o != null ? o.toString().toUpperCase() : null;
    }

    public String getStringField(E e) {
        Object o = fieldMap.get(e.name());

        return o != null ? o.toString() : null;
    }

    public String getStringField(E e, String postfix) {
        Object o = fieldMap.get(e.name() + postfix);

        return o != null ? o.toString() : null;
    }

    public Date getDateField(E e){
        return (Date) fieldMap.get(e.name());
    }

    public Integer getIntegerField(E e){
        return (Integer) fieldMap.get(e.name());
    }

    public BigDecimal getBigDecimalField(E e){
        return (BigDecimal) fieldMap.get(e.name());
    }

    public Map<String, Object> getMap() {
        return fieldMap;
    }

    public void setMap(Map<String, Object> map) {
        this.fieldMap = map;
    }

    @Override
    public String toString() {
        return getToStringHelper().toString();
    }

    protected MoreObjects.ToStringHelper getToStringHelper() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("fieldMap", fieldMap);
    }
}
