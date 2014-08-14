/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.web.edit.apartment_card;

import org.apache.wicket.Component;
import org.complitex.dictionary.web.component.back.BackInfo;
import org.complitex.pspoffice.person.menu.OperationMenu;
import org.complitex.template.web.template.MenuManager;

/**
 *
 * @author Artem
 */
public final class ApartmentCardBackInfo extends BackInfo {

    private final long apartmentCardId;
    private final String backInfoSessionKey;

    public ApartmentCardBackInfo(long apartmentCardId, String backInfoSessionKey) {
        this.apartmentCardId = apartmentCardId;
        this.backInfoSessionKey = backInfoSessionKey;
    }

    @Override
    public void back(Component pageComponent) {
        MenuManager.setMenuItem(OperationMenu.REGISTRATION_MENU_ITEM);
        pageComponent.setResponsePage(new ApartmentCardEdit(apartmentCardId, backInfoSessionKey));
    }
}
