/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.complitex.pspoffice.person.strategy.web.edit.apartment_card;

import org.apache.wicket.Component;
import ru.complitex.common.web.component.back.BackInfo;
import ru.complitex.pspoffice.person.menu.OperationMenu;
import ru.complitex.template.web.template.MenuManager;

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
