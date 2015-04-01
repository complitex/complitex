package org.complitex.common.web.component.datatable;

import org.apache.commons.lang.reflect.FieldUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.HeadersToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.TextFilteredPropertyColumn;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.ResourceModel;
import org.complitex.common.entity.FilterWrapper;
import org.complitex.common.web.component.paging.AjaxNavigationToolbar;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Anatoly Ivanov
 *         Date: 001 01.07.14 17:06
 */
public abstract class FilteredDataTable<T extends Serializable> extends Panel implements IFilterBean<T>{
    private FilteredDataProvider<T> provider;

    public FilteredDataTable(String id, Class<T> objectClass, Map<String, IColumn<T, String>> columnMap,
                             List<Action<T>> actions, String... fields) {
        super(id);

        setOutputMarkupId(true);

        provider = new FilteredDataProvider<>(this, objectClass);

        FilterForm<T> form = new FilterForm<>("form", provider);
        add(form);

        List<IColumn<T, String>> columns = new ArrayList<>();

        for (String field : fields){
            IColumn<T, String> column = columnMap != null ? columnMap.get(field) : null;

            if (column == null){
                Field f = FieldUtils.getField(objectClass, field, true);

                column = getColumn(field, f);

                if (column == null){
                    if (f.getType().isEnum()){
                        //noinspection unchecked
                        column = new EnumColumn(new ResourceModel(field), field, f.getType(), getLocale());
                    }else {
                        column = new TextFilteredPropertyColumn<T, FilterWrapper<T>, String>(new ResourceModel(field), field, field);
                    }
                }
            }

            columns.add(column);
        }

        CheckGroup<T> group = new CheckGroup<>("group", getFilterWrapper().getGroup());
        group.add(new AjaxFormChoiceComponentUpdatingBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
            }
        });
        form.add(group);

        DataTable<T, String> table = new DataTable<>("table", columns, provider, 10);
        table.setOutputMarkupId(true);

        columns.add(new FilteredActionColumn<T>(actions != null ? actions : new ArrayList<>()){
            @Override
            protected void onReset(AjaxRequestTarget target) {
                provider.init();
                target.add(table);
            }

            @Override
            protected void onFilter(AjaxRequestTarget target) {
                target.add(table);
            }
        });

        table.addTopToolbar(new HeadersToolbar<>(table, provider));
        table.addTopToolbar(new FilterToolbar(table, form, provider));
        table.addBottomToolbar(new AjaxNavigationToolbar(table));

        group.add(table);
    }

    public FilteredDataTable(String id, Class<T> objectClass, String... fields){
        this(id, objectClass, null, null, fields);
    }

    protected IColumn<T, String> getColumn(String field, Field f){
        return null;
    }

    public FilterWrapper<T> getFilterWrapper(){
        return provider.getFilterWrapper();
    }
}
