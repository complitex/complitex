package org.complitex.template.web.component.toolbar;

import org.apache.wicket.request.resource.SharedResourceReference;

/**
 *
 * @author Artem
 */
public abstract class DeleteItemButton extends ToolbarButton {

    private static final String IMAGE_SRC = "images/icon-deleteDocument.gif";

    private static final String TITLE_KEY = "image.title.deleteItem";

    public DeleteItemButton(String id) {
        super(id, new SharedResourceReference(IMAGE_SRC), TITLE_KEY, false);
    }

    public DeleteItemButton(String id, boolean useAjax) {
        super(id, new SharedResourceReference(IMAGE_SRC), TITLE_KEY, useAjax);
    }


}
