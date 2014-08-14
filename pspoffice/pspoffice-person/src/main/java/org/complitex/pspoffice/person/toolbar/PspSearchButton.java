/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.toolbar;

import org.apache.wicket.request.resource.SharedResourceReference;
import org.complitex.pspoffice.person.menu.OperationMenu;
import org.complitex.pspoffice.person.strategy.web.list.apartment_card.ApartmentCardSearch;
import org.complitex.template.web.component.toolbar.ToolbarButton;
import org.complitex.template.web.template.MenuManager;

/**
 *
 * @author Artem
 */
public class PspSearchButton extends ToolbarButton {

    private static final String IMAGE_SRC = "images/icon-open.gif";
    private static final String TITLE_KEY = "psp_search_button";

    public PspSearchButton(String id) {
        super(id, new SharedResourceReference(IMAGE_SRC), TITLE_KEY);
    }

    @Override
    protected void onClick() {
        MenuManager.setMenuItem(OperationMenu.REGISTRATION_MENU_ITEM);
        setResponsePage(ApartmentCardSearch.class);
    }
}
