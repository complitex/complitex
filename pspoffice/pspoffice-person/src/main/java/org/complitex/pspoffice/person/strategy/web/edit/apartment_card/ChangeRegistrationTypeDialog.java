/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.web.edit.apartment_card;

import java.text.MessageFormat;
import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.string.Strings;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.web.component.DisableAwareDropDownChoice;
import org.complitex.dictionary.web.component.DomainObjectDisableAwareRenderer;
import org.complitex.pspoffice.person.strategy.ApartmentCardStrategy;
import org.complitex.pspoffice.person.strategy.PersonStrategy;
import org.complitex.pspoffice.person.strategy.entity.ChangeRegistrationTypeCard;
import org.complitex.pspoffice.person.strategy.entity.Person;
import org.complitex.pspoffice.person.strategy.entity.Registration;
import org.complitex.pspoffice.registration_type.strategy.RegistrationTypeStrategy;
import org.odlabs.wiquery.core.javascript.JsStatement;
import org.odlabs.wiquery.ui.core.JsScopeUiEvent;
import org.odlabs.wiquery.ui.dialog.Dialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Artem
 */
final class ChangeRegistrationTypeDialog extends Panel {

    private final Logger log = LoggerFactory.getLogger(ChangeRegistrationTypeDialog.class);
    @EJB
    private ApartmentCardStrategy apartmentCardStrategy;
    @EJB
    private RegistrationTypeStrategy registrationTypeStrategy;
    @EJB
    private PersonStrategy personStrategy;
    private IModel<ChangeRegistrationTypeCard> model;
    private Dialog dialog;
    private FeedbackPanel messages;
    private Form<ChangeRegistrationTypeCard> form;
    private DisableAwareDropDownChoice<DomainObject> registrationType;
    private TextArea<String> explanation;
    private List<Registration> registrationsToChangeType;
    private long apartmentCardId;
    private final List<DomainObject> allRegistrationTypes;
    private DomainObject defaultRegistrationType;

    ChangeRegistrationTypeDialog(String id) {
        super(id);

        allRegistrationTypes = registrationTypeStrategy.getAll();
        for (DomainObject regType : allRegistrationTypes) {
            if (regType.getId().equals(RegistrationTypeStrategy.PERMANENT)) {
                defaultRegistrationType = regType;
                break;
            }
        }
        if (defaultRegistrationType == null) {
            defaultRegistrationType = allRegistrationTypes.get(0);
        }

        init();
    }

    private void init() {
        dialog = new Dialog("dialog") {

            {
                getOptions().putLiteral("width", "auto");
            }
        };
        dialog.setModal(true);
        dialog.setOpenEvent(JsScopeUiEvent.quickScope(new JsStatement().self().chain("parents", "'.ui-dialog:first'").
                chain("find", "'.ui-dialog-titlebar-close'").
                chain("hide").render()));
        dialog.setCloseOnEscape(false);
        add(dialog);

        messages = new FeedbackPanel("messages");
        messages.setOutputMarkupId(true);
        dialog.add(messages);

        model = new CompoundPropertyModel<ChangeRegistrationTypeCard>(new ChangeRegistrationTypeCard());
        form = new Form<ChangeRegistrationTypeCard>("form", model);
        form.setOutputMarkupId(true);
        dialog.add(form);

        model.getObject().setRegistrationType(defaultRegistrationType);
        registrationType = new DisableAwareDropDownChoice<DomainObject>("registrationType",
                allRegistrationTypes, new DomainObjectDisableAwareRenderer() {

            @Override
            public Object getDisplayValue(DomainObject object) {
                return registrationTypeStrategy.displayDomainObject(object, getLocale());
            }
        });
        registrationType.setRequired(true);
        form.add(registrationType);

        explanation = new TextArea<String>("explanation");
        explanation.setRequired(true);
        form.add(explanation);

        IndicatingAjaxButton submit = new IndicatingAjaxButton("submit", form) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                try {
                    if (ChangeRegistrationTypeDialog.this.validate()) {
                        changeRegistrationType();
                        setResponsePage(new ApartmentCardEdit(apartmentCardId, null));
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
        form.add(submit);

        AjaxLink<Void> cancel = new AjaxLink<Void>("cancel") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                dialog.close(target);
            }
        };
        form.add(cancel);
    }

    void open(AjaxRequestTarget target, long apartmentCardId, List<Registration> registrationsToChangeType) {
        this.apartmentCardId = apartmentCardId;
        this.registrationsToChangeType = registrationsToChangeType;

        initializeModel();

        target.add(form);
        target.add(messages);
        dialog.open(target);
    }

    private void initializeModel() {
        model.setObject(new ChangeRegistrationTypeCard());
        model.getObject().setRegistrationType(defaultRegistrationType);
    }

    private void changeRegistrationType() {
        apartmentCardStrategy.changeRegistrationType(registrationsToChangeType, model.getObject());
    }

    private boolean validate() {
        boolean valid = true;

        //permanent registration type
        Long registrationTypeId = model.getObject().getRegistrationType().getId();
        if (registrationTypeId.equals(RegistrationTypeStrategy.PERMANENT)) {
            for (Registration registration : registrationsToChangeType) {
                Person person = registration.getPerson();
                String address = personStrategy.findPermanentRegistrationAddress(person.getId(), getLocale());
                if (!Strings.isEmpty(address)) {
                    String personName = personStrategy.displayDomainObject(person, getLocale());
                    error(MessageFormat.format(getString("permanent_registration_error"), personName, address));
                    valid = false;
                }
            }
        }

        return valid;
    }
}
