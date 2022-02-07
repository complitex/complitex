/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.complitex.pspoffice.person.strategy.web.edit.person.toolbar;

import org.apache.wicket.request.resource.SharedResourceReference;
import ru.complitex.template.web.component.toolbar.ToolbarButton;

/**
 *
 * @author Artem
 */
public class DeathButton extends ToolbarButton {

    private static final String IMAGE_SRC = "images/icon-deleteDocument.gif";
    private static final String TITLE_KEY = "death";

    public DeathButton(String id) {
        super(id, new SharedResourceReference(IMAGE_SRC), TITLE_KEY, true);
    }
}
