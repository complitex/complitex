package org.complitex.common.web.domain;

import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.HeadersToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterToolbar;
import org.apache.wicket.markup.html.panel.Panel;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.entity.DomainObjectFilter;
import org.complitex.common.entity.Entity;
import org.complitex.common.entity.EntityAttributeType;
import org.complitex.common.strategy.EntityBean;
import org.complitex.common.web.component.paging.AjaxNavigationToolbar;
import org.odlabs.wiquery.ui.dialog.Dialog;

import javax.ejb.EJB;
import java.util.ArrayList;
import java.util.List;

/**
 * @author inheaven on 004 04.12.14 17:34.
 */
public class DomainSelectDialog extends Panel{
    @EJB
    private EntityBean entityBean;

    private Dialog dialog;

    public DomainSelectDialog(String id, String entityTable) {
        super(id);

        dialog = new Dialog("dialog");
        add(dialog);

        Entity entity = entityBean.getEntity(entityTable);

        DomainDataProvider provider = new DomainDataProvider(entityTable);

        FilterForm<DomainObjectFilter> form = new FilterForm<>("form", provider);
        add(form);

        List<IColumn<DomainObject, Long>> columns = new ArrayList<>();

        for (EntityAttributeType entityAttributeType : entity.getEntityAttributeTypes()){
            columns.add(new DomainFilteredColumn(entityTable, entityAttributeType, getLocale()));
        }

        DataTable<DomainObject, Long> table = new DataTable<>("table", columns, provider, 10);
        table.setOutputMarkupId(true);

        table.addTopToolbar(new HeadersToolbar<>(table, provider));
        table.addTopToolbar(new FilterToolbar(table, form, provider));
        table.addBottomToolbar(new AjaxNavigationToolbar(table));

        form.add(table);









    }
}
