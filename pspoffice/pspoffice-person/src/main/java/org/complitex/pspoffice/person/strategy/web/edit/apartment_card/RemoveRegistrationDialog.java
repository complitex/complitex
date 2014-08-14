/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.web.edit.apartment_card;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import static com.google.common.collect.Lists.*;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.complitex.dictionary.util.DateUtil;
import org.complitex.dictionary.web.component.dateinput.MaskedDateInput;
import org.complitex.dictionary.web.component.fieldset.CollapsibleFieldset;
import org.complitex.dictionary.web.component.scroll.ScrollToElementUtil;
import org.complitex.pspoffice.person.strategy.ApartmentCardStrategy;
import org.complitex.pspoffice.person.strategy.entity.Registration;
import org.complitex.pspoffice.person.strategy.entity.RemoveRegistrationCard;
import org.odlabs.wiquery.core.javascript.JsStatement;
import org.odlabs.wiquery.ui.core.JsScopeUiEvent;
import org.odlabs.wiquery.ui.dialog.Dialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Artem
 */
final class RemoveRegistrationDialog extends Panel {

    private final Logger log = LoggerFactory.getLogger(RemoveRegistrationDialog.class);
    @EJB
    private ApartmentCardStrategy apartmentCardStrategy;
    private IModel<RemoveRegistrationCard> model;
    private Dialog dialog;
    private Form<RemoveRegistrationCard> form;
    private FeedbackPanel messages;
    private long apartmentCardId;
    private List<Registration> registrationsToRemove;
    private MaskedDateInput departureDate;

    RemoveRegistrationDialog(String id) {
        super(id);
        init();
    }

    private void init() {
        dialog = new Dialog("dialog");
        dialog.setModal(true);
        dialog.setWidth(650);
        dialog.setOpenEvent(JsScopeUiEvent.quickScope(new JsStatement().self().chain("parents", "'.ui-dialog:first'").
                chain("find", "'.ui-dialog-titlebar-close'").
                chain("hide").render()));
        dialog.setCloseOnEscape(false);
        add(dialog);

        final Label label = new Label("label", new ResourceModel("title"));
        label.setOutputMarkupId(true);
        dialog.add(label);

        messages = new FeedbackPanel("messages");
        messages.setOutputMarkupId(true);
        dialog.add(messages);

        model = new CompoundPropertyModel<RemoveRegistrationCard>(new RemoveRegistrationCard());
        form = new Form<RemoveRegistrationCard>("form", model);
        form.setOutputMarkupId(true);
        dialog.add(form);

        TextField<String> reason = new TextField<String>("reason");
        reason.setRequired(true);
        form.add(reason);

        departureDate = new MaskedDateInput("date");
        departureDate.setRequired(true);
        form.add(departureDate);

        TextArea<String> explanation = new TextArea<String>("explanation");
        explanation.setRequired(true);
        form.add(explanation);

        CollapsibleFieldset addressFieldset = new CollapsibleFieldset("addressFieldset", new ResourceModel("address"));
        form.add(addressFieldset);
        addressFieldset.add(new TextField<String>("country"));
        addressFieldset.add(new TextField<String>("region"));
        addressFieldset.add(new TextField<String>("district"));
        addressFieldset.add(new TextField<String>("city"));
        addressFieldset.add(new TextField<String>("street"));
        addressFieldset.add(new TextField<String>("buildingNumber"));
        addressFieldset.add(new TextField<String>("buildingCorp"));
        addressFieldset.add(new TextField<String>("apartment"));

        IndicatingAjaxButton removeFromRegistration = new IndicatingAjaxButton("removeFromRegistration", form) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                try {
                    if (RemoveRegistrationDialog.this.validate()) {
                        removeRegistrations();
                        setResponsePage(new ApartmentCardEdit(apartmentCardId, null));
                    } else {
                        target.add(messages);
                        scrollToMessages(target);
                    }
                } catch (Exception e) {
                    log.error("", e);
                    error(getString("db_error"));
                    target.add(messages);
                    scrollToMessages(target);
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(messages);
                scrollToMessages(target);
            }

            private void scrollToMessages(AjaxRequestTarget target) {
                target.appendJavaScript(ScrollToElementUtil.scrollTo(label.getMarkupId()));
            }
        };
        form.add(removeFromRegistration);

        AjaxLink<Void> cancel = new AjaxLink<Void>("cancel") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                dialog.close(target);
            }
        };
        form.add(cancel);
    }

    void open(AjaxRequestTarget target, long apartmentCardId, List<Registration> registrationsToRemove) {
        this.apartmentCardId = apartmentCardId;
        this.registrationsToRemove = registrationsToRemove;
        RemoveRegistrationCard newCard = new RemoveRegistrationCard();
        newCard.setDate(DateUtil.getCurrentDate());
        model.setObject(newCard);

        departureDate.setMinDate(Collections.max(newArrayList(Iterables.transform(registrationsToRemove,
                new Function<Registration, Date>() {

                    @Override
                    public Date apply(Registration registration) {
                        return registration.getRegistrationDate();
                    }
                }))));

        target.add(form);
        target.add(messages);
        dialog.open(target);
    }

    private void removeRegistrations() {
        apartmentCardStrategy.removeRegistrations(registrationsToRemove, model.getObject());
    }

    private boolean validate() {
        Date maxRegistrationDate = Collections.max(newArrayList(transform(registrationsToRemove, new Function<Registration, Date>() {

            @Override
            public Date apply(Registration registration) {
                return registration.getRegistrationDate();
            }
        })));
        if (maxRegistrationDate.after(model.getObject().getDate())) {
            error(getString("departure_date_error"));
            return false;
        }
        return true;
    }
}
