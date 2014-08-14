/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.web.edit.apartment_card;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.odlabs.wiquery.ui.dialog.Dialog;

/**
 *
 * @author Artem
 */
final class DisableApartmentCardDialog extends Panel {

    private Dialog dialog;

    DisableApartmentCardDialog(String id) {
        super(id);

        dialog = new Dialog("dialog");
        dialog.setModal(true);
        dialog.setWidth(600);
        add(dialog);

        dialog.add(new AjaxLink<Void>("ok") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                dialog.close(target);
            }
        });
    }

    void open(AjaxRequestTarget target) {
        dialog.open(target);
    }
}
