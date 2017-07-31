package org.complitex.ui.wicket.datatable;

import de.agilecoders.wicket.extensions.markup.html.bootstrap.table.toolbars.BootstrapNavigationToolbar;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackHeadersToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.TextFilteredPropertyColumn;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.ResourceModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 03.07.2017 16:59
 */
public class TablePanel<T extends Serializable> extends Panel {
    public TablePanel(String id, Class<T> modelClass, List<String> modelFields, TableDataProvider<T> dataProvider) {
        super(id);

        FilterForm<T> filterForm = new FilterForm<>("filterForm", dataProvider);
        add(filterForm);

        DataTable<T, String> dataTable = new DataTable<>("dataTable", getColumns(modelClass, modelFields),
                dataProvider, 10);
        dataTable.addTopToolbar(new AjaxFallbackHeadersToolbar<>(dataTable, dataProvider));
        dataTable.addTopToolbar(new FilterToolbar(dataTable, filterForm){
            @Override
            protected void onBeforeRender() {
                super.onBeforeRender();

                visitChildren(TextField.class, (component, visit) ->
                        component.add(new AttributeModifier("class", "form-control input-sm")));
            }
        });
        dataTable.addBottomToolbar(new BootstrapNavigationToolbar(dataTable));

        filterForm.add(dataTable);
    }

    protected IColumn<T, String> getColumn(String field){
        return null;
    }

    private List<IColumn<T, String>> getColumns(Class<T> modelClass, List<String> modelFields){
        List<IColumn<T, String>> columns = new ArrayList<>();

        modelFields.forEach(f -> {
            IColumn<T, String> column = getColumn(f);

            if (column == null){
                column = new TextFilteredPropertyColumn<T, String, String>(new ResourceModel(f), f, f);
            }

            columns.add(column);
        });

        return columns;
    }
}
