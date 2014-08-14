/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.web.component.autocomplete;

import java.util.List;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.complitex.pspoffice.person.strategy.web.component.RoomCreateDialog;
import org.odlabs.wiquery.ui.autocomplete.Autocomplete;

/**
 *
 * @author Artem
 */
abstract class AutocompleteRoomCreateDialog extends RoomCreateDialog {

    private final Autocomplete<String> autocomplete;

    AutocompleteRoomCreateDialog(String id, Autocomplete<String> autocomplete, List<Long> userOrganizationIds) {
        super(id, userOrganizationIds);
        this.autocomplete = autocomplete;
    }

    @Override
    protected void onCancel(AjaxRequestTarget target) {
        super.onCancel(target);
        autocomplete.setModelObject(null);
        target.add(autocomplete);
        target.focusComponent(autocomplete);
    }
}
