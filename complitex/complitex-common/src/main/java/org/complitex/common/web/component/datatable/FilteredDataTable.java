package org.complitex.common.web.component.datatable;

import org.apache.commons.lang.reflect.FieldUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.HeadersToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterToolbar;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.util.ListModel;
import org.complitex.common.entity.FilterWrapper;
import org.complitex.common.web.component.datatable.column.DateColumn;
import org.complitex.common.web.component.datatable.column.EditableColumn;
import org.complitex.common.web.component.datatable.column.EnumColumn;
import org.complitex.common.web.component.datatable.column.FilteredActionColumn;
import org.complitex.common.web.component.paging.AjaxNavigationToolbar;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Anatoly Ivanov
 *         Date: 001 01.07.14 17:06
 */
public abstract class FilteredDataTable<T extends Serializable> extends Panel implements IFilterBean<T>{
    private FilteredDataProvider<T> provider;
    private IModel<List<T>> checkGroupModel = new ListModel<>(new ArrayList<>());
    private IModel<T> radioGroupModel = Model.of();

    public FilteredDataTable(String id, Class<T> objectClass, Map<String, IColumn<T, String>> columnMap,
                             List<Action<T>> actions, boolean filter, String... fields) {
        super(id);

        setOutputMarkupId(true);

        provider = new FilteredDataProvider<T>(this, objectClass){
            @Override
            protected void onInit(FilterWrapper<T> filterWrapper) {
                FilteredDataTable.this.onInit(filterWrapper);
            }
        };

        FilterForm<T> form = new FilterForm<T>("form", provider){
            @Override
            public void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag) {
                try {
                    super.onComponentTagBody(markupStream, openTag);
                } catch (Exception e) {
                    //UrlRequestParametersAdapter.java:48 url npe
                }
            }
        };
        add(form);

        List<IColumn<T, String>> columns = new ArrayList<>();

        for (String field : fields){
            IColumn<T, String> column = columnMap != null ? columnMap.get(field) : null;

            if (column == null){
                column = onColumn(field);
            }

            if (column == null){
                Field f = FieldUtils.getField(objectClass, field, true);

                if (f.getType().equals(Date.class)){
                    column = new DateColumn<>(new ResourceModel(field), field, field);
                }else if (f.getType().isEnum()){
                    //noinspection unchecked
                    column = new EnumColumn(new ResourceModel(field), field, f.getType(), getLocale());
                }else {
                    column = new EditableColumn<T>(field) {
                        @Override
                        protected boolean isEdit(String propertyExpression, IModel<T> rowModel) {
                            return FilteredDataTable.this.isEdit(propertyExpression, rowModel);
                        }
                    };
                }
            }

            columns.add(column);
        }

        RadioGroup<T> radioGroup = new RadioGroup<>("radioGroup", radioGroupModel);
        radioGroup.add(new AjaxFormChoiceComponentUpdatingBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
            }
        });
        form.add(radioGroup);

        CheckGroup<T> checkGroup = new CheckGroup<>("checkGroup", checkGroupModel);
        checkGroup.add(new AjaxFormChoiceComponentUpdatingBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
            }
        });
        radioGroup.add(checkGroup);

        DataTable<T, String> table = new DataTable<>("table", columns, provider, 10);
        table.setOutputMarkupId(true);

        if (filter) {
            columns.add(new FilteredActionColumn<T>(actions != null ? actions : new ArrayList<>()) {
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
        }

        table.addTopToolbar(new HeadersToolbar<>(table, provider));

        if (filter) {
            table.addTopToolbar(new FilterToolbar(table, form, provider));
        }

        table.addBottomToolbar(new AjaxNavigationToolbar(table));

        table.setTableBodyCss("noWrap");

        checkGroup.add(table);
    }

    public FilteredDataTable(String id, Class<T> objectClass, Map<String, IColumn<T, String>> columnMap,
                             List<Action<T>> actions, String... fields){
        this(id, objectClass, columnMap, actions, true, fields);
    }

    public FilteredDataTable(String id, Class<T> objectClass, boolean filter, String... fields){
        this(id, objectClass, null, null, filter, fields);
    }

    public FilteredDataTable(String id, Class<T> objectClass, String... fields){
        this(id, objectClass, null, null, fields);
    }

    protected IColumn<T, String> onColumn(String field){
        return null;
    }

    public FilterWrapper<T> getFilterWrapper(){
        return provider.getFilterWrapper();
    }

    public IModel<List<T>> getCheckGroupModel() {
        return checkGroupModel;
    }

    public IModel<T> getRadioGroupModel() {
        return radioGroupModel;
    }

    protected void onInit(FilterWrapper<T> filterWrapper){
    }

    protected boolean isEdit(String field, IModel<T> rowModel){
        return false;
    }
}
