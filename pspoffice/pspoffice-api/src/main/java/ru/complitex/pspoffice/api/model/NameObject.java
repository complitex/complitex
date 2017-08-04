package ru.complitex.pspoffice.api.model;

import java.io.Serializable;
import java.util.Map;

/**
 * @author Anatoly A. Ivanov
 * 03.08.2017 17:42
 */
public class NameObject implements Serializable{
    private Long id;
    private Map<String, String> name;

    public NameObject() {
    }

    public NameObject(Long id, Map<String, String> name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Map<String, String> getName() {
        return name;
    }

    public void setName(Map<String, String> name) {
        this.name = name;
    }
}
