package org.complitex.common.web.component.domain;

import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.complitex.common.web.component.datatable.Action;
import org.complitex.common.web.component.datatable.FilteredDataTable;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * @author inheaven on 019 19.02.15 19:57
 */
public abstract class DomainObjectFilteredDataTable<T extends Serializable> extends FilteredDataTable<T>{

    public DomainObjectFilteredDataTable(String id, Class<T> objectClass, Map<String, IColumn<T, String>> columnMap, List<Action<T>> actions, String... fields) {
        super(id, objectClass, columnMap, actions, fields);
    }

    public DomainObjectFilteredDataTable(String id, Class<T> objectClass, String... fields) {
        super(id, objectClass, fields);
    }

    protected IColumn<T, String> getColumn(String field, Field f){
        return null; //todo
    }
}
