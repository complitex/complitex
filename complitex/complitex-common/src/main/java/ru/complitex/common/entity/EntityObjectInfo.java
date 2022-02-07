package ru.complitex.common.entity;

/**
* @author Anatoly Ivanov
*         Date: 003 03.07.14 17:36
*/
public class EntityObjectInfo {

    private String entityName;
    private Long id;

    public EntityObjectInfo(String entityName, Long id) {
        this.entityName = entityName;
        this.id = id;
    }

    public String getEntityName() {
        return entityName;
    }

    public Long getId() {
        return id;
    }
}
