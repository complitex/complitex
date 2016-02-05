package org.complitex.common.web.component.scroll;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 *
 * @author Artem
 */
public class ScrollBookmarkablePageLink<T> extends BookmarkablePageLink<T> {

    public <C extends Page> ScrollBookmarkablePageLink(String id, Class<C> pageClass, PageParameters parameters, String markupId) {
        super(id, pageClass, parameters);
        setMarkupId(ScrollListBehavior.SCROLL_PREFIX + markupId);
    }

    public <C extends Page> ScrollBookmarkablePageLink(String id, Class<C> pageClass, String markupId) {
        this(id, pageClass, null, markupId);
    }
}
