package org.complitex.template.web.pages;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.template.web.template.TemplatePage;

/**
 *
 * @author Artem
 */
@Deprecated
public class ScrollListPage extends TemplatePage {
    public static final String SCROLL_PARAMETER = "idToScroll";

    public ScrollListPage() {
    }

    public ScrollListPage(PageParameters params) {
        String idToScroll = params.get(SCROLL_PARAMETER).toString();

//        if (!Strings.isEmpty(idToScroll)) {
//            add(new ScrollListBehavior(idToScroll));
//        }
    }
}
