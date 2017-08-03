package ru.complitex.pspoffice.api.model;

import java.io.Serializable;
import java.util.Map;

/**
 * @author Anatoly A. Ivanov
 * 03.08.2017 17:42
 */
public class NameObject implements Serializable{
    private Long objectId;
    private Map<String, String> name;

    public NameObject() {
    }

    public NameObject(Long objectId, Map<String, String> name) {
        this.objectId = objectId;
        this.name = name;
    }

    public Long getObjectId() {
        return objectId;
    }

    public void setObjectId(Long objectId) {
        this.objectId = objectId;
    }

    public Map<String, String> getName() {
        return name;
    }

    public void setName(Map<String, String> name) {
        this.name = name;
    }
}
