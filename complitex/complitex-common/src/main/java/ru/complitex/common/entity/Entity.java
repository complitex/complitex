package ru.complitex.common.entity;

import ru.complitex.common.util.StringValueUtil;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

public class Entity implements Serializable{
    private Long id;
    private String entity;
    private List<StringValue> names;

    private List<EntityAttribute> attributes;

    public EntityAttribute getAttribute(Long entityAttributeId) {
        return attributes.stream().filter(a -> entityAttributeId.equals(a.getId()))
                .findFirst()
                .orElseGet(() -> {
                    throw new IllegalArgumentException("entityAttributeId = " + entityAttributeId + " not found");
                });
    }

    public String getName(Locale locale){
        return StringValueUtil.getValue(names, locale);
    }

    public String getName(Long entityAttributeId, Locale locale){
        return StringValueUtil.getValue(getAttribute(entityAttributeId).getNames(), locale);
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String table) {
        this.entity = table;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<EntityAttribute> getAttributes() {
        return attributes;
    }

    public void setEntityAttribute(List<EntityAttribute> entityAttributes) {
        this.attributes = entityAttributes;
    }

    public List<StringValue> getNames() {
        return names;
    }

    public void setNames(List<StringValue> names) {
        this.names = names;
    }


}
