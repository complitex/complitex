package org.complitex.common.web.component.domain;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.entity.DomainObjectFilter;
import org.odlabs.wiquery.ui.dialog.Dialog;

/**
 * @author inheaven on 004 04.12.14 17:34.
 */
public class DomainSelectDialog extends Panel{
    private Dialog dialog;

    private IModel<Long> model;

    private DomainObjectListPanel objects;

    public DomainSelectDialog(String id, String entityName, IModel<String> titleModel) {
        super(id);

        dialog = new Dialog("dialog");
        dialog.setWidth(800);
        dialog.setTitle(titleModel);
        add(dialog);

        objects = new DomainObjectListPanel("objects", entityName, null, true){
            @Override
            protected void onSelect(AjaxRequestTarget target, DomainObject domainObject) {
                model.setObject(domainObject.getObjectId());
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

    public void open(AjaxRequestTarget target, IModel<Long> model){
        this.model = model;

        dialog.open(target);
    }

    protected void onSelect(AjaxRequestTarget target){
    }

    public DomainObjectFilter getFilter(){
        return objects.getFilter();
    }
}
