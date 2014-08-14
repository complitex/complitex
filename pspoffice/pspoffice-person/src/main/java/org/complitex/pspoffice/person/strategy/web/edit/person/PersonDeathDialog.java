/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.web.edit.person;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import java.text.MessageFormat;
import java.util.Collections;
import static com.google.common.collect.Iterables.*;
import static com.google.common.collect.Lists.*;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.complitex.address.service.AddressRendererBean;
import org.complitex.dictionary.util.DateUtil;
import org.complitex.dictionary.web.component.dateinput.MaskedDateInput;
import org.complitex.pspoffice.person.strategy.PersonStrategy;
import org.complitex.pspoffice.person.strategy.PersonStrategy.PersonRegistration;
import org.complitex.pspoffice.person.strategy.entity.Person;
import org.complitex.pspoffice.person.util.PersonDateFormatter;
import org.odlabs.wiquery.core.javascript.JsStatement;
import org.odlabs.wiquery.ui.core.JsScopeUiEvent;
import org.odlabs.wiquery.ui.dialog.Dialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Artem
 */
final class PersonDeathDialog extends Panel {

    private final Logger log = LoggerFactory.getLogger(PersonDeathDialog.class);
    @EJB
    private PersonStrategy personStrategy;
    @EJB
    private AddressRendererBean addressRendererBean;
    private Dialog dialog;
    private WebMarkupContainer content;
    private IModel<Date> deathDateModel;
    private Person person;
    private List<PersonRegistration> personRegistrations;

    PersonDeathDialog(String id) {
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

        content = new WebMarkupContainer("content");
        content.setOutputMarkupId(true);
        dialog.add(content);

        final FeedbackPanel messages = new FeedbackPanel("messages");
        messages.setOutputMarkupId(true);
        content.add(messages);

        deathDateModel = new Model<Date>();
        Form<Void> form = new Form<Void>("form");
        content.add(form);

        MaskedDateInput deathDate = new MaskedDateInput("date", deathDateModel);
        deathDate.setRequired(true);
        form.add(deathDate);

        IndicatingAjaxButton registerDeath = new IndicatingAjaxButton("registerDeath", form) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                try {
                    loadActivePersonRegistrations();
                    if (PersonDeathDialog.this.validate()) {
                        registerDeath();
                        setResponsePage(personStrategy.getListPage(), personStrategy.getListPageParams());
                    } else {
                        target.add(messages);
                    }
                } catch (Exception e) {
                    log.error("", e);
                    error(getString("db_error"));
                    target.add(messages);
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(messages);
            }
        };
        form.add(registerDeath);

        AjaxLink<Void> cancel = new AjaxLink<Void>("cancel") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                dialog.close(target);
            }
        };
        form.add(cancel);
    }

    private void loadActivePersonRegistrations() {
        personRegistrations = newArrayList(filter(personStrategy.findPersonRegistrations(person.getId()), new Predicate<PersonRegistration>() {

            @Override
            public boolean apply(PersonRegistration personRegistration) {
                return !personRegistration.getRegistration().isFinished();
            }
        }));
    }

    void open(AjaxRequestTarget target, Person person) {
        this.person = person;
        deathDateModel.setObject(DateUtil.getCurrentDate());
        personRegistrations = null;
        target.add(content);
        dialog.open(target);
    }

    private void registerDeath() {
        personStrategy.registerPersonDeath(person, deathDateModel.getObject(), personRegistrations, getLocale());
    }

    private boolean validate() {
        Date deathDate = deathDateModel.getObject();
        boolean valid = true;
        if (deathDate.before(person.getBirthDate())) {
            error(getString("death_birth_date_error"));
            valid = false;
        }
        if (!person.getChildren().isEmpty()) {
            Date maxChildrenBirthDate = Collections.max(newArrayList(transform(person.getChildren(), new Function<Person, Date>() {

                @Override
                public Date apply(Person child) {
                    return child.getBirthDate();
                }
            })));
            if (deathDate.before(maxChildrenBirthDate)) {
                error(getString("death_children_birth_date_error"));
                valid = false;
            }
        }
        if (personRegistrations != null && !personRegistrations.isEmpty()) {
            for (PersonRegistration personRegistration : personRegistrations) {
                if (deathDate.before(personRegistration.getRegistration().getRegistrationDate())) {
                    error(MessageFormat.format(getString("death_registration_date_error"),
                            addressRendererBean.displayAddress(personRegistration.getAddressEntity(),
                            personRegistration.getAddressId(), getLocale()),
                            PersonDateFormatter.format(personRegistration.getRegistration().getRegistrationDate())));
                    valid = false;
                    break;
                }
            }
        }
        return valid;
    }
}
