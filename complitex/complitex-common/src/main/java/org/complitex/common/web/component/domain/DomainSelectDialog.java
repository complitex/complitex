package org.complitex.common.web.component.domain;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.complitex.common.entity.DomainObject;
import org.odlabs.wiquery.ui.dialog.Dialog;

/**
 * @author inheaven on 004 04.12.14 17:34.
 */
public class DomainSelectDialog extends Panel{
    private Dialog dialog;

    private IModel<DomainObject> model;

    public DomainSelectDialog(String id, String entityTable) {
        super(id);

        dialog = new Dialog("dialog");
        dialog.setWidth(632);
        add(dialog);

        DomainObjectListPanel objects = new DomainObjectListPanel("objects", entityTable, null, true){
            @Override
            protected void onSelect(AjaxRequestTarget target, DomainObject domainObject) {
                model.setObject(domainObject);
                dialog.close(target);

                DomainSelectDialog.this.onSelect(target);
            }

            @Override
            protected void onCancel(AjaxRequestTarget target) {
                dialog.close(target);
            }
        };
        dialog.add(objects);
    }

    public void open(AjaxRequestTarget target, IModel<DomainObject> model){
        this.model = model;

        dialog.open(target);
    }

    protected void onSelect(AjaxRequestTarget target){
    }
}
