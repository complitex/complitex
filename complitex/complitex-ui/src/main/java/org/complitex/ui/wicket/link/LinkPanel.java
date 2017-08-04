package org.complitex.ui.wicket.link;

import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * @author Anatoly A. Ivanov
 * 28.07.2017 17:00
 */
public class LinkPanel extends Panel{
    public static String LINK_COMPONENT_ID = "link";

    public LinkPanel(String id, Link link) {
        super(id);

        add(link);
    }
}
