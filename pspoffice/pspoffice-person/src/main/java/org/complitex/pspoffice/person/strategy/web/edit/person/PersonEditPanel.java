package org.complitex.pspoffice.person.strategy.web.edit.person;

import com.google.common.base.Function;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.util.string.Strings;
import org.complitex.dictionary.entity.Log;
import org.complitex.dictionary.service.LogBean;
import org.complitex.dictionary.service.SessionBean;
import org.complitex.dictionary.service.StringCultureBean;
import org.complitex.dictionary.util.DateUtil;
import org.complitex.dictionary.web.component.scroll.ScrollToElementUtil;
import org.complitex.pspoffice.person.Module;
import org.complitex.pspoffice.person.strategy.PersonStrategy;
import org.complitex.pspoffice.person.strategy.entity.Person;
import org.complitex.pspoffice.person.strategy.entity.PersonAgeType;
import org.complitex.pspoffice.person.strategy.web.component.ExplanationDialog;
import org.complitex.pspoffice.person.strategy.web.history.person.PersonHistoryPage;
import org.complitex.resources.WebCommonResourceInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.text.MessageFormat;
import java.util.*;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.transform;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static org.complitex.dictionary.strategy.web.DomainObjectAccessUtil.canEdit;
import static org.complitex.dictionary.web.component.DomainObjectComponentUtil.labelModel;
import static org.complitex.pspoffice.person.strategy.PersonStrategy.*;

/**
 *
 * @author Artem
 */
public abstract class PersonEditPanel extends Panel {

    private final Logger log = LoggerFactory.getLogger(PersonEditPanel.class);
    @EJB
    private StringCultureBean stringBean;
    @EJB
    private LogBean logBean;
    @EJB
    private SessionBean sessionBean;
    @EJB
    private PersonStrategy personStrategy;
    private Person oldPerson;
    private Person newPerson;
    private PersonInputPanel personInputPanel;
    private FeedbackPanel messages;

    public PersonEditPanel(String id, PersonAgeType personAgeType, Person oldPerson, Person newPerson) {
        super(id);
        this.oldPerson = oldPerson;
        this.newPerson = newPerson;
        init(personAgeType, null, null, null, null);
    }

    public PersonEditPanel(String id, Person newPerson, PersonAgeType personAgeType,
            Locale defaultNameLocale, String defaultLastName, String defaultFirstName, String defaultMiddleName) {
        super(id);
        this.newPerson = newPerson;
        init(personAgeType, defaultNameLocale, defaultLastName, defaultFirstName, defaultMiddleName);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(JavaScriptHeaderItem.forReference(WebCommonResourceInitializer.SCROLL_JS));
    }

    private void init(PersonAgeType personAgeType, Locale defaultNameLocale,
            String defaultLastName, String defaultFirstName, String defaultMiddleName) {
        final Label label = new Label("label", new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                final String entityName = stringBean.displayValue(personStrategy.getEntity().getEntityNames(), getLocale());
                return isNew() || !sessionBean.isAdmin() ? entityName
                        : MessageFormat.format(getString("label_edit"), entityName, newPerson.getId());
            }
        });
        label.setOutputMarkupId(true);
        add(label);

        messages = new FeedbackPanel("messages");
        messages.setOutputMarkupId(true);
        add(messages);

        Form<Void> form = new Form<Void>("form");

        //input panel
        personInputPanel = new PersonInputPanel("personInputPanel", newPerson, messages, label, personAgeType,
                defaultNameLocale, defaultLastName, defaultFirstName, defaultMiddleName);
        form.add(personInputPanel);

        //history
        form.add(new Link<Void>("history") {

            @Override
            public void onClick() {
                setResponsePage(new PersonHistoryPage(newPerson.getId()));
            }
        }.setVisible(!isNew()));

        //register children
        final RegisterChildrenDialog registerChildrenDialog = new RegisterChildrenDialog("registerChildrenDialog") {

            @Override
            protected void onClose(AjaxRequestTarget target) {
                onSave(oldPerson, newPerson, target);
            }
        };
        add(registerChildrenDialog);

        //explanation
        final ExplanationDialog personExplanationDialog = new ExplanationDialog("personExplanationDialog");
        add(personExplanationDialog);

        //save-cancel functional
        IndicatingAjaxButton submit = new IndicatingAjaxButton("submit", form) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                try {
                    if (PersonEditPanel.this.validate()) {
                        String needExplanationLabel = needExplanationLabel();
                        boolean isNeedExplanation = !Strings.isEmpty(needExplanationLabel);
                        if (!isNeedExplanation) {
                            persist(target, null);
                        } else {
                            personExplanationDialog.open(target, needExplanationLabel, new ExplanationDialog.ISubmitAction() {

                                @Override
                                public void onSubmit(AjaxRequestTarget target, String explanation) {
                                    try {
                                        persist(target, explanation);
                                    } catch (Exception e) {
                                        onFatalError(target, e);
                                    }
                                }
                            });
                        }
                    } else {
                        target.add(messages);
                        scrollToMessages(target);
                    }
                } catch (Exception e) {
                    onFatalError(target, e);
                }
            }

            private void persist(AjaxRequestTarget target, String explanation) {
                save(explanation);

                // register children dialog
                List<Person> newChildren = null;
                List<PersonApartmentCardAddress> personApartmentCardAddresses = null;
                boolean needRegisterChildren = false;
                if (!isNew()) {
                    newChildren = getNewChildren();
                    if (newChildren != null && !newChildren.isEmpty()) {
                        personApartmentCardAddresses = getPersonApartmentCardAddresses(newChildren);
                        if (personApartmentCardAddresses != null && !personApartmentCardAddresses.isEmpty()) {
                            needRegisterChildren = true;
                        }
                    }
                }
                if (needRegisterChildren) {
                    registerChildrenDialog.open(target, personApartmentCardAddresses, newChildren);
                } else {
                    onSave(oldPerson, newPerson, target);
                }
            }

            private void onFatalError(AjaxRequestTarget target, Exception e) {
                log.error("", e);
                error(getString("db_error"));
                target.add(messages);
                scrollToMessages(target);
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
        submit.setVisible(canEdit(null, personStrategy.getEntityTable(), newPerson));
        form.add(submit);

        AjaxLink<Void> cancel = new AjaxLink<Void>("cancel") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                onCalcel(target);
            }
        };
        cancel.setVisible(canEdit(null, personStrategy.getEntityTable(), newPerson));
        form.add(cancel);

        AjaxLink<Void> back = new AjaxLink<Void>("back") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                onBack(target);
            }
        };
        back.setVisible(!canEdit(null, personStrategy.getEntityTable(), newPerson));
        form.add(back);
        add(form);
    }

    private boolean validate() {
        return personInputPanel.validate();
    }

    private boolean isNew() {
        return oldPerson == null;
    }

    private void save(String explanation) {
        personInputPanel.beforePersist();
        if (isNew()) {
            personStrategy.insert(newPerson, DateUtil.getCurrentDate());
        } else {
            if (!Strings.isEmpty(explanation)) {
                personStrategy.setExplanation(newPerson, explanation);
            }
            personStrategy.update(oldPerson, newPerson, DateUtil.getCurrentDate());
        }
        logBean.log(Log.STATUS.OK, Module.NAME, PersonEdit.class, isNew() ? Log.EVENT.CREATE : Log.EVENT.EDIT, personStrategy,
                oldPerson, newPerson,
                !Strings.isEmpty(explanation) ? MessageFormat.format(getString("explanation_log"), explanation) : null);
    }

    protected abstract void onSave(Person oldPerson, Person newPerson, AjaxRequestTarget target);

    protected void onCalcel(AjaxRequestTarget target) {
        onBack(target);
    }

    protected abstract void onBack(AjaxRequestTarget target);

    private List<Person> getNewChildren() {
        List<Person> oldChildren = oldPerson.getChildren();
        List<Person> newChildren = newPerson.getChildren();

        List<Person> newChildrenList = newArrayList();
        for (Person newChild : newChildren) {
            boolean isAdded = true;
            for (Person oldChild : oldChildren) {
                if (newChild.getId().equals(oldChild.getId())) {
                    isAdded = false;
                    break;
                }
            }
            if (isAdded) {
                newChildrenList.add(newChild);
            }
        }
        return newChildrenList;
    }

    private List<PersonApartmentCardAddress> getPersonApartmentCardAddresses(List<Person> children) {
        List<PersonApartmentCardAddress> personApartmentCardAddresses = personStrategy.findPersonApartmentCardAddresses(newPerson.getId());
        if (personApartmentCardAddresses == null || personApartmentCardAddresses.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, List<Long>> childrenRegistrationsMap = newHashMap();
        for (Person child : children) {
            List<PersonApartmentCardAddress> childApartmentCardAddresses =
                    personStrategy.findPersonApartmentCardAddresses(child.getId());
            childrenRegistrationsMap.put(child.getId(), transform(childApartmentCardAddresses,
                    new Function<PersonApartmentCardAddress, Long>() {

                        @Override
                        public Long apply(PersonApartmentCardAddress apartmentCardAddress) {
                            return apartmentCardAddress.getApartmentCardId();
                        }
                    }));
        }

        if (childrenRegistrationsMap.isEmpty()) {
            return personApartmentCardAddresses;
        }

        List<PersonApartmentCardAddress> finalApartmentCardAddresses = newArrayList();
        for (PersonApartmentCardAddress apartmentCardAddress : personApartmentCardAddresses) {
            boolean suit = false;
            for (long childId : childrenRegistrationsMap.keySet()) {
                if (!childrenRegistrationsMap.get(childId).contains(apartmentCardAddress.getApartmentCardId())) {
                    suit = true;
                }
            }
            if (suit) {
                finalApartmentCardAddresses.add(apartmentCardAddress);
            }
        }
        return finalApartmentCardAddresses;
    }

    private String needExplanationLabel() {
        if (isNew()) {
            return null;
        }

        Set<String> modifiedAttributes = newHashSet();
        for (final long nameAttributeTypeId : NAME_ATTRIBUTE_IDS) {
            if (personStrategy.isNameAttributeModified(oldPerson, newPerson, nameAttributeTypeId)) {
                modifiedAttributes.add(labelModel(personStrategy.getEntity().
                        getAttributeType(nameAttributeTypeId).getAttributeNames(), getLocale()).getObject());
            }
        }

        if (newPerson.getReplacedDocument() != null) {
            modifiedAttributes.add(labelModel(personStrategy.getEntity().getAttributeType(DOCUMENT).getAttributeNames(),
                    getLocale()).getObject());
        }


        if (modifiedAttributes.isEmpty()) {
            return null;
        }

        StringBuilder attributes = new StringBuilder();
        for (Iterator<String> i = modifiedAttributes.iterator(); i.hasNext();) {
            attributes.append("'").append(i.next()).append("'").append(i.hasNext() ? ", " : "");
        }
        return MessageFormat.format(getString("need_explanation_label"), attributes.toString());
    }
}
