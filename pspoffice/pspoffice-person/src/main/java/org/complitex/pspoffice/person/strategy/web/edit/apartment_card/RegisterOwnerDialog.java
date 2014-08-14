/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.web.edit.apartment_card;

import com.google.common.base.Function;
import static com.google.common.collect.ImmutableList.*;
import com.google.common.collect.Iterables;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Date;
import static com.google.common.collect.Lists.*;
import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.util.string.Strings;
import org.complitex.dictionary.entity.Attribute;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.util.DateUtil;
import org.complitex.dictionary.web.component.DisableAwareDropDownChoice;
import org.complitex.dictionary.web.component.DomainObjectDisableAwareRenderer;
import org.complitex.dictionary.web.component.dateinput.MaskedDateInput;
import org.complitex.dictionary.web.component.fieldset.CollapsibleFieldset;
import org.complitex.dictionary.web.component.scroll.ScrollToElementUtil;
import org.complitex.pspoffice.person.strategy.ApartmentCardStrategy;
import org.complitex.pspoffice.person.strategy.PersonStrategy;
import org.complitex.pspoffice.person.strategy.entity.ApartmentCard;
import org.complitex.pspoffice.person.strategy.entity.Person;
import org.complitex.pspoffice.person.strategy.entity.RegisterOwnerCard;
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
final class RegisterOwnerDialog extends Panel {

    private final Logger log = LoggerFactory.getLogger(RegisterOwnerDialog.class);
    @EJB
    private ApartmentCardStrategy apartmentCardStrategy;
    @EJB
    private RegistrationTypeStrategy registrationTypeStrategy;
    @EJB
    private PersonStrategy personStrategy;
    private IModel<RegisterOwnerCard> model;
    private Dialog dialog;
    private Form<RegisterOwnerCard> form;
    private FeedbackPanel messages;
    private ApartmentCard apartmentCard;
    private WebMarkupContainer registerChildrenContainer;
    private List<Person> children;
    private Date saveDate;

    RegisterOwnerDialog(String id) {
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

        model = new CompoundPropertyModel<RegisterOwnerCard>(new RegisterOwnerCard());
        form = new Form<RegisterOwnerCard>("form", model);
        form.setOutputMarkupId(true);
        dialog.add(form);

        MaskedDateInput registrationDate = new MaskedDateInput("registrationDate");
        registrationDate.setRequired(true);
        form.add(registrationDate);

        final List<DomainObject> allRegistrationTypes = registrationTypeStrategy.getAll();

        DisableAwareDropDownChoice<DomainObject> registrationType = new DisableAwareDropDownChoice<DomainObject>("registrationType",
                allRegistrationTypes, new DomainObjectDisableAwareRenderer() {

            @Override
            public Object getDisplayValue(DomainObject object) {
                return registrationTypeStrategy.displayDomainObject(object, getLocale());
            }
        });
        registrationType.setRequired(true);
        form.add(registrationType);

        registerChildrenContainer = new WebMarkupContainer("registerChildrenContainer");
        form.add(registerChildrenContainer);
        registerChildrenContainer.add(new CheckBox("registerChildren"));

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
        addressFieldset.add(new MaskedDateInput("arrivalDate"));

        IndicatingAjaxButton register = new IndicatingAjaxButton("register") {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                try {
                    if (RegisterOwnerDialog.this.validate()) {
                        register();
                        setResponsePage(new ApartmentCardEdit(apartmentCard.getId(), null));
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
        form.add(register);
        AjaxLink<Void> cancel = new AjaxLink<Void>("cancel") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                setResponsePage(new ApartmentCardEdit(apartmentCard.getId(), null));
            }
        };
        form.add(cancel);
    }

    void open(AjaxRequestTarget target, ApartmentCard apartmentCard, Date saveDate) {
        this.apartmentCard = apartmentCard;
        this.saveDate = saveDate;

        RegisterOwnerCard newCard = new RegisterOwnerCard();
        newCard.setRegistrationDate(DateUtil.getCurrentDate());
        model.setObject(newCard);

        children = newArrayList();
        List<Attribute> childrenAttributes = apartmentCard.getOwner().getAttributes(PersonStrategy.CHILDREN);
        for (Attribute childAttribute : childrenAttributes) {
            children.add(personStrategy.findById(childAttribute.getValueId(), true, true, false, false, false));
        }
        registerChildrenContainer.setVisible(children != null && !children.isEmpty());

        target.add(form);
        target.add(messages);
        dialog.open(target);
    }

    private void register() {
        apartmentCardStrategy.registerOwner(apartmentCard, model.getObject(), children, saveDate);
    }

    private boolean validate() {
        boolean valid = true;

        //registration date
        RegisterOwnerCard card = model.getObject();
        if (!card.isRegisterChildren()) {
            if (!card.getRegistrationDate().after(apartmentCard.getOwner().getBirthDate())) {
                error(getString("registration_date_owner_error"));
                valid = false;
            }
        } else {
            Date maxBirthDate = Collections.max(newArrayList(Iterables.transform(
                    Iterables.concat(of(apartmentCard.getOwner()), children),
                    new Function<Person, Date>() {

                        @Override
                        public Date apply(Person person) {
                            return person.getBirthDate();
                        }
                    })));
            if (!card.getRegistrationDate().after(maxBirthDate)) {
                error(getString("registration_date_children_error"));
                valid = false;
            }
        }

        //permanent registration type
        if (card.getRegistrationType().getId().equals(RegistrationTypeStrategy.PERMANENT)) {
            if (!card.isRegisterChildren()) {
                Person owner = apartmentCard.getOwner();
                String address = personStrategy.findPermanentRegistrationAddress(owner.getId(), getLocale());
                if (!Strings.isEmpty(address)) {
                    String personName = personStrategy.displayDomainObject(owner, getLocale());
                    error(MessageFormat.format(getString("permanent_registration_error"), personName, address));
                    valid = false;
                }
            } else {
                List<Person> allPersons = newArrayList();
                allPersons.add(apartmentCard.getOwner());
                allPersons.addAll(children);
                for (Person person : allPersons) {
                    String address = personStrategy.findPermanentRegistrationAddress(person.getId(), getLocale());
                    if (!Strings.isEmpty(address)) {
                        String personName = personStrategy.displayDomainObject(person, getLocale());
                        error(MessageFormat.format(getString("permanent_registration_error"), personName, address));
                        valid = false;
                    }
                }
            }
        }

        return valid;
    }
}
