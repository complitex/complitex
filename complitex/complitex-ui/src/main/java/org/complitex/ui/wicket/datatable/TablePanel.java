package org.complitex.ui.wicket.datatable;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.TextFilteredPropertyColumn;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.ResourceModel;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 03.07.2017 16:59
 */
public abstract class TablePanel<T extends Serializable> extends Panel {
    public TablePanel(String id, Class<T> modelClass, List<String> modelFields) {
        super(id);

        FilterForm<T> filterForm = new FilterForm<>("filterForm", null); //todo filter locator

        DataTable<T, String> dataTable = new DataTable<>("dataTable", getColumns(modelClass, modelFields),
                null, getRowsPerPage()); //todo data locator
    }
    
    protected IColumn<T, String> getColumn(String field){
        return null;
    }
    
    protected long getRowsPerPage(){
        return 10;
    }
    
    private List<IColumn<T, String>> getColumns(Class<T> modelClass, List<String> modelFields){
        List<IColumn<T, String>> columns = new ArrayList<>();

        modelFields.forEach(f -> {
            IColumn<T, String> column = getColumn(f);
            
            if (column == null){
                Field field = FieldUtils.getField(modelClass, f, true);

                if (field.getType().equals(String.class)){
                    column = new TextFilteredPropertyColumn<>(new ResourceModel(f), f, f);
                }
            }
            
            columns.add(column);
        });
        
        return columns;
    }






}
