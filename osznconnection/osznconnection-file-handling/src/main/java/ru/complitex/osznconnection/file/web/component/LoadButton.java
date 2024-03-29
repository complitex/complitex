package ru.complitex.osznconnection.file.web.component;

import org.apache.wicket.request.resource.SharedResourceReference;
import ru.complitex.template.web.component.toolbar.ToolbarButton;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 10.12.10 17:58
 */
public abstract class LoadButton extends ToolbarButton {

    private static final String IMAGE_SRC = "images/icon-open.gif";
    private static final String TITLE_KEY = "load";

    public LoadButton(String id) {
        super(id, new SharedResourceReference(IMAGE_SRC), TITLE_KEY, true);
    }
}