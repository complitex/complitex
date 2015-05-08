package org.complitex.common.entity;

import org.complitex.common.util.StringCultures;

import java.util.List;
import java.util.Locale;

public class Entity implements IEntity {
    private Long id;
    private String table;
    private List<StringCulture> names;
    private List<AttributeType> attributeTypes;

    public AttributeType getAttributeType(Long attributeTypeId) {
        for (AttributeType attributeType : getAttributeTypes()) {
            if (attributeType.getId().equals(attributeTypeId)) {
                return attributeType;
            }
        }
        throw new IllegalArgumentException("attributeTypeId = " + attributeTypeId + " not found");
    }

    public String getName(Locale locale){
        return StringCultures.getValue(names, locale);
    }

    public String getName(Long attributeTypeId, Locale locale){
        return StringCultures.getValue(getAttributeType(attributeTypeId).getAttributeNames(), locale);
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

    public List<AttributeType> getAttributeTypes() {
        return attributeTypes;
    }

    public void setAttributeType(List<AttributeType> attributeTypes) {
        this.attributeTypes = attributeTypes;
    }

    public List<StringCulture> getNames() {
        return names;
    }

    public void setNames(List<StringCulture> names) {
        this.names = names;
    }


}
