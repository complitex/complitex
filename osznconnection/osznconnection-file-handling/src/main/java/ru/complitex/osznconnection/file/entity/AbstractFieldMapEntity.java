package ru.complitex.osznconnection.file.entity;

import com.google.common.base.MoreObjects;
import ru.complitex.common.entity.ILongId;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Anatoly A. Ivanov
 * 25.01.2018 17:57
 */
public abstract class AbstractFieldMapEntity<E extends Enum> implements ILongId {
    private Map<String, Object> dbfFields = new TreeMap<>();

    public Object getField(String fieldName) {
        return dbfFields.get(fieldName);
    }

    public void putField(String fieldName, Object object) {
        dbfFields.put(fieldName, object);
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
        Object o = dbfFields.get(e.name() + postfix);

        return o != null ? o.toString().toUpperCase() : null;
    }

    public String getStringField(E e) {
        Object o = dbfFields.get(e.name());

        return o != null ? o.toString() : null;
    }

    public String getStringField(E e, String postfix) {
        Object o = dbfFields.get(e.name() + postfix);

        return o != null ? o.toString() : null;
    }

    public Date getDateField(E e){
        return (Date) dbfFields.get(e.name());
    }

    public Integer getIntegerField(E e){
        return (Integer) dbfFields.get(e.name());
    }

    public BigDecimal getBigDecimalField(E e){
        return (BigDecimal) dbfFields.get(e.name());
    }

    public Map<String, Object> getDbfFields() {
        return dbfFields;
    }

    public void setDbfFields(Map<String, Object> dbfFields) {
        this.dbfFields = dbfFields;
    }

    @Override
    public String toString() {
        return getToStringHelper().toString();
    }

    protected MoreObjects.ToStringHelper getToStringHelper() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("dbfFields", dbfFields);
    }
}
