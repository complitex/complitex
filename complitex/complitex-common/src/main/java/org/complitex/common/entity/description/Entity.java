package org.complitex.common.entity.description;

import org.complitex.common.entity.StringCulture;
import org.complitex.common.util.StringCultures;

import java.util.List;
import java.util.Locale;

public class Entity implements IEntity {
    private Long id;
    private String table;
    private List<StringCulture> names;
    private List<EntityAttributeType> entityAttributeTypes;

    public EntityAttributeType getAttributeType(Long attributeTypeId) {
        for (EntityAttributeType attributeType : getEntityAttributeTypes()) {
            if (attributeType.getId().equals(attributeTypeId)) {
                return attributeType;
            }
        }
        return null;
    }

    public String getName(Locale locale){
        return StringCultures.getValue(names, locale);
    }

    public String getName(Long attributeTypeId, Locale locale){
        return StringCultures.getValue(getAttributeType(attributeTypeId).getAttributeNames(), locale);
    }

    public String getTable() {
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

    public List<EntityAttributeType> getEntityAttributeTypes() {
        return entityAttributeTypes;
    }

    public void setEntityAttributeType(List<EntityAttributeType> entityAttributeTypes) {
        this.entityAttributeTypes = entityAttributeTypes;
    }

    public List<StringCulture> getNames() {
        return names;
    }

    public void setNames(List<StringCulture> names) {
        this.names = names;
    }


}
