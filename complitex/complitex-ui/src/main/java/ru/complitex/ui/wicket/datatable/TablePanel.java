package ru.complitex.ui.wicket.datatable;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapBookmarkablePageLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.GlyphIconType;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.table.toolbars.BootstrapNavigationToolbar;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Page;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackHeadersToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.TextFilteredPropertyColumn;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.ui.wicket.datatable.column.EditColumn;
import ru.complitex.ui.wicket.link.LinkPanel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 03.07.2017 16:59
 */
public class TablePanel<T extends Serializable> extends Panel {
    private Class<? extends Page> editPageClass;

    private List<String> modelFields;

    public TablePanel(String id, List<String> modelFields, TableDataProvider<T> dataProvider, Class<? extends Page> editPageClass) {
        super(id);

        this.editPageClass = editPageClass;
        this.modelFields = modelFields;

        FilterForm<T> filterForm = new FilterForm<>("filterForm", dataProvider);
        add(filterForm);

        DataTable<T, String> dataTable = new DataTable<>("dataTable", getColumns(), dataProvider, 10);
        dataTable.addTopToolbar(new AjaxFallbackHeadersToolbar<>(dataTable, dataProvider));

        if (isShowFilter()) {
            dataTable.addTopToolbar(new FilterToolbar(dataTable, filterForm){
                @Override
                protected void onBeforeRender() {
                    super.onBeforeRender();

                    visitChildren(TextField.class, (component, visit) ->
                            component.add(new AttributeModifier("class", "form-control")));
                }
            });
        }

        dataTable.addBottomToolbar(new BootstrapNavigationToolbar(dataTable));

        filterForm.add(dataTable);
    }

    public TablePanel(String id, List<String> modelFields, TableDataProvider<T> dataProvider){
        this(id, modelFields, dataProvider, null);
    }

    public TablePanel(String id, TableDataProvider<T> dataProvider){
        this(id, null, dataProvider, null);
    }

    protected IColumn<T, String> getColumn(String field){
        return null;
    }

    protected List<IColumn<T, String>> getColumns(){
        List<IColumn<T, String>> columns = new ArrayList<>();

        if (modelFields != null) {
            modelFields.forEach(f -> {
                IColumn<T, String> column = getColumn(f);

                if (column == null){
                    column = new TextFilteredPropertyColumn<T, String, String>(new ResourceModel(f), f, f);
                }

                columns.add(column);
            });
        }

        if (editPageClass != null){
            columns.add(new EditColumn<T>(){
                @Override
                public void populateItem(Item<ICellPopulator<T>> cellItem, String componentId, IModel<T> rowModel) {
                    PageParameters pageParameters = new PageParameters();

                    populateEdit(rowModel, pageParameters);

                    cellItem.add(new LinkPanel(componentId, new BootstrapBookmarkablePageLink(LinkPanel.LINK_COMPONENT_ID,
                            editPageClass,pageParameters, Buttons.Type.Menu)
                            .setIconType(GlyphIconType.edit).setSize(Buttons.Size.Small)));
                }
            });

        }

        return columns;
    }

    protected void populateEdit(IModel<T> rowModel, PageParameters pageParameters){

    }

    protected boolean isShowFilter(){
        return true;
    }
}
