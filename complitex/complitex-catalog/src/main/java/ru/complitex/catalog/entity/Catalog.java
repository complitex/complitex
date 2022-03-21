package ru.complitex.catalog.entity;

import java.util.List;
import java.util.Objects;

/**
 * @author Ivanov Anatoliy
 */
public class Catalog extends KeyEntity {
    private List<Value> values;

    public Value getValue(int value, Integer locale) {
        Value val = null;

        for (Value v : values) {
            if (v.getKeyId() == value && (v.getLocale() == null || Objects.equals(v.getLocale().getKeyId(), locale))) {
                if (val != null) {
                    throw new IllegalStateException("value not unique");
                }

                val = v;
            }
        }

        if (val == null) {
            throw new IllegalStateException("value not found");
        }

        return val;
    }

    public Value getValue(int value) {
        return getValue(value, null);
    }

    public List<Value> getValues() {
        return values;
    }

    public void setValues(List<Value> values) {
        this.values = values;
    }

    public String getTable() {
        return getName().toLowerCase();
    }
}
