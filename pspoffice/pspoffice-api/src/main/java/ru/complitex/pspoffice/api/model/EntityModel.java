package ru.complitex.pspoffice.api.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author Anatoly A. Ivanov
 * 22.09.2017 16:12
 */
public class EntityModel implements Serializable{
    private Long id;
    private String entity;
    private Map<String, String> names;
    private List<EntityAttributeModel> attributes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }


    public Map<String, String> getNames() {
        return names;
    }

    public void setNames(Map<String, String> names) {
        this.names = names;
    }

    public List<EntityAttributeModel> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<EntityAttributeModel> attributes) {
        this.attributes = attributes;
    }
}
