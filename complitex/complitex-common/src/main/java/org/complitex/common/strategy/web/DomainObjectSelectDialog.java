package org.complitex.common.strategy.web;

import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.entity.description.Entity;
import org.complitex.common.service.EntityBean;
import org.odlabs.wiquery.ui.dialog.Dialog;

import javax.ejb.EJB;
import java.text.Normalizer;

/**
 * @author inheaven on 004 04.12.14 17:34.
 */
public class DomainObjectSelectDialog extends Panel{
    @EJB
    private EntityBean entityBean;

    private Dialog dialog;

    public DomainObjectSelectDialog(String id, String entityTable) {
        super(id);

        Entity entity = entityBean.getEntity(entityTable);

        dialog = new Dialog("dialog");
        add(dialog);







    }
}
