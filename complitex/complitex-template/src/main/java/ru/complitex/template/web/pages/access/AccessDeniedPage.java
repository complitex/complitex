package ru.complitex.template.web.pages.access;

import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.ResourceModel;
import ru.complitex.resources.WebCommonResourceInitializer;

/**
 *
 * @author Artem
 */
public final class AccessDeniedPage extends org.apache.wicket.markup.html.pages.AccessDeniedPage {

    public AccessDeniedPage() {
        init();
    }

    private void init() {
        add(new Label("title", new ResourceModel("title")));
        //todo back to previous page
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        response.render(CssHeaderItem.forReference(WebCommonResourceInitializer.STYLE_CSS));
    }
}
