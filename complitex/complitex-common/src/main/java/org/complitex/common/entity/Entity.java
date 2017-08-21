package org.complitex.common.entity;

import org.complitex.common.util.StringValueUtil;

import java.util.List;
import java.util.Locale;

public class Entity implements ILongId {
    private Long id;
    private String table;
    private List<StringValue> names;
    private List<EntityAttribute> entityAttributes;

    public EntityAttribute getAttributeType(Long attributeTypeId) {
        for (EntityAttribute entityAttribute : getEntityAttributes()) {
            if (entityAttribute.getId().equals(attributeTypeId)) {
                return entityAttribute;
            }
        }
        throw new IllegalArgumentException("attributeTypeId = " + attributeTypeId + " not found");
    }

    public String getName(Locale locale){
        return StringValueUtil.getValue(names, locale);
    }

    public String getName(Long attributeTypeId, Locale locale){
        return StringValueUtil.getValue(getAttributeType(attributeTypeId).getNames(), locale);
    }

    public String getEntityName() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<EntityAttribute> getEntityAttributes() {
        return entityAttributes;
    }

    public void setAttributeType(List<EntityAttribute> entityAttributes) {
        this.entityAttributes = entityAttributes;
    }

    public List<StringValue> getNames() {
        return names;
    }

    public void setNames(List<StringValue> names) {
        this.names = names;
    }


}
