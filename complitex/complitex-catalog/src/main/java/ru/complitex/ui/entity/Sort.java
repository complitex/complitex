package ru.complitex.ui.entity;

import java.io.Serializable;

/**
 * @author Ivanov Anatoliy
 */
public class Sort implements Serializable {
    private String key;
    private Object object;
    private boolean ascending;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public boolean isAscending() {
        return ascending;
    }

    public void setAscending(boolean ascending) {
        this.ascending = ascending;
    }
}
