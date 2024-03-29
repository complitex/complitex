package ru.complitex.pspoffice.person.strategy.web.edit.person;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import ru.complitex.address.service.AddressRendererBean;
import ru.complitex.common.entity.Attribute;
import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.entity.EntityAttribute;
import ru.complitex.common.entity.Status;
import ru.complitex.common.strategy.StringValueBean;
import ru.complitex.common.util.StringValueUtil;
import ru.complitex.common.web.component.DisableAwareDropDownChoice;
import ru.complitex.common.web.component.DomainObjectDisableAwareRenderer;
import ru.complitex.common.web.component.DomainObjectInputPanel;
import ru.complitex.common.web.component.domain.DomainObjectAccessUtil;
import ru.complitex.common.web.component.fieldset.CollapsibleFieldset;
import ru.complitex.common.web.component.fieldset.ICollapsibleFieldsetListener;
import ru.complitex.common.web.component.list.AjaxRemovableListView;
import ru.complitex.common.web.component.scroll.ScrollToElementUtil;
import ru.complitex.pspoffice.document.strategy.DocumentStrategy;
import ru.complitex.pspoffice.document.strategy.entity.Document;
import ru.complitex.pspoffice.document_type.strategy.DocumentTypeStrategy;
import ru.complitex.pspoffice.military.strategy.MilitaryServiceRelationStrategy;
import ru.complitex.pspoffice.ownerrelationship.strategy.OwnerRelationshipStrategy;
import ru.complitex.pspoffice.person.strategy.PersonStrategy;
import ru.complitex.pspoffice.person.strategy.entity.Person;
import ru.complitex.pspoffice.person.strategy.entity.PersonAgeType;
import ru.complitex.pspoffice.person.strategy.web.component.PersonPicker;
import ru.complitex.pspoffice.person.util.PersonDateFormatter;
import ru.complitex.pspoffice.registration_type.strategy.RegistrationTypeStrategy;
import org.odlabs.wiquery.ui.dialog.Dialog;
import org.odlabs.wiquery.ui.effects.CoreEffectJavaScriptResourceReference;
import org.odlabs.wiquery.ui.effects.SlideEffectJavaScriptResourceReference;

import javax.ejb.EJB;
import java.text.MessageFormat;
import java.util.*;

import static com.google.common.collect.Iterables.*;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static ru.complitex.common.web.component.DomainObjectComponentUtil.labelModel;
import static ru.complitex.common.web.component.DomainObjectComponentUtil.newInputComponent;
import static ru.complitex.pspoffice.person.strategy.PersonStrategy.*;

/**
 *
 * @author Artem
 */
public class PersonInputPanel extends Panel {

    @EJB
    private StringValueBean stringBean;
    @EJB
    private PersonStrategy personStrategy;
    @EJB
    private DocumentStrategy documentStrategy;
    @EJB
    private DocumentTypeStrategy documentTypeStrategy;
    @EJB
    private AddressRendererBean addressRendererBean;
    @EJB
    private RegistrationTypeStrategy registrationTypeStrategy;
    @EJB
    private OwnerRelationshipStrategy ownerRelationshipStrategy;
    @EJB
    private MilitaryServiceRelationStrategy militaryServiceRelationStrategy;
    private Person person;
    private FeedbackPanel messages;
    private Component scrollToComponent;
    private boolean documentReplacedFlag;
    private final PersonAgeType personAgeType;

    public PersonInputPanel(String id, Person person, FeedbackPanel messages, Component scrollToComponent, PersonAgeType personAgeType,
            Locale defaultNameLocale, String defaultLastName, String defaultFirstName, String defaultMiddleName) {
        super(id);
        this.person = person;
        this.messages = messages;
        this.scrollToComponent = scrollToComponent;
        if (!isNew() && !person.isKid()) {
            this.personAgeType = PersonAgeType.ADULT;
        } else {
            this.personAgeType = personAgeType;
        }
        init(defaultNameLocale, defaultLastName, defaultFirstName, defaultMiddleName);
    }

    private boolean isNew() {
        return person.getObjectId() == null;
    }

    private boolean canEdit() {
        return DomainObjectAccessUtil.canEdit(null, personStrategy.getEntityName(), person);
    }

    private boolean isInactive() {
        return person.getStatus() != Status.ACTIVE;
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(JavaScriptHeaderItem.forReference(CoreEffectJavaScriptResourceReference.get()));
        response.render(JavaScriptHeaderItem.forReference(SlideEffectJavaScriptResourceReference.get()));
    }

    private void init(Locale defaultNameLocale, String defaultLastName, String defaultFirstName, String defaultMiddleName) {
        //full name:
        PersonFullNamePanel personFullNamePanel = defaultNameLocale != null ? new PersonFullNamePanel("personFullNamePanel", person,
                defaultNameLocale, defaultLastName, defaultFirstName, defaultMiddleName)
                : new PersonFullNamePanel("personFullNamePanel", person);
        personFullNamePanel.setEnabled(canEdit());
        add(personFullNamePanel);

        //system attributes:
        initSystemAttributeInput(this, "identityCode", IDENTITY_CODE, false);
        initSystemAttributeInput(this, "birthDate", BIRTH_DATE, true);
//        final MaskedDateInput birthDateComponent = (MaskedDateInput) get("birthDateContainer:"
//                + DomainObjectComponentUtil.INPUT_COMPONENT_ID + ":" + MaskedDateInputPanel.DATE_INPUT_ID);
//
//        switch (personAgeType) {
//            case KID:
//                birthDateComponent.setMinDate(DateUtil.add(DateUtil.getCurrentDate(), -AGE_THRESHOLD));
//                birthDateComponent.setMaxDate(DateUtil.getCurrentDate());
//                break;
//            case ADULT:
//                birthDateComponent.setMaxDate(DateUtil.add(DateUtil.getCurrentDate(), -AGE_THRESHOLD));
//                break;
//            case ANY:
//                birthDateComponent.setMaxDate(DateUtil.getCurrentDate());
//                break;
//        }

        initSystemAttributeInput(this, "gender", GENDER, false);

        CollapsibleFieldset birthPlaceFieldset = new CollapsibleFieldset("birthPlaceFieldset", new ResourceModel("birthPlaceLabel"));
        birthPlaceFieldset.setVisible(isBirthPlaceFieldsetVisible());
        add(birthPlaceFieldset);
        initSystemAttributeInput(birthPlaceFieldset, "birthCountry", BIRTH_COUNTRY, false);
        initSystemAttributeInput(birthPlaceFieldset, "birthRegion", BIRTH_REGION, false);
        initSystemAttributeInput(birthPlaceFieldset, "birthDistrict", BIRTH_DISTRICT, false);
        initSystemAttributeInput(birthPlaceFieldset, "birthCity", BIRTH_CITY, false);
        initSystemAttributeInput(this, "ukraineCitizenship", UKRAINE_CITIZENSHIP, false);
        final WebMarkupContainer deathDateZone = new WebMarkupContainer("deathDateZone");
        deathDateZone.setVisible(isInactive());
        add(deathDateZone);
        initSystemAttributeInput(deathDateZone, "deathDate", DEATH_DATE, false);

        //military service
        add(initMilitaryServiceRelation());

        //document
        add(initDocument());

        //user attributes:
        add(initUserAttributes());

        //children
        final Component childrentComponent = initChildren();
        childrentComponent.setOutputMarkupPlaceholderTag(true);
        childrentComponent.setVisible(
                (personAgeType == PersonAgeType.ADULT) || (personAgeType == PersonAgeType.ANY && !person.isKid()));
        add(childrentComponent);

//        birthDateComponent.add(new AjaxFormComponentUpdatingBehavior("blur") {
//
//            @Override
//            protected void onUpdate(AjaxRequestTarget target) {
//                boolean showAdultComponents = (personAgeType == PersonAgeType.ADULT)
//                        || (personAgeType == PersonAgeType.ANY && !person.isKid())
//                        || person.hasChildren();
//                updateChildrenComponent(target, showAdultComponents);
//                updateMilitaryServiceRelationComponent(target, showAdultComponents);
//            }
//
//            @Override
//            protected void onError(AjaxRequestTarget target, RuntimeException e) {
//                super.onError(target, e);
//                getSession().getFeedbackMessages().clear(new IFeedbackMessageFilter() {
//
//                    @Override
//                    public boolean accept(FeedbackMessage message) {
//                        return message.getReporter() == birthDateComponent && message.isError();
//                    }
//                });
//            }
//
//            private void updateMilitaryServiceRelationComponent(AjaxRequestTarget target, boolean visible) {
//                boolean wasVisible = militaryServiceRelationHead.isVisible();
//                militaryServiceRelationHead.setVisible(visible);
//                militaryServiceRelationBody.setVisible(visible);
//                if (wasVisible ^ visible) {
//                    target.add(militaryServiceRelationHead);
//                    target.add(militaryServiceRelationBody);
//                }
//            }
//
//            private void updateChildrenComponent(AjaxRequestTarget target, boolean visible) {
//                boolean wasVisible = childrentComponent.isVisible();
//                childrentComponent.setVisible(visible);
//                if (wasVisible ^ visible) {
//                    target.add(childrentComponent);
//                }
//            }
//        });

        //registrations
        add(initRegistrationsFieldset());
    }

    private Component initUserAttributes() {
        List<Long> userAttributeTypeIds = newArrayList(transform(filter(personStrategy.getEntity().getAttributes(),
                new Predicate<EntityAttribute>() {

                    @Override
                    public boolean apply(EntityAttribute attributeType) {
                        return !attributeType.isSystem();
                    }
                }),
                new Function<EntityAttribute, Long>() {

                    @Override
                    public Long apply(EntityAttribute attributeType) {
                        return attributeType.getId();
                    }
                }));

        List<Attribute> userAttributes = newArrayList();
        for (Long entityAttributeId : userAttributeTypeIds) {
            Attribute userAttribute = person.getAttribute(entityAttributeId);
            if (userAttribute != null) {
                userAttributes.add(userAttribute);
            }
        }

        ListView<Attribute> userAttributesView = new ListView<Attribute>("userAttributesView", userAttributes) {

            @Override
            protected void populateItem(ListItem<Attribute> item) {
                long userAttributeTypeId = item.getModelObject().getEntityAttributeId();
                initAttributeInput(item, userAttributeTypeId, false);
            }
        };
        return userAttributesView;
    }

    private void initSystemAttributeInput(MarkupContainer parent, String id, long entityAttributeId, boolean showIfMissing) {
        WebMarkupContainer container = new WebMarkupContainer(id + "Container");
        parent.add(container);
        initAttributeInput(container, entityAttributeId, showIfMissing);
    }

    private boolean isBirthPlaceFieldsetVisible() {
        return canEdit() || (person.getAttribute(BIRTH_COUNTRY) != null) || (person.getAttribute(BIRTH_DISTRICT) != null)
                || (person.getAttribute(BIRTH_REGION) != null) || (person.getAttribute(BIRTH_CITY) != null);
    }

    @SuppressWarnings("Duplicates")
    private void initAttributeInput(MarkupContainer parent, long entityAttributeId, boolean showIfMissing) {
        EntityAttribute entityAttribute = personStrategy.getEntity().getAttribute(entityAttributeId);

        //label
        parent.add(new Label("label", labelModel(entityAttribute.getNames(), getLocale())));

        //required container
        WebMarkupContainer requiredContainer = new WebMarkupContainer("required");
        requiredContainer.setVisible(entityAttribute.isRequired());
        parent.add(requiredContainer);

        //input component
        Attribute attribute = person.getAttribute(entityAttributeId);
        if (attribute == null) {
            attribute = new Attribute();
            attribute.setStringValues(StringValueUtil.newStringValues());
            attribute.setEntityAttributeId(entityAttributeId);
            parent.setVisible(showIfMissing);
        }
        parent.add(newInputComponent(personStrategy.getEntityName(), null, person, attribute, getLocale(), isInactive()));
    }

    public void beforePersist() {
        //military service relation
        person.removeAttribute(MILITARY_SERVICE_RELATION);
        final DomainObject militaryServiceRelation = person.getMilitaryServiceRelation();
        if (militaryServiceRelation != null) {
            Attribute militaryServiceRelationAttribute = new Attribute();
            militaryServiceRelationAttribute.setAttributeId(1L);
            militaryServiceRelationAttribute.setEntityAttributeId(MILITARY_SERVICE_RELATION);
            militaryServiceRelationAttribute.setValueId(militaryServiceRelation.getObjectId());
            person.addAttribute(militaryServiceRelationAttribute);
        }

        //children
        person.getAttributes().removeAll(Collections2.filter(person.getAttributes(), new Predicate<Attribute>() {

            @Override
            public boolean apply(Attribute attr) {
                return attr.getEntityAttributeId().equals(CHILDREN);
            }
        }));
        if (!person.isKid()) {
            long attributeId = 1;
            for (Person child : person.getChildren()) {
                Attribute childrenAttribute = new Attribute();
                childrenAttribute.setAttributeId(attributeId++);
                childrenAttribute.setEntityAttributeId(CHILDREN);
                childrenAttribute.setValueId(child.getObjectId());
                person.addAttribute(childrenAttribute);
            }
        }
    }

    private boolean validateDocumentIssuedDate() {
        final Date birthDate = person.getBirthDate();
        final Document document = !documentReplacedFlag ? person.getDocument() : person.getReplacedDocument();
        final Date documentIssued = document.getDateIssued();
        if (documentIssued != null && birthDate.after(documentIssued)) {
            error(getString("document_issued_later_birth_date"));
            return false;
        }
        return true;
    }

    public boolean validate() {
        //birth date and PersonAgeType
        if (!person.isKid() && personAgeType == PersonAgeType.KID) {
            error(getString("must_be_kid"));
        }
        if (person.isKid() && personAgeType == PersonAgeType.ADULT) {
            error(getString("must_be_adult"));
        }

        //if birth date and PersonAgeType are not correlating then further validation is not make sense.
        if (!getSession().getFeedbackMessages().isEmpty()) {
            return false;
        }

        //document
        Document document = !documentReplacedFlag ? person.getDocument() : person.getReplacedDocument();
        if (document == null) {
            error(getString("empty_document"));
        } else {
            if (!document.isKidDocument() && person.isKid()) {
                error(MessageFormat.format(getString("kid_document_type_error"),
                        documentTypeStrategy.displayDomainObject(documentTypeModel.getObject(), getLocale()).toLowerCase(getLocale())));
            }
            if (!document.isAdultDocument() && !person.isKid()) {
                error(MessageFormat.format(getString("adult_document_type_error"),
                        documentTypeStrategy.displayDomainObject(documentTypeModel.getObject(), getLocale()).toLowerCase(getLocale())));
            }

            //document issued date must be later then person's birth date
            validateDocumentIssuedDate();
        }

        //children
        if (person.hasChildren() && person.isKid()) {
            error(getString("kid_has_children_error"));
        }
        Collection<Person> nonNullChildren = newArrayList(filter(person.getChildren(), new Predicate<Person>() {

            @Override
            public boolean apply(Person child) {
                return child != null && child.getObjectId() != null && child.getObjectId() > 0;
            }
        }));
        if (nonNullChildren.size() != person.getChildren().size()) {
            error(getString("children_error"));
        }

        Set<Long> childrenIds = newHashSet(transform(nonNullChildren, new Function<Person, Long>() {

            @Override
            public Long apply(Person child) {
                return child.getObjectId();
            }
        }));

        if (!isNew()) {
            if (childrenIds.contains(person.getObjectId())) {
                error(getString("references_themselves"));
            }
        }

        if (childrenIds.size() != nonNullChildren.size()) {
            error(getString("children_duplicate"));
        }

        return getSession().getFeedbackMessages().isEmpty();
    }

    private Component initChildren() {
        CollapsibleFieldset childrenFieldset = new CollapsibleFieldset("childrenFieldset",
                labelModel(personStrategy.getEntity().getAttribute(CHILDREN).getNames(), getLocale()));
        add(childrenFieldset);
        final WebMarkupContainer childrenContainer = new WebMarkupContainer("childrenContainer");
        childrenContainer.setOutputMarkupId(true);
        childrenFieldset.add(childrenContainer);
        ListView<Person> children = new AjaxRemovableListView<Person>("children", person.getChildren()) {

            @Override
            protected void populateItem(ListItem<Person> item) {
                final WebMarkupContainer fakeContainer = new WebMarkupContainer("fakeContainer");
                item.add(fakeContainer);
                item.add(new Label("label", new AbstractReadOnlyModel<String>() {

                    @Override
                    public String getObject() {
                        return MessageFormat.format(getString("children_number"), getCurrentIndex(fakeContainer) + 1);
                    }
                }));

                IModel<Person> childModel = new Model<Person>() {

                    @Override
                    public Person getObject() {
                        int index = getCurrentIndex(fakeContainer);
                        return person.getChildren().get(index);
                    }

                    @Override
                    public void setObject(Person child) {
                        int index = getCurrentIndex(fakeContainer);
                        person.setChild(index, child);
                    }
                };
                childModel.setObject(item.getModelObject());

                PersonPicker personPicker = new PersonPicker("searchChildComponent", PersonAgeType.KID, childModel,
                        false, null, canEdit());
                item.add(personPicker);

                addRemoveLink("removeChild", item, null, childrenContainer).setVisible(canEdit());
            }
        };
        AjaxLink<Void> addChild = new AjaxLink<Void>("addChild") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                Person newChild = null;
                person.addChild(newChild);
                target.add(childrenContainer);
            }
        };
        addChild.setVisible(canEdit());
        childrenFieldset.add(addChild);
        childrenContainer.add(children);
        return childrenFieldset;
    }
    /**
     * Document UI fields
     */
    private IModel<DomainObject> documentTypeModel;
    private IModel<List<DomainObject>> documentTypesModel;
    private DisableAwareDropDownChoice<DomainObject> documentType;
    private WebMarkupContainer documentInputPanelContainer;

    private Component initDocument() {
        CollapsibleFieldset documentFieldset = new CollapsibleFieldset("documentFieldset",
                new ResourceModel("documentLabel"), false);

        final Form<Void> documentForm = new Form<Void>("documentForm");
        documentFieldset.add(documentForm);

        final WebMarkupContainer documentButtonsContainer = new WebMarkupContainer("documentButtonsContainer");
        documentButtonsContainer.setOutputMarkupId(true);
        documentButtonsContainer.setVisible(!isNew());
        documentFieldset.add(documentButtonsContainer);

        final WebMarkupContainer documentInputPanelWrapper = new WebMarkupContainer("documentInputPanelWrapper");
        documentInputPanelWrapper.setOutputMarkupId(true);
        documentForm.add(documentInputPanelWrapper);

        documentInputPanelContainer = new WebMarkupContainer("documentInputPanelContainer");
        documentInputPanelContainer.setOutputMarkupId(true);
        if (isNew()) {
            documentInputPanelContainer.add(new EmptyPanel("documentInputPanel"));
        } else {
            documentInputPanelContainer.add(newDocumentInputPanel(person.getDocument()));
        }
        documentInputPanelWrapper.add(documentInputPanelContainer);

        //document type
        final EntityAttribute documentTypeEntityAttribute =
                documentStrategy.getEntity().getAttribute(DocumentStrategy.DOCUMENT_TYPE);
        //label
        IModel<String> labelModel = labelModel(documentTypeEntityAttribute.getNames(), getLocale());
        documentForm.add(new Label("label", labelModel));
        //required
        documentForm.add(new WebMarkupContainer("required").setVisible(documentTypeEntityAttribute.isRequired()));
        documentTypeModel = new Model<>();
        documentTypesModel = Model.ofList(null);
        if (!isNew()) {
            documentTypesModel.setObject(documentTypeStrategy.getAll(getLocale()));
            documentTypeModel.setObject(find(documentTypesModel.getObject(), new Predicate<DomainObject>() {

                @Override
                public boolean apply(DomainObject documentType) {
                    return documentType.getObjectId().equals(person.getDocument().getDocumentTypeId());
                }
            }));
        } else {
            initDocumentTypesModel();
        }
        documentType = new DisableAwareDropDownChoice<DomainObject>("documentType",
                documentTypeModel, documentTypesModel, new DomainObjectDisableAwareRenderer() {

            @Override
            public Object getDisplayValue(DomainObject object) {
                return documentTypeStrategy.displayDomainObject(object, getLocale());
            }
        });
        documentType.setOutputMarkupId(true);
        documentType.setLabel(labelModel);
        documentType.setEnabled(isNew() && canEdit());
        documentType.add(new AjaxFormComponentUpdatingBehavior("change") {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                DomainObject newDocumentType = documentTypeModel.getObject();
                if (newDocumentType != null && newDocumentType.getObjectId() != null) {
                    documentType.setEnabled(false);
                    Document document = documentStrategy.newInstance(newDocumentType.getObjectId());
                    if (!documentReplacedFlag) {
                        person.setDocument(document);
                    } else {
                        person.setReplacedDocument(document);
                    }
                    documentInputPanelContainer.replace(newDocumentInputPanel(document));
                    target.add(documentInputPanelContainer);
                    target.add(documentType);

                    final String documentInputPanelWrapperId = documentInputPanelWrapper.getMarkupId();
                    target.prependJavaScript("$('#" + documentInputPanelWrapperId + "').hide();");
                    target.appendJavaScript("$('#" + documentInputPanelWrapperId + "').slideDown('fast',"
                            + "function(){ $('input, textarea, select', this).filter(':enabled:not(:hidden)').first().focus(); });");
                }
            }
        });
        updateDocumentTypeComponent();
        documentForm.add(documentType);

        //replace document
        AjaxSubmitLink replaceDocument = new AjaxSubmitLink("replaceDocument", documentForm) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                if (PersonInputPanel.this.validateDocumentIssuedDate()) {
                    documentReplacedFlag = true;
                    setVisible(false);
                    target.add(documentButtonsContainer);
                    documentTypeModel.setObject(null);
                    documentType.setEnabled(true);
                    initDocumentTypesModel();
                    updateDocumentTypeComponent();

                    final String documentInputPanelWrapperId = documentInputPanelWrapper.getMarkupId();
                    if (documentTypesModel.getObject().size() > 1) {
                        target.prependJavaScript("$('#" + documentInputPanelWrapperId + "').hide('slide', {}, 750);");
                    } else {
                        target.prependJavaScript("$('#" + documentInputPanelWrapperId + "').hide();");
                        target.appendJavaScript("$('#" + documentInputPanelWrapperId + "').slideDown('fast',"
                                + "function(){ $('input, textarea, select', this).filter(':enabled:not(:hidden)').first().focus(); });");
                        target.add(documentInputPanelContainer);
                    }
                    target.focusComponent(documentType);
                    target.add(documentType);
                } else {
                    target.appendJavaScript(ScrollToElementUtil.scrollTo(scrollToComponent.getMarkupId()));
                }
                target.add(messages);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(messages);
                target.appendJavaScript(ScrollToElementUtil.scrollTo(scrollToComponent.getMarkupId()));
            }
        };
        replaceDocument.setVisible(!isNew() && canEdit());
        documentButtonsContainer.add(replaceDocument);

        //previous documents
        final Dialog previousDocumentsDialog = new Dialog("previousDocumentsDialog") {

            {
                getOptions().putLiteral("width", "auto");
            }
        };
        previousDocumentsDialog.setModal(true);
        add(previousDocumentsDialog);

        IModel<List<Document>> previousDocumentsModel = new AbstractReadOnlyModel<List<Document>>() {

            private List<Document> previousDocuments;

            @Override
            public List<Document> getObject() {
                if (previousDocuments == null) {
                    previousDocuments = newArrayList();
                    List<Document> previousDocs = personStrategy.findPreviousDocuments(person.getObjectId());
                    if (previousDocs != null && !previousDocs.isEmpty()) {
                        for (Document previousDoc : previousDocs) {
                            if (!person.getDocument().getObjectId().equals(previousDoc.getObjectId())) {
                                previousDocuments.add(previousDoc);
                            }
                        }
                    }
                }
                return previousDocuments;
            }
        };

        ListView<Document> previousDocuments = new ListView<Document>("previousDocuments", previousDocumentsModel) {

            @Override
            protected void populateItem(ListItem<Document> item) {
                Document previousDocument = item.getModelObject();
                item.add(new Label("previousDocumentLabel",
                        documentTypeStrategy.displayDomainObject(previousDocument.getDocumentType(), getLocale())));
                item.add(new DomainObjectInputPanel("previousDocument", previousDocument, documentStrategy.getEntityName(),
                        null, null, null, previousDocument.getStartDate()));
            }
        };
        previousDocumentsDialog.add(previousDocuments);

        AjaxLink<Void> showPreviousDocuments = new AjaxLink<Void>("showPreviousDocuments") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                previousDocumentsDialog.open(target);
            }
        };
        showPreviousDocuments.setVisible(previousDocumentsModel.getObject() != null && !previousDocumentsModel.getObject().isEmpty());
        documentButtonsContainer.add(showPreviousDocuments);

        return documentFieldset;
    }

    private DomainObjectInputPanel newDocumentInputPanel(Document document) {
        return new DomainObjectInputPanel("documentInputPanel", document, documentStrategy.getEntityName(), null, null, null);
    }

    private void initDocumentTypesModel() {
        switch (personAgeType) {
            case KID:
                documentTypesModel.setObject(documentTypeStrategy.getKidDocumentTypes());
                break;
            case ADULT:
                documentTypesModel.setObject(documentTypeStrategy.getAdultDocumentTypes());
                break;
            case ANY:
                if (person.getBirthDate() != null) {
                    documentTypesModel.setObject(person.isKid() ? documentTypeStrategy.getKidDocumentTypes()
                            : documentTypeStrategy.getAdultDocumentTypes());
                } else {
                    documentTypesModel.setObject(documentTypeStrategy.getAll(getLocale()));
                }
                break;
        }
    }

    private void updateDocumentTypeComponent() {
        if (documentTypesModel.getObject().size() == 1) {
            documentType.setEnabled(false);
            DomainObject newDocumentType = documentTypesModel.getObject().get(0);
            documentTypeModel.setObject(newDocumentType);
            Document document = documentStrategy.newInstance(newDocumentType.getObjectId());
            if (!documentReplacedFlag) {
                person.setDocument(document);
            } else {
                person.setReplacedDocument(document);
            }
            documentInputPanelContainer.replace(newDocumentInputPanel(document));
        }
    }

    private Component initRegistrationsFieldset() {
        final int countPersonRegistrations = isNew() ? 0 : personStrategy.countPersonRegistrations(person.getObjectId());

        final WebMarkupContainer content = new WebMarkupContainer("content");
        content.setOutputMarkupPlaceholderTag(true);
        content.setVisible(false);

        ICollapsibleFieldsetListener listener = new ICollapsibleFieldsetListener() {

            @Override
            public void onExpand(AjaxRequestTarget target) {
                if (!content.isVisible()) {
                    content.setVisible(true);
                    target.add(content);
                }
            }
        };
        CollapsibleFieldset registrationsFieldset = new CollapsibleFieldset("registrationsFieldset", new ResourceModel("registrations_label"),
                listener);
        registrationsFieldset.add(content);
        registrationsFieldset.setVisible(countPersonRegistrations > 0);

        final IModel<List<PersonRegistration>> personRegistrationsModel = new AbstractReadOnlyModel<List<PersonRegistration>>() {

            private List<PersonRegistration> personRegistrations;

            @Override
            public List<PersonRegistration> getObject() {
                if (personRegistrations == null) {
                    personRegistrations = countPersonRegistrations == 0 ? new ArrayList<PersonRegistration>()
                            : personStrategy.findPersonRegistrations(person.getObjectId());
                }
                return personRegistrations;
            }
        };
        ListView<PersonRegistration> registrations = new ListView<PersonRegistration>("registrations", personRegistrationsModel) {

            @Override
            protected void populateItem(ListItem<PersonRegistration> item) {
                PersonRegistration personRegistration = item.getModelObject();
                item.add(new Label("registrationAddress",
                        addressRendererBean.displayAddress(personRegistration.getAddressEntity(), personRegistration.getAddressId(),
                        getLocale())));
                DomainObject registrationType = personRegistration.getRegistration().getRegistrationType();
                item.add(new Label("registrationType", registrationType != null
                        ? registrationTypeStrategy.displayDomainObject(registrationType, getLocale()) : null));
                Date registrationStartDate = personRegistration.getRegistration().getRegistrationDate();
                item.add(new Label("registrationStartDate", registrationStartDate != null ? PersonDateFormatter.format(registrationStartDate) : null));
                Date registrationEndDate = personRegistration.getRegistration().getDepartureDate();
                item.add(new Label("registrationEndDate", registrationEndDate != null ? PersonDateFormatter.format(registrationEndDate)
                        : getString("live_registration_end_date")));
                final DomainObject ownerRelationship = personRegistration.getRegistration().getOwnerRelationship();
                item.add(new Label("registrationOwnerRelationship", ownerRelationship != null
                        ? ownerRelationshipStrategy.displayDomainObject(ownerRelationship, getLocale()) : null));
            }
        };
        content.add(registrations);
        return registrationsFieldset;
    }
    /**
     * Military service relation UI fields.
     */
    private WebMarkupContainer militaryServiceRelationHead;
    private WebMarkupContainer militaryServiceRelationBody;

    private Component initMilitaryServiceRelation() {
        WebMarkupContainer militaryServiceRelationContainer = new WebMarkupContainer("militaryServiceRelationContainer");

        militaryServiceRelationHead = new WebMarkupContainer("militaryServiceRelationHead");
        militaryServiceRelationHead.setOutputMarkupPlaceholderTag(true);
        militaryServiceRelationContainer.add(militaryServiceRelationHead);

        EntityAttribute militaryAttruibuteType = personStrategy.getEntity().getAttribute(MILITARY_SERVICE_RELATION);
        //label
        final IModel<String> labelModel = labelModel(militaryAttruibuteType.getNames(), getLocale());
        militaryServiceRelationHead.add(new Label("label", labelModel));

        //required container
        WebMarkupContainer requiredContainer = new WebMarkupContainer("required");
        requiredContainer.setVisible(militaryAttruibuteType.isRequired());
        militaryServiceRelationHead.add(requiredContainer);

        militaryServiceRelationBody = new WebMarkupContainer("militaryServiceRelationBody");
        militaryServiceRelationBody.setOutputMarkupPlaceholderTag(true);
        militaryServiceRelationContainer.add(militaryServiceRelationBody);

        //input component
        final List<DomainObject> allMilitaryServiceRelations = militaryServiceRelationStrategy.getAll(getLocale());
        IModel<DomainObject> militaryServiceRelationModel = new Model<DomainObject>() {

            @Override
            public DomainObject getObject() {
                return person.getMilitaryServiceRelation();
            }

            @Override
            public void setObject(DomainObject object) {
                person.setMilitaryServiceRelation(object);
            }
        };
        if (person.getMilitaryServiceRelation() != null) {
            for (DomainObject msr : allMilitaryServiceRelations) {
                if (msr.getObjectId().equals(person.getMilitaryServiceRelation().getObjectId())) {
                    militaryServiceRelationModel.setObject(msr);
                    break;
                }
            }
        }
        DisableAwareDropDownChoice<DomainObject> militaryServiceRelation =
                new DisableAwareDropDownChoice<DomainObject>("input", militaryServiceRelationModel,
                allMilitaryServiceRelations, new DomainObjectDisableAwareRenderer() {

            @Override
            public Object getDisplayValue(DomainObject object) {
                return militaryServiceRelationStrategy.displayDomainObject(object, getLocale());
            }
        });
        militaryServiceRelation.setNullValid(true);
        militaryServiceRelation.setLabel(labelModel);
        militaryServiceRelation.setRequired(militaryAttruibuteType.isRequired());
        militaryServiceRelation.setEnabled(canEdit());
        militaryServiceRelationBody.add(militaryServiceRelation);

        boolean initialVisibility = personAgeType == PersonAgeType.ADULT || (personAgeType == PersonAgeType.ANY && !person.isKid());
        militaryServiceRelationHead.setVisible(initialVisibility);
        militaryServiceRelationBody.setVisible(initialVisibility);

        return militaryServiceRelationContainer;
    }
}
