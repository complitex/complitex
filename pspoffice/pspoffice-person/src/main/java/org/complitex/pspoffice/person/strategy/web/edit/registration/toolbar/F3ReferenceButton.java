/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.web.edit.registration.toolbar;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.request.resource.SharedResourceReference;
import org.complitex.template.web.component.toolbar.ToolbarButton;

/**
 *
 * @author Artem
 */
public abstract class F3ReferenceButton extends ToolbarButton {

    private static final String IMAGE_SRC = "images/icon-editDocument.gif";
    private static final String TITLE_KEY = "image.title.f3reference";

    public F3ReferenceButton(String id) {
        super(id, new SharedResourceReference(IMAGE_SRC), TITLE_KEY);
    }

    @Override
    protected Link<Void> newLink(String linkId) {
        return new Link<Void>(linkId) {

            @Override
            public void onClick() {
                F3ReferenceButton.this.onClick();
            }

            @Override
            protected void onComponentTag(ComponentTag tag) {
                super.onComponentTag(tag);
                tag.put("target", "_blank");
            }
        };
    }
}
