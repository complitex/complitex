package ru.complitex.pspoffice.person.strategy.web.history.person;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.*;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.util.string.Strings;
import ru.complitex.common.entity.Attribute;
import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.entity.Entity;
import ru.complitex.common.entity.EntityAttribute;
import ru.complitex.common.service.IUserProfileBean;
import ru.complitex.common.strategy.StringLocaleBean;
import ru.complitex.common.strategy.StringValueBean;
import ru.complitex.common.util.StringValueUtil;
import ru.complitex.common.web.component.DisableAwareDropDownChoice;
import ru.complitex.common.web.component.DomainObjectDisableAwareRenderer;
import ru.complitex.common.web.component.css.CssAttributeBehavior;
import ru.complitex.pspoffice.document.strategy.DocumentStrategy;
import ru.complitex.pspoffice.document_type.strategy.DocumentTypeStrategy;
import ru.complitex.pspoffice.military.strategy.MilitaryServiceRelationStrategy;
import ru.complitex.pspoffice.person.strategy.PersonStrategy;
import ru.complitex.pspoffice.person.strategy.entity.ModificationType;
import ru.complitex.pspoffice.person.strategy.entity.Person;
import ru.complitex.pspoffice.person.strategy.entity.PersonModification;
import ru.complitex.pspoffice.person.strategy.entity.PersonName;
import ru.complitex.pspoffice.person.strategy.entity.PersonName.PersonNameType;
import ru.complitex.pspoffice.person.strategy.service.PersonNameBean;
import ru.complitex.pspoffice.person.strategy.web.history.HistoryDateFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.google.common.collect.Iterables.*;
import static com.google.common.collect.Lists.newArrayList;
import static ru.complitex.common.web.component.DomainObjectComponentUtil.labelModel;
import static ru.complitex.common.web.component.DomainObjectComponentUtil.newInputComponent;
import static ru.complitex.pspoffice.person.strategy.PersonStrategy.*;

/**
 *
 * @author Artem
 */
final class PersonHistoryPanel extends Panel {
    private final Logger log = LoggerFactory.getLogger(PersonHistoryPanel.class);

    @EJB
    private PersonStrategy personStrategy;

    @EJB
    private StringLocaleBean stringLocaleBean;

    @EJB
    private PersonNameBean personNameBean;

    @EJB
    private StringValueBean stringBean;

    @EJB
    private DocumentStrategy documentStrategy;

    @EJB
    private DocumentTypeStrategy documentTypeStrategy;

    @EJB
    private MilitaryServiceRelationStrategy militaryServiceRelationStrategy;

    @EJB(name = "UserProfileBean")
    private IUserProfileBean userProfileBean;

    private final Entity ENTITY = personStrategy.getEntity();

    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(CssHeaderItem.forReference(new PackageResourceReference(PersonHistoryPanel.class,
                PersonHistoryPanel.class.getSimpleName() + ".css")));
    }

    PersonHistoryPanel(String id, long personId, final Date endDate) {
        super(id);

        final Date startDate = personStrategy.getPreviousModificationDate(personId, endDate);
        final Person person = personStrategy.getHistoryPerson(personId, startDate);
        if (person == null) {
            throw new NullPointerException("History person is null. Id: " + personId
                    + ", startDate:" + startDate + ", endDate: " + endDate);
        }
        final PersonModification modification = personStrategy.getDistinctions(person, startDate);

        add(new Label("label", endDate != null ? new StringResourceModel("label", null,
                Model.of(new Object[]{personId, HistoryDateFormatter.format(startDate),
                    HistoryDateFormatter.format(endDate)}))
                : new StringResourceModel("label_current", null, Model.of(new Object[]{personId,
                    HistoryDateFormatter.format(startDate)}))));

        final Long editedByUserId = modification.getEditedByUserId();
        String editedByUserName = null;
        try {
            editedByUserName = userProfileBean.getFullName(editedByUserId, getLocale());
        } catch (Exception e) {
            log.error("", e);
        }
        add(new Label("editedByUser", !Strings.isEmpty(editedByUserName) ? editedByUserName : "[N/A]"));

        //last name
        final EntityAttribute lastNameEntityAttribute = ENTITY.getAttribute(LAST_NAME);
        add(new Label("lastNameLabel", labelModel(lastNameEntityAttribute.getNames(), getLocale())));
        WebMarkupContainer lastNameTable = new WebMarkupContainer("lastNameTable");
        lastNameTable.add(new ListView<Attribute>("lastNames", person.getAttributes(PersonStrategy.LAST_NAME)) {

            @Override
            protected void populateItem(ListItem<Attribute> item) {
                PersonHistoryPanel.this.populateItem(item, PersonNameType.LAST_NAME, "lastName");
            }
        });
        lastNameTable.add(new CssAttributeBehavior(modification.getModificationType(LAST_NAME).getCssClass()));
        add(lastNameTable);

        //first name
        final EntityAttribute firstNameEntityAttribute = ENTITY.getAttribute(FIRST_NAME);
        add(new Label("firstNameLabel", labelModel(firstNameEntityAttribute.getNames(), getLocale())));
        WebMarkupContainer firstNameTable = new WebMarkupContainer("firstNameTable");
        firstNameTable.add(new ListView<Attribute>("firstNames", person.getAttributes(PersonStrategy.FIRST_NAME)) {

            @Override
            protected void populateItem(ListItem<Attribute> item) {
                PersonHistoryPanel.this.populateItem(item, PersonNameType.FIRST_NAME, "firstName");
            }
        });
        firstNameTable.add(new CssAttributeBehavior(modification.getModificationType(FIRST_NAME).getCssClass()));
        add(firstNameTable);

        //middle name
        final EntityAttribute middleNameEntityAttribute = ENTITY.getAttribute(MIDDLE_NAME);
        add(new Label("middleNameLabel", labelModel(middleNameEntityAttribute.getNames(), getLocale())));
        WebMarkupContainer middleNameTable = new WebMarkupContainer("middleNameTable");
        middleNameTable.add(new ListView<Attribute>("middleNames", person.getAttributes(PersonStrategy.MIDDLE_NAME)) {

            @Override
            protected void populateItem(ListItem<Attribute> item) {
                PersonHistoryPanel.this.populateItem(item, PersonNameType.MIDDLE_NAME, "middleName");
            }
        });
        middleNameTable.add(new CssAttributeBehavior(modification.getModificationType(MIDDLE_NAME).getCssClass()));
        add(middleNameTable);

        //identity code
        WebMarkupContainer identityCodeContainer = new WebMarkupContainer("identityCodeContainer");
        initAttributeInput(person, modification, identityCodeContainer, IDENTITY_CODE, false);
        add(identityCodeContainer);

        //birth date
        WebMarkupContainer birthDateContainer = new WebMarkupContainer("birthDateContainer");
        initAttributeInput(person, modification, birthDateContainer, BIRTH_DATE, true);
        add(birthDateContainer);

        //gender
        WebMarkupContainer genderContainer = new WebMarkupContainer("genderContainer");
        initAttributeInput(person, modification, genderContainer, GENDER, true);
        add(genderContainer);

        //ukraine citizenship
        WebMarkupContainer ukraineCitizenshipContainer = new WebMarkupContainer("ukraineCitizenshipContainer");
        initAttributeInput(person, modification, ukraineCitizenshipContainer, UKRAINE_CITIZENSHIP, true);
        add(ukraineCitizenshipContainer);

        //military service relation
        {
            WebMarkupContainer militaryServiceRelationContainer = new WebMarkupContainer("militaryServiceRelationContainer");
            final EntityAttribute militaryServiceRelationEntityAttribute = ENTITY.getAttribute(MILITARY_SERVICE_RELATION);

            //label
            militaryServiceRelationContainer.add(new Label("label",
                    labelModel(militaryServiceRelationEntityAttribute.getNames(), getLocale())));

            //required container
            WebMarkupContainer militaryServiceRelationRequiredContainer = new WebMarkupContainer("required");
            militaryServiceRelationRequiredContainer.setVisible(militaryServiceRelationEntityAttribute.isRequired());
            militaryServiceRelationContainer.add(militaryServiceRelationRequiredContainer);

            final List<DomainObject> allMilitaryServiceRelations = militaryServiceRelationStrategy.getAll(getLocale());
            IModel<DomainObject> militaryServiceRelationModel = new Model<DomainObject>();
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
            militaryServiceRelation.setEnabled(false);
            militaryServiceRelationContainer.add(militaryServiceRelation);

            final ModificationType militaryServiceRelationModificationType =
                    modification.getModificationType(MILITARY_SERVICE_RELATION);
            if (militaryServiceRelationModificationType != null) {
                militaryServiceRelation.add(new CssAttributeBehavior(militaryServiceRelationModificationType.getCssClass()));
            }
            militaryServiceRelationContainer.setVisible(
                    person.getMilitaryServiceRelation() != null
                    || (militaryServiceRelationModificationType != null
                    && militaryServiceRelationModificationType == ModificationType.REMOVE));

            add(militaryServiceRelationContainer);
        }

        //death date
        WebMarkupContainer deathDateContainer = new WebMarkupContainer("deathDateContainer");
        initAttributeInput(person, modification, deathDateContainer, DEATH_DATE, false);
        add(deathDateContainer);

        //user attributes
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
                initAttributeInput(person, modification, item, userAttributeTypeId, false);
            }
        };
        add(userAttributesView);

        //birth place
        //country
        WebMarkupContainer birthPlaceContainer = new WebMarkupContainer("birthPlaceContainer");
        add(birthPlaceContainer);
        WebMarkupContainer birthCountryContainer = new WebMarkupContainer("birthCountryContainer");
        initAttributeInput(person, modification, birthCountryContainer, BIRTH_COUNTRY, true);
        birthPlaceContainer.add(birthCountryContainer);
        //region
        WebMarkupContainer birthRegionContainer = new WebMarkupContainer("birthRegionContainer");
        initAttributeInput(person, modification, birthRegionContainer, BIRTH_REGION, true);
        birthPlaceContainer.add(birthRegionContainer);
        //district
        WebMarkupContainer birthDistrictContainer = new WebMarkupContainer("birthDistrictContainer");
        initAttributeInput(person, modification, birthDistrictContainer, BIRTH_DISTRICT, true);
        birthPlaceContainer.add(birthDistrictContainer);
        //city
        WebMarkupContainer birthCityContainer = new WebMarkupContainer("birthCityContainer");
        initAttributeInput(person, modification, birthCityContainer, BIRTH_CITY, true);
        birthPlaceContainer.add(birthCityContainer);

        //document
        {
            WebMarkupContainer documentContainer = new WebMarkupContainer("documentContainer");
            add(documentContainer);
            WebMarkupContainer documentTypeContainer = new WebMarkupContainer("documentTypeContainer");
            documentContainer.add(documentTypeContainer);
            final EntityAttribute documentTypeEntityAttribute = documentStrategy.getEntity().
                    getAttribute(DocumentStrategy.DOCUMENT_TYPE);
            IModel<String> documentTyleLabelModel = labelModel(documentTypeEntityAttribute.getNames(), getLocale());
            documentTypeContainer.add(new Label("label", documentTyleLabelModel));
            documentTypeContainer.add(new WebMarkupContainer("required").setVisible(documentTypeEntityAttribute.isRequired()));
            final List<DomainObject> documentTypes = documentTypeStrategy.getAll(null);
            IModel<DomainObject> documentTypeModel = new Model<DomainObject>();
            documentTypeModel.setObject(find(documentTypes, new Predicate<DomainObject>() {

                @Override
                public boolean apply(DomainObject documentType) {
                    return documentType.getObjectId().equals(person.getDocument().getDocumentTypeId());
                }
            }));
            documentTypeContainer.add(new DisableAwareDropDownChoice<DomainObject>("documentType",
                    documentTypeModel, documentTypes, new DomainObjectDisableAwareRenderer() {

                @Override
                public Object getDisplayValue(DomainObject object) {
                    return documentTypeStrategy.displayDomainObject(object, getLocale());
                }
            }).setEnabled(false));
            documentContainer.add(new DocumentHistoryPanel("documentInputPanel", person.getDocument(),
                    modification.getDocumentModification()));
            documentContainer.add(new CssAttributeBehavior(modification.getDocumentModification().getModificationType().getCssClass()));
        }

        //children
        WebMarkupContainer childrenFieldset = new WebMarkupContainer("childrenFieldset");
        ModificationType childRemovedModification = modification.isChildRemoved() ? ModificationType.REMOVE : ModificationType.NONE;
        childrenFieldset.add(new CssAttributeBehavior(childRemovedModification.getCssClass()));
        add(childrenFieldset);
        childrenFieldset.add(new Label("childrenLabel", labelModel(ENTITY.getAttribute(CHILDREN).getNames(), getLocale())));
        childrenFieldset.add(new ListView<Person>("children", person.getChildren()) {

            @Override
            protected void populateItem(ListItem<Person> item) {
                final Person child = item.getModelObject();
                item.add(new Label("label", String.valueOf(item.getIndex() + 1)));
                Label childComponent = new Label("child", personStrategy.displayDomainObject(child, getLocale()));
                childComponent.add(new CssAttributeBehavior(modification.getChildModificationType(child.getObjectId()).getCssClass()));
                item.add(childComponent);
            }
        });

        WebMarkupContainer explanationContainer = new WebMarkupContainer("explanationContainer");
        explanationContainer.add(new Label("label", new ResourceModel("explanation")));
        WebMarkupContainer wrapper = new WebMarkupContainer("wrapper");
        wrapper.add(new CssAttributeBehavior(ModificationType.ADD.getCssClass()));
        explanationContainer.add(wrapper);
        String explanationText = modification.getExplanation();
        TextArea<String> explanation = new TextArea<String>("explanation", new Model<String>(explanationText));
        explanation.setEnabled(false);
        wrapper.add(explanation);
        explanationContainer.setVisible(!Strings.isEmpty(explanationText));
        add(explanationContainer);
    }

    private void initAttributeInput(Person person, PersonModification modification, MarkupContainer parent,
            long entityAttributeId, boolean showIfMissing) {
        final EntityAttribute entityAttribute = ENTITY.getAttribute(entityAttributeId);

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
        Component inputComponent = newInputComponent(personStrategy.getEntityName(), null, person, attribute,
                getLocale(), true);
        ModificationType modificationType = modification.getModificationType(entityAttributeId);
        if (modificationType == null) {
            modificationType = ModificationType.NONE;
        }
        inputComponent.add(new CssAttributeBehavior(modificationType.getCssClass()));
        parent.add(inputComponent);
    }

    private void populateItem(ListItem<Attribute> item, PersonNameType personNameType, String personNameComponentId) {
        Attribute personNameAttribute = item.getModelObject();
        Locale locale = stringLocaleBean.getLocale(personNameAttribute.getAttributeId());
        boolean isSystemLocale = stringLocaleBean.getLocaleObject(personNameAttribute.getAttributeId()).isSystem();
        final TextField<String> personNameComponent =
                new TextField<String>(personNameComponentId, newNameModel(personNameType, personNameAttribute));
        personNameComponent.setEnabled(false);
        item.add(personNameComponent);
        Label language = new Label("language", locale.getDisplayLanguage(getLocale()));
        item.add(language);
        WebMarkupContainer requiredContainer = new WebMarkupContainer("required");
        requiredContainer.setVisible(isSystemLocale);
        item.add(requiredContainer);
    }

    private IModel<String> newNameModel(final PersonNameType personNameType, final Attribute nameAttribute) {
        return new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                PersonName personName = personNameBean.findById(personNameType, nameAttribute.getValueId());
                return personName != null ? personName.getName() : null;
            }
        };
    }
}
