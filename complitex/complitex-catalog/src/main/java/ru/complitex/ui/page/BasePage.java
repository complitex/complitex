package ru.complitex.ui.page;

import de.agilecoders.wicket.webjars.request.resource.WebjarsCssResourceReference;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import ru.complitex.ui.resource.CssResourceReference;


/**
 * @author Ivanov Anatoliy
 */
//@AuthorizeInstantiation(UserRoles.ADMINISTRATOR)
public class BasePage extends WebPage {
    public static final MetaDataKey<String> NAME = new MetaDataKey<>() {};

    public static final MetaDataKey<String> MENU = new MetaDataKey<>() {};

    private static final WebjarsCssResourceReference FONT = new WebjarsCssResourceReference("font-awesome/current/css/all.css");

    public BasePage() {
        String name = getApplication().getMetaData(NAME);

        add(new Label("title", name));

        add(new Label("header", name));

        try {
            Panel menu = (Panel) Class.forName(getApplication().getMetaData(MENU))
                    .getDeclaredConstructor(String.class)
                    .newInstance("menu");

            add(menu);
        } catch (Exception e) {
            e.printStackTrace();
        }

        add(new Link<>("logout") {
            @Override
            public void onClick() {
                getSession().invalidate();
            }
        });
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        response.render(CssHeaderItem.forReference(CssResourceReference.INSTANCE));

        response.render(CssHeaderItem.forReference(FONT));
    }
}
