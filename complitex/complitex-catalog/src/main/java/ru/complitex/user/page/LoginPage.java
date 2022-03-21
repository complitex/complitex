package ru.complitex.user.page;

import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import ru.complitex.ui.resource.CssResourceReference;
import ru.complitex.user.session.WebSession;

/**
 * @author Ivanov Anatoliy
 */
public class LoginPage extends WebPage {
    public LoginPage() {
        IModel<String> usernameModel = Model.of("");
        IModel<String> passwordModel = Model.of("");

        FeedbackPanel feedbackPanel = new NotificationPanel("feedback");
        add(feedbackPanel);

        Form<String> form = new StatelessForm<>("form");
        add(form);

        form.add(new TextField<>("username", usernameModel));
        form.add(new PasswordTextField("password", passwordModel));

        form.add(new Button("login"){
            @Override
            public void onSubmit() {
                if (((WebSession)getSession()).signIn(usernameModel.getObject(), passwordModel.getObject())) {
                    continueToOriginalDestination();
                    setResponsePage(getApplication().getHomePage());
                } else {
                    error(getString("error_login"));
                }
            }
        });
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        response.render(CssHeaderItem.forReference(CssResourceReference.INSTANCE));
    }
}
