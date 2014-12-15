/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.common.entity;

/**
 *
 * @author Artem
 */
public class Parameter {

    private String entityTable;

    private Object object;

    public Parameter(String entityTable, Object parameter) {
        this.entityTable = entityTable;
        this.object = parameter;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object entity) {
        this.object = entity;
    }

    public String getEntityTable() {
        return entityTable;
    }

    public void setEntityTable(String entityTable) {
        this.entityTable = entityTable;
    }
}
