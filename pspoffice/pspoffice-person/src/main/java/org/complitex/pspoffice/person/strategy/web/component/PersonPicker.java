package org.complitex.pspoffice.person.strategy.web.component;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.util.string.Strings;
import org.complitex.dictionary.util.StringUtil;
import org.complitex.dictionary.web.component.back.BackInfoManager;
import org.complitex.dictionary.web.component.type.GenderPanel;
import org.complitex.pspoffice.document.strategy.DocumentStrategy;
import org.complitex.pspoffice.person.menu.PersonMenu;
import org.complitex.pspoffice.person.strategy.PersonStrategy;
import org.complitex.pspoffice.person.strategy.entity.Person;
import org.complitex.pspoffice.person.strategy.entity.PersonAgeType;
import org.complitex.pspoffice.person.strategy.entity.PersonName.PersonNameType;
import org.complitex.pspoffice.person.strategy.web.edit.apartment_card.ApartmentCardBackInfo;
import org.complitex.pspoffice.person.strategy.web.edit.apartment_card.ApartmentCardEdit;
import org.complitex.pspoffice.person.strategy.web.edit.person.PersonEditPanel;
import org.complitex.pspoffice.person.util.PersonDateFormatter;
import org.complitex.template.web.template.MenuManager;
import org.complitex.template.web.template.TemplatePage;
import org.odlabs.wiquery.core.javascript.JsStatement;
import org.odlabs.wiquery.ui.core.JsScopeUiEvent;

import javax.ejb.EJB;
import java.util.List;

/**
 *
 * @author Artem
 */
public final class PersonPicker extends FormComponentPanel<Person> {

    @EJB
    private PersonStrategy personStrategy;
    @EJB
    private DocumentStrategy documentStrategy;
    private final boolean enabled;
    private final boolean required;
    private final IModel<String> labelModel;
    private final PersonAgeType personAgeType;
    private final Long apartmentCardId;
    private AjaxLink<Void> createNew;

    private static class Dialog extends org.odlabs.wiquery.ui.dialog.Dialog {

        private Dialog(String id) {
            super(id);
            getOptions().putLiteral("width", "auto");
        }
    }

    public PersonPicker(String id, PersonAgeType personAgeType, IModel<Person> model, boolean required,
            IModel<String> labelModel, boolean enabled) {
        super(id, model);
        this.required = required;
        this.enabled = enabled;
        this.labelModel = labelModel;
        this.personAgeType = personAgeType;
        this.apartmentCardId = null;
        init();
    }

    public PersonPicker(String id, PersonAgeType personAgeType, IModel<Person> model, boolean required,
            IModel<String> labelModel, boolean enabled, long apartmentCardId) {
        super(id, model);
        this.required = required;
        this.enabled = enabled;
        this.labelModel = labelModel;
        this.personAgeType = personAgeType;
        this.apartmentCardId = apartmentCardId;
        init();
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(CssHeaderItem.forReference(new PackageResourceReference(
                PersonPicker.class, PersonPicker.class.getSimpleName() + ".css")));
        response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
                PersonPicker.class, PersonPicker.class.getSimpleName() + ".js")));
    }

    private void init() {
        setRequired(required);
        setLabel(labelModel);

        final Link<Void> personLink = new Link<Void>("personLink") {

            @Override
            public void onClick() {
                Person person = PersonPicker.this.getModelObject();

                MenuManager.setMenuItem(PersonMenu.PERSON_MENU_ITEM);
                PageParameters params = personStrategy.getEditPageParams(person.getId(), null, null);
                BackInfoManager.put(this, ApartmentCardEdit.PAGE_SESSION_KEY, new ApartmentCardBackInfo(apartmentCardId, null));
                params.set(TemplatePage.BACK_INFO_SESSION_KEY, ApartmentCardEdit.PAGE_SESSION_KEY);
                setResponsePage(personStrategy.getEditPage(), params);
            }
        };
        personLink.setOutputMarkupPlaceholderTag(true);

        final Label personLabel = new Label("personLabel",
                new AbstractReadOnlyModel<String>() {

                    @Override
                    public String getObject() {
                        Person person = getModelObject();
                        if (person != null) {
                            return personStrategy.displayDomainObject(person, getLocale());
                        } else {
                            return getString("person_not_selected");
                        }
                    }
                });
        personLabel.setOutputMarkupPlaceholderTag(true);

        final Label personLinkLabel = new Label("personLinkLabel",
                new AbstractReadOnlyModel<String>() {

                    @Override
                    public String getObject() {
                        Person person = getModelObject();
                        return personStrategy.displayDomainObject(person, getLocale());
                    }
                });
        personLink.add(personLinkLabel);

        final WebMarkupContainer personLabelContainer = new WebMarkupContainer("personLabelContainer") {

            @Override
            protected void onBeforeRender() {
                super.onBeforeRender();
                Person person = getModelObject();
                if (person == null || apartmentCardId == null) {
                    personLink.setVisible(false);
                    personLabel.setVisible(true);
                } else {
                    personLabel.setVisible(false);
                    personLink.setVisible(true);
                }
            }
        };
        personLabelContainer.setOutputMarkupId(true);
        personLabelContainer.add(personLabel);
        personLabelContainer.add(personLink);
        add(personLabelContainer);

        //lookup dialog
        final Dialog lookupDialog = new Dialog("lookupDialog");
        lookupDialog.setModal(true);
        lookupDialog.setOpenEvent(JsScopeUiEvent.quickScope(new JsStatement().self().chain("parents", "'.ui-dialog:first'").
                chain("find", "'.ui-dialog-titlebar-close'").
                chain("hide").render()));
        lookupDialog.setCloseOnEscape(false);
        add(lookupDialog);

        //content
        final WebMarkupContainer content = new WebMarkupContainer("content");
        content.setOutputMarkupId(true);
        lookupDialog.add(content);

        //form
        Form<Void> filterForm = new Form<Void>("filterForm");
        content.add(filterForm);

        //last name
        final IModel<String> lastNameModel = new Model<String>();
        final PersonNameAutocompleteComponent lastName = new PersonNameAutocompleteComponent("lastName", null, lastNameModel,
                PersonNameType.LAST_NAME, getLocale(), false);

        filterForm.add(lastName);

        //first name
        final IModel<String> firstNameModel = new Model<String>();
        PersonNameAutocompleteComponent firstName = new PersonNameAutocompleteComponent("firstName", null, firstNameModel,
                PersonNameType.FIRST_NAME, getLocale(), false);
        filterForm.add(firstName);

        //middle name
        final IModel<String> middleNameModel = new Model<String>();
        PersonNameAutocompleteComponent middleName = new PersonNameAutocompleteComponent("middleName", null, middleNameModel,
                PersonNameType.MIDDLE_NAME, getLocale(), false);
        filterForm.add(middleName);

        //personContainer
        final WebMarkupContainer personContainer = new WebMarkupContainer("personContainer");
        personContainer.setOutputMarkupPlaceholderTag(true);
        content.add(personContainer);

        //personNotFoundContainer
        final WebMarkupContainer personNotFoundContainer = new WebMarkupContainer("personNotFoundContainer");
        personNotFoundContainer.setVisible(false);
        personContainer.add(personNotFoundContainer);

        //personsDataContainer
        final WebMarkupContainer personsDataContainer = new WebMarkupContainer("personsDataContainer");
        personsDataContainer.setVisible(false);
        personContainer.add(personsDataContainer);

        final IModel<List<? extends Person>> personsModel = Model.ofList(null);
        final IModel<Person> personModel = new Model<Person>();

        //select
        final AjaxLink<Void> select = new AjaxLink<Void>("select") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                if (personModel.getObject() == null) {
                    throw new IllegalStateException("Oops...");
                } else {
                    PersonPicker.this.getModel().setObject(personModel.getObject());
                    clearAndCloseLookupDialog(personModel, personsModel,
                            target, lookupDialog, content, personNotFoundContainer, personsDataContainer, this);
                    target.add(personLabelContainer);
                }
            }
        };
        select.setOutputMarkupPlaceholderTag(true);
        select.setVisible(false);
        content.add(select);

        //person creation dialog
        final Dialog personCreationDialog = new Dialog("personCreationDialog");
        personCreationDialog.setModal(true);
        personCreationDialog.setOpenEvent(JsScopeUiEvent.quickScope(new JsStatement().self().chain("parents", "'.ui-dialog:first'").
                chain("find", "'.ui-dialog-titlebar-close'").
                chain("hide").render()));
        personCreationDialog.setCloseOnEscape(false);
        add(personCreationDialog);

        //person edit container
        final WebMarkupContainer personEditContainer = new WebMarkupContainer("personEditContainer");
        personEditContainer.setOutputMarkupId(true);
        personCreationDialog.add(personEditContainer);

        //person edit panel
        personEditContainer.add(new EmptyPanel("personEditPanel"));

        //create new
        createNew = new AjaxLink<Void>("createNew") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                clearAndCloseLookupDialog(personModel, personsModel,
                        target, lookupDialog, content, personNotFoundContainer, personsDataContainer, select);

                personEditContainer.replace(newPersonEditPanel(personCreationDialog, personEditContainer, personLabelContainer,
                        lastNameModel.getObject(), firstNameModel.getObject(), middleNameModel.getObject()));

                target.add(personEditContainer);
                personCreationDialog.open(target);
            }
        };
        createNew.setOutputMarkupPlaceholderTag(true);
        createNew.setVisible(false);
        content.add(createNew);

        //radio group
        final RadioGroup<Person> radioGroup = new RadioGroup<Person>("radioGroup", personModel);
        radioGroup.add(new AjaxFormChoiceComponentUpdatingBehavior() {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                toggleSelectButton(select, target, personModel);
            }
        });
        personsDataContainer.add(radioGroup);

        //persons list view
        ListView<Person> persons = new ListView<Person>("persons", personsModel) {

            @Override
            protected void populateItem(ListItem<Person> item) {
                Person person = item.getModelObject();
                item.add(new Radio<Person>("radio", item.getModel(), radioGroup));
                item.add(new Label("fullName", personStrategy.displayDomainObject(person, getLocale())));
                item.add(new Label("birthDate", PersonDateFormatter.format(person.getBirthDate())));
                item.add(new Label("gender", GenderPanel.display(person.getGender(), getLocale())));
                item.add(new Label("idCode", StringUtil.valueOf(person.getIdentityCode())));
                item.add(new Label("document", documentStrategy.displayDomainObject(person.getDocument(), null)));
            }
        };
        radioGroup.add(persons);

        //find
        IndicatingAjaxButton find = new IndicatingAjaxButton("find", filterForm) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                find(lastNameModel.getObject(), firstNameModel.getObject(), middleNameModel.getObject(), personModel,
                        personsModel);

                boolean isPersonsDataContainerVisible = personsModel.getObject() != null && !personsModel.getObject().isEmpty();
                personNotFoundContainer.setVisible(!isPersonsDataContainerVisible);
                personsDataContainer.setVisible(isPersonsDataContainerVisible);

                target.add(personContainer);
                toggleSelectButton(select, target, personModel);
                if (!isPersonsDataContainerVisible) {
                    target.focusComponent(lastName.getAutocompleteField());
                }
                createNew.setVisible(true);
                target.add(createNew);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
            }
        };
        filterForm.add(find);

        //cancel
        AjaxLink<Void> cancel = new AjaxLink<Void>("cancel") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                clearAndCloseLookupDialog(personModel, personsModel,
                        target, lookupDialog, content, personNotFoundContainer, personsDataContainer, select);
            }
        };
        content.add(cancel);

        //choose
        AjaxLink<Void> choose = new AjaxLink<Void>("choose") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                lookupDialog.open(target);
            }
        };
        choose.setVisible(enabled);
        add(choose);
    }

    private PersonEditPanel newPersonEditPanel(final Dialog personCreationDialog, final WebMarkupContainer personEditContainer,
            final WebMarkupContainer personLabelContainer, String lastName, String firstName, String middleName) {
        return new PersonEditPanel("personEditPanel", personStrategy.newInstance(), personAgeType,
                getLocale(), lastName, firstName, middleName) {

            @Override
            protected void onSave(Person oldPerson, Person newPerson, AjaxRequestTarget target) {
                Person createdPerson = personStrategy.findById(newPerson.getId(), false, true, false, false, false);
                PersonPicker.this.getModel().setObject(createdPerson);

                target.add(personLabelContainer);
                this.onBack(target);
            }

            @Override
            protected void onBack(AjaxRequestTarget target) {
                personEditContainer.replace(new EmptyPanel("personEditPanel"));
                target.add(personEditContainer);
                personCreationDialog.close(target);
            }
        };
    }

    private void clearAndCloseLookupDialog(IModel<Person> personModel, IModel<List<? extends Person>> personsModel,
            AjaxRequestTarget target, Dialog lookupDialog, WebMarkupContainer content,
            WebMarkupContainer personNotFoundContainer, WebMarkupContainer personsDataContainer, Component select) {
        personsModel.setObject(null);
        personModel.setObject(null);
        select.setVisible(false);
        createNew.setVisible(false);
        personNotFoundContainer.setVisible(false);
        personsDataContainer.setVisible(false);

        target.add(content);
        lookupDialog.close(target);
    }

    private void toggleSelectButton(Component select, AjaxRequestTarget target, IModel<Person> personModel) {
        boolean wasVisible = select.isVisible();
        select.setVisible(personModel.getObject() != null);
        if (select.isVisible() ^ wasVisible) {
            target.add(select);
        }
    }

    private void find(String lastName, String firstName, String middleName, IModel<Person> personModel,
            IModel<List<? extends Person>> personsModel) {
        if (Strings.isEmpty(lastName)) {
            personsModel.setObject(null);
        } else {
            personsModel.setObject(personStrategy.findByName(personAgeType, lastName, firstName, middleName, getLocale()));
        }
        personModel.setObject(null);
    }

    @Override
    protected void convertInput() {
        setConvertedInput(getModelObject());
    }
}
