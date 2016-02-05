package org.complitex.common.web.component.scroll;

import org.apache.wicket.util.string.Strings;

import java.text.MessageFormat;

/**
 * Page must import scroll js as following:
 *  add(JavascriptPackageResource.getHeaderContribution(WebCommonResourceInitializer.SCROLL_JS));
 * @author Artem
 */
public class ScrollToElementUtil {

    private final static String SCROLL_TO_JAVASCRIPT = "$(document).scrollTo($(''#{0}''), {1});";
    private final static int SPEED_SCROLLING = 600;

    private ScrollToElementUtil() {
    }

    public static String scrollTo(String domElementId) {
        if (Strings.isEmpty(domElementId)) {
            throw new IllegalArgumentException("Dom element id is null or empty.");
        }
        return MessageFormat.format(SCROLL_TO_JAVASCRIPT, domElementId, SPEED_SCROLLING);
    }
}
