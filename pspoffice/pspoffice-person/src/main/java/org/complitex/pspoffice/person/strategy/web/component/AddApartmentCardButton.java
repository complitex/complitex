/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.web.component;

import org.apache.wicket.request.resource.SharedResourceReference;
import org.complitex.template.web.component.toolbar.ToolbarButton;

/**
 *
 * @author Artem
 */
public abstract class AddApartmentCardButton extends ToolbarButton {

    private static final String IMAGE_SRC = "images/icon-addItem.gif";
    private static final String TITLE_KEY = "add_apartment_card";

    public AddApartmentCardButton(String id) {
        super(id, new SharedResourceReference(IMAGE_SRC), TITLE_KEY);
    }
}
