package org.complitex.common.strategy.web;

import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.IFilterStateLocator;
import org.apache.wicket.markup.html.panel.Panel;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.entity.description.Entity;
import org.complitex.common.service.EntityBean;
import org.odlabs.wiquery.ui.dialog.Dialog;

import javax.ejb.EJB;

/**
 * @author inheaven on 004 04.12.14 17:34.
 */
public class DomainObjectSelectDialog extends Panel{
    @EJB
    private EntityBean entityBean;

    private Dialog dialog;

    public DomainObjectSelectDialog(String id, String entityTable) {
        super(id);

        dialog = new Dialog("dialog");
        add(dialog);

        Entity entity = entityBean.getEntity(entityTable);

        FilterForm<DomainObject> filterForm = new FilterForm<>("filterForm", new IFilterStateLocator<DomainObject>() {
            @Override
            public DomainObject getFilterState() {
                return null;
            }

            @Override
            public void setFilterState(DomainObject state) {

            }
        });









    }
}
