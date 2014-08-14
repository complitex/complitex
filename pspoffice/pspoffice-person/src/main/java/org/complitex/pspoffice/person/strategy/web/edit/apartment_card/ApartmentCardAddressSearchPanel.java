/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.web.edit.apartment_card;

import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.service.SessionBean;
import org.complitex.dictionary.web.component.ShowMode;
import org.complitex.dictionary.web.component.search.ISearchCallback;
import org.complitex.dictionary.web.component.search.SearchComponentState;
import org.complitex.pspoffice.person.strategy.web.component.autocomplete.EnhancedAddressSearchComponent;

/**
 *
 * @author Artem
 */
class ApartmentCardAddressSearchPanel extends Panel {

    @EJB
    private SessionBean sessionBean;

    ApartmentCardAddressSearchPanel(String id, SearchComponentState searchComponentState, List<String> searchFilters,
            ISearchCallback callback, ShowMode showMode, boolean enabled) {
        super(id);

        add(new EnhancedAddressSearchComponent("searchComponent", searchComponentState, searchFilters,
                callback, showMode, enabled, sessionBean.getUserOrganizationObjectIds()) {

            @Override
            protected void onSelect(AjaxRequestTarget target, String entity) {
                super.onSelect(target, entity);

                ApartmentCardAddressSearchPanel.this.onSelect(target, entity, getModelObject(entity));
            }
        });
    }

    protected void onSelect(AjaxRequestTarget target, String entity, DomainObject object) {
    }
}
