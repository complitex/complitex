package ru.complitex.pspoffice.person.report.web;

import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Fragment;

import java.util.Collection;

/**
 * @author inheaven on 013 13.04.15 17:42
 */
class MessagesFragment extends Fragment {

    private RegistrationStopCouponPage components;
    private Collection<FeedbackMessage> messages;

    MessagesFragment(RegistrationStopCouponPage components, String id, Collection<FeedbackMessage> messages) {
        super(id, "messages", components);
        this.components = components;
        this.messages = messages;
        add(new FeedbackPanel("messages"));
    }

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();
        for (FeedbackMessage message : messages) {
            getSession().getFeedbackMessages().add(message);
        }
    }
}
