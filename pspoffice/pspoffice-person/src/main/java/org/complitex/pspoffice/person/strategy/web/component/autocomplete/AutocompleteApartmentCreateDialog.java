/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.web.component.autocomplete;

import org.apache.wicket.ajax.AjaxRequestTarget;
import java.util.List;
import org.complitex.pspoffice.person.strategy.web.component.ApartmentCreateDialog;
import org.odlabs.wiquery.ui.autocomplete.Autocomplete;

/**
 *
 * @author Artem
 */
abstract class AutocompleteApartmentCreateDialog extends ApartmentCreateDialog {

    private final Autocomplete<String> autocomplete;

    AutocompleteApartmentCreateDialog(String id, Autocomplete<String> autocomplete, List<Long> userOrganizationIds) {
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
