package ru.complitex.template.web.component.toolbar;

import org.apache.wicket.request.resource.SharedResourceReference;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 03.06.11 17:45
 */
public abstract class SaveButton extends ToolbarButton {

    private static final String IMAGE_SRC = "images/icon-save.gif";
    private static final String TITLE_KEY = "image.title.save";

    public SaveButton(String id, boolean useAjax) {
        super(id, new SharedResourceReference(IMAGE_SRC), TITLE_KEY, useAjax);
    }
}
