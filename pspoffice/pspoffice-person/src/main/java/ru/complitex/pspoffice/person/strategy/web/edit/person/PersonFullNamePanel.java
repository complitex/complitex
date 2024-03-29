/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.complitex.pspoffice.person.strategy.web.edit.person;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.string.Strings;
import ru.complitex.common.entity.Attribute;
import ru.complitex.common.strategy.StringLocaleBean;
import ru.complitex.common.web.component.DomainObjectComponentUtil;
import ru.complitex.pspoffice.person.strategy.PersonStrategy;
import ru.complitex.pspoffice.person.strategy.entity.Person;
import ru.complitex.pspoffice.person.strategy.entity.PersonName.PersonNameType;
import ru.complitex.pspoffice.person.strategy.web.component.PersonNameAutocompleteComponent;

import javax.ejb.EJB;
import java.util.Locale;

/**
 *
 * @author Artem
 */
class PersonFullNamePanel extends Panel {
    @EJB
    private StringLocaleBean stringLocaleBean;

    @EJB
    private PersonStrategy personStrategy;

    PersonFullNamePanel(String id, Person person) {
        super(id);
        init(person, null, null, null, null);
    }

    PersonFullNamePanel(String id, Person person, Locale defaultNameLocale, String defaultLastName, String defaultFirstName,
            String defaultMiddleName) {
        super(id);
        init(person, defaultNameLocale, defaultLastName, defaultFirstName, defaultMiddleName);
    }

    protected void init(Person person, final Locale defaultNameLocale, final String defaultLastName, final String defaultFirstName,
            final String defaultMiddleName) {
        final IModel<String> lastNameLabelModel = newLabelModel(PersonStrategy.LAST_NAME);
        add(new Label("lastNameLabel", lastNameLabelModel));
        add(new ListView<Attribute>("lastNames", person.getAttributes(PersonStrategy.LAST_NAME)) {

            @Override
            protected void populateItem(ListItem<Attribute> item) {
                PersonFullNamePanel.this.populateItem(item, PersonNameType.LAST_NAME, "lastName", lastNameLabelModel,
                        defaultNameLocale, defaultLastName);
            }
        });

        final IModel<String> firstNameLabelModel = newLabelModel(PersonStrategy.FIRST_NAME);
        add(new Label("firstNameLabel", firstNameLabelModel));
        add(new ListView<Attribute>("firstNames", person.getAttributes(PersonStrategy.FIRST_NAME)) {

            @Override
            protected void populateItem(ListItem<Attribute> item) {
                PersonFullNamePanel.this.populateItem(item, PersonNameType.FIRST_NAME, "firstName", firstNameLabelModel,
                        defaultNameLocale, defaultFirstName);
            }
        });

        final IModel<String> middleNameLabelModel = newLabelModel(PersonStrategy.MIDDLE_NAME);
        add(new Label("middleNameLabel", middleNameLabelModel));
        add(new ListView<Attribute>("middleNames", person.getAttributes(PersonStrategy.MIDDLE_NAME)) {

            @Override
            protected void populateItem(ListItem<Attribute> item) {
                PersonFullNamePanel.this.populateItem(item, PersonNameType.MIDDLE_NAME, "middleName", middleNameLabelModel,
                        defaultNameLocale, defaultMiddleName);
            }
        });
    }

    private IModel<String> newLabelModel(long nameAttributeTypeId) {
        return DomainObjectComponentUtil.labelModel(personStrategy.getEntity().getAttribute(nameAttributeTypeId).getNames(),
                getLocale());
    }

    private void populateItem(ListItem<Attribute> item, PersonNameType personNameType, String personNameComponentId,
            IModel<String> labelModel, Locale defaultNameLocale, String defaultNameValue) {
        Attribute personNameAttribute = item.getModelObject();
        Locale locale = stringLocaleBean.getLocale(personNameAttribute.getAttributeId());
        boolean isSystemLocale = stringLocaleBean.getLocaleObject(personNameAttribute.getAttributeId()).isSystem();
        final PersonNameAutocompleteComponent personNameComponent =
                (!Strings.isEmpty(defaultNameValue) && defaultNameLocale != null
                && stringLocaleBean.convert(defaultNameLocale).getId().equals(personNameAttribute.getAttributeId()))
                ? new PersonNameAutocompleteComponent(personNameComponentId, newNameModel(personNameAttribute), new Model<String>(defaultNameValue),
                personNameType, locale, true)
                : new PersonNameAutocompleteComponent(personNameComponentId, newNameModel(personNameAttribute), personNameType, locale, true);
        personNameComponent.setRequired(isSystemLocale);
        personNameComponent.setLabel(labelModel);
        item.add(personNameComponent);
        Label language = new Label("language", locale.getDisplayLanguage(getLocale()));
        item.add(language);
        WebMarkupContainer requiredContainer = new WebMarkupContainer("required");
        requiredContainer.setVisible(isSystemLocale);
        item.add(requiredContainer);
    }

    private IModel<Long> newNameModel(final Attribute nameAttribute) {
        return new Model<Long>() {

            @Override
            public Long getObject() {
                return nameAttribute.getValueId();
            }

            @Override
            public void setObject(Long object) {
                nameAttribute.setValueId(object);
            }
        };
    }
}
