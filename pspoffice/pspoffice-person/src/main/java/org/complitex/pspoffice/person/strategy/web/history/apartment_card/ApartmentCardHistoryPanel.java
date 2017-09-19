package org.complitex.pspoffice.person.strategy.web.history.apartment_card;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.util.string.Strings;
import org.complitex.common.entity.Attribute;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.entity.Entity;
import org.complitex.common.entity.EntityAttribute;
import org.complitex.common.service.IUserProfileBean;
import org.complitex.common.util.StringUtil;
import org.complitex.common.web.component.DisableAwareDropDownChoice;
import org.complitex.common.web.component.DomainObjectDisableAwareRenderer;
import org.complitex.common.web.component.ShowMode;
import org.complitex.common.web.component.css.CssAttributeBehavior;
import org.complitex.common.web.component.search.CollapsibleSearchComponent;
import org.complitex.pspoffice.ownerrelationship.strategy.OwnerRelationshipStrategy;
import org.complitex.pspoffice.ownership.strategy.OwnershipFormStrategy;
import org.complitex.pspoffice.person.strategy.ApartmentCardStrategy;
import org.complitex.pspoffice.person.strategy.PersonStrategy;
import org.complitex.pspoffice.person.strategy.RegistrationStrategy;
import org.complitex.pspoffice.person.strategy.entity.*;
import org.complitex.pspoffice.person.strategy.web.history.HistoryDateFormatter;
import org.complitex.pspoffice.person.util.PersonDateFormatter;
import org.complitex.pspoffice.registration_type.strategy.RegistrationTypeStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.ImmutableList.of;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static org.complitex.common.web.component.DomainObjectComponentUtil.labelModel;
import static org.complitex.common.web.component.DomainObjectComponentUtil.newInputComponent;
import static org.complitex.pspoffice.person.strategy.ApartmentCardStrategy.*;

/**
 *
 * @author Artem
 */
final class ApartmentCardHistoryPanel extends Panel {
    private final Logger log = LoggerFactory.getLogger(ApartmentCardHistoryPanel.class);
    private static final String EMPTY_OWNER_RELATIONSHIP = "               ";

    @EJB
    private ApartmentCardStrategy apartmentCardStrategy;

    @EJB
    private PersonStrategy personStrategy;

    @EJB
    private OwnerRelationshipStrategy ownerRelationshipStrategy;

    @EJB
    private OwnershipFormStrategy ownershipFormStrategy;

    @EJB
    private RegistrationTypeStrategy registrationTypeStrategy;

    @EJB(name = "UserProfileBean")

    private IUserProfileBean userProfileBean;
    private final Entity ENTITY = apartmentCardStrategy.getEntity();

    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(CssHeaderItem.forReference(new PackageResourceReference(ApartmentCardHistoryPanel.class,
                ApartmentCardHistoryPanel.class.getSimpleName() + ".css")));
    }

    ApartmentCardHistoryPanel(String id, long apartmentCardId, final Date endDate) {
        super(id);

        final Date startDate = apartmentCardStrategy.getPreviousModificationDate(apartmentCardId, endDate);
        final ApartmentCard card = apartmentCardStrategy.getHistoryApartmentCard(apartmentCardId, startDate);
        if (card == null) {
            throw new NullPointerException("History apartment card is null. Id: " + apartmentCardId
                    + ", startDate:" + startDate + ", endDate: " + endDate);
        }
        final ApartmentCardModification modification = apartmentCardStrategy.getDistinctions(card, startDate);

        add(new Label("label", endDate != null ? new StringResourceModel("label", null,
                Model.of(new Object[]{apartmentCardId, HistoryDateFormatter.format(startDate),
                    HistoryDateFormatter.format(endDate)}))
                : new StringResourceModel("label_current", null, Model.of(new Object[]{apartmentCardId,
                    HistoryDateFormatter.format(startDate)}))));

        final String editedByUserName = getEditedByUserName(modification);
        add(new Label("editedByUser", !Strings.isEmpty(editedByUserName) ? editedByUserName : "[N/A]"));

        //address
        WebMarkupContainer addressContainer = new WebMarkupContainer("addressContainer");
        final EntityAttribute addressEntityAttribute = ENTITY.getAttribute(ADDRESS);
        addressContainer.add(new Label("label", labelModel(addressEntityAttribute.getNames(), getLocale())));
        addressContainer.add(new WebMarkupContainer("required").setVisible(addressEntityAttribute.isMandatory()));
        final Component address = new CollapsibleSearchComponent("address",
                apartmentCardStrategy.initAddressSearchComponentState(apartmentCardStrategy.getAddressEntity(card), card.getAddressId()),
                of("city", "street", "building", "apartment", "room"), null, ShowMode.ALL, false);
        address.add(new CssAttributeBehavior(modification.getModificationType(ADDRESS).getCssClass()));
        addressContainer.add(address);
        add(addressContainer);

        //owner
        WebMarkupContainer ownerContainer = new WebMarkupContainer("ownerContainer");
        final EntityAttribute ownerEntityAttribute = ENTITY.getAttribute(OWNER);
        IModel<String> ownerLabelModel = labelModel(ownerEntityAttribute.getNames(), getLocale());
        ownerContainer.add(new Label("label", ownerLabelModel));
        ownerContainer.add(new WebMarkupContainer("required").setVisible(ownerEntityAttribute.isMandatory()));
        final Component owner = new Label("owner", personStrategy.displayDomainObject(card.getOwner(), getLocale()));
        owner.add(new CssAttributeBehavior(modification.getModificationType(OWNER).getCssClass()));
        ownerContainer.add(owner);
        add(ownerContainer);

        //form of ownership
        final EntityAttribute formOfOwnershipEntityAttribute = apartmentCardStrategy.getEntity().getAttribute(FORM_OF_OWNERSHIP);
        WebMarkupContainer formOfOwnershipContainer = new WebMarkupContainer("formOfOwnershipContainer");
        IModel<String> labelModel = labelModel(formOfOwnershipEntityAttribute.getNames(), getLocale());
        formOfOwnershipContainer.add(new Label("label", labelModel));
        formOfOwnershipContainer.add(new WebMarkupContainer("required").setVisible(formOfOwnershipEntityAttribute.isMandatory()));
        final List<DomainObject> allOwnershipForms = ownershipFormStrategy.getAll();
        IModel<DomainObject> ownershipFormModel = new Model<DomainObject>();
        if (card.getOwnershipForm() != null) {
            for (DomainObject ownershipForm : allOwnershipForms) {
                if (ownershipForm.getObjectId().equals(card.getOwnershipForm().getObjectId())) {
                    ownershipFormModel.setObject(ownershipForm);
                    break;
                }
            }
        }
        final Component formOfOwnership = new DisableAwareDropDownChoice<>("formOfOwnership",
                ownershipFormModel, allOwnershipForms, new DomainObjectDisableAwareRenderer() {

            @Override
            public Object getDisplayValue(DomainObject object) {
                return ownershipFormStrategy.displayDomainObject(object, getLocale());
            }
        });
        formOfOwnership.setEnabled(false);
        formOfOwnership.add(new CssAttributeBehavior(modification.getModificationType(FORM_OF_OWNERSHIP).getCssClass()));
        formOfOwnershipContainer.add(formOfOwnership);
        add(formOfOwnershipContainer);

        //registrations
        ListView<Registration> registrations = new ListView<Registration>("registrations", card.getRegistrations()) {

            @Override
            protected void populateItem(ListItem<Registration> item) {
                final Registration registration = item.getModelObject();
                final RegistrationModification registrationModification =
                        modification.getRegistrationModification(registration.getObjectId());

                Set<String> rowCss = newHashSet();
                rowCss.add(registrationModification.getModificationType().getCssClass());

                item.add(new Label("personName", personStrategy.displayDomainObject(registration.getPerson(), getLocale())));

                Date birthDate = registration.getPerson().getBirthDate();
                item.add(new Label("personBirthDate", birthDate != null ? PersonDateFormatter.format(birthDate) : null));

                Component registrationType = new Label("registrationType",
                        StringUtil.valueOf(registrationTypeStrategy.displayDomainObject(
                        registration.getRegistrationType(), getLocale())));
                addRegistrationAttributeCss(registrationType, RegistrationStrategy.REGISTRATION_TYPE, registrationModification);
                item.add(registrationType);

                Date registrationStartDate = registration.getRegistrationDate();
                Component registrationStartDateLabel = new Label("registrationStartDate", registrationStartDate != null
                        ? PersonDateFormatter.format(registrationStartDate) : null);
                addRegistrationAttributeCss(registrationStartDateLabel, RegistrationStrategy.REGISTRATION_DATE, registrationModification);
                item.add(registrationStartDateLabel);

                Date registrationEndDate = registration.getDepartureDate();
                item.add(new Label("registrationEndDate", registrationEndDate != null
                        ? PersonDateFormatter.format(registrationEndDate) : null));

                final DomainObject ownerRelationship = registration.getOwnerRelationship();
                Component registrationOwnerRelationship = new Label("registrationOwnerRelationship", ownerRelationship != null
                        ? ownerRelationshipStrategy.displayDomainObject(ownerRelationship, getLocale()) : EMPTY_OWNER_RELATIONSHIP);
                addRegistrationAttributeCss(registrationOwnerRelationship, RegistrationStrategy.OWNER_RELATIONSHIP,
                        registrationModification);
                if (ownerRelationship == null) {
                    registrationOwnerRelationship.add(new CssAttributeBehavior("empty_owner_relationship"));
                }
                item.add(registrationOwnerRelationship);

                if (registration.isFinished()) {
                    rowCss.add("finished_registration");
                }
                item.add(new CssAttributeBehavior(rowCss));
            }

            private void addRegistrationAttributeCss(Component component, long entityAttributeId,
                    RegistrationModification registrationModification) {
                ModificationType attributeModificationType = registrationModification.getAttributeModificationType(entityAttributeId);
                if (attributeModificationType != null) {
                    component.add(new CssAttributeBehavior(attributeModificationType.getCssClass()));
                }
            }
        };
        add(registrations);

        //user attributes and housing rights:
        List<Long> restAttributeTypeIds = newArrayList(transform(filter(ENTITY.getAttributes(),
                new Predicate<EntityAttribute>() {

                    @Override
                    public boolean apply(EntityAttribute attributeType) {
                        return !attributeType.isSystem() || attributeType.getId().equals(HOUSING_RIGHTS);
                    }
                }),
                new Function<EntityAttribute, Long>() {

                    @Override
                    public Long apply(EntityAttribute attributeType) {
                        return attributeType.getId();
                    }
                }));

        List<Attribute> restAttributes = newArrayList();
        for (Long entityAttributeId : restAttributeTypeIds) {
            Attribute userAttribute = card.getAttribute(entityAttributeId);
            if (userAttribute != null) {
                restAttributes.add(userAttribute);
            }
        }
        ListView<Attribute> restAttributesView = new ListView<Attribute>("restAttributesView", restAttributes) {

            @Override
            protected void populateItem(ListItem<Attribute> item) {
                long restAttributeTypeId = item.getModelObject().getEntityAttributeId();
                initAttributeInput(card, modification, item, restAttributeTypeId);
            }
        };
        add(restAttributesView);

        //explanation
        WebMarkupContainer explanationContainer = new WebMarkupContainer("explanationContainer");
        explanationContainer.add(new Label("label", new ResourceModel("explanation")));
        WebMarkupContainer wrapper = new WebMarkupContainer("wrapper");
        wrapper.add(new CssAttributeBehavior(ModificationType.ADD.getCssClass()));
        explanationContainer.add(wrapper);
        String explanationText = getExplanation(modification);
        TextArea<String> explanation = new TextArea<String>("explanation", new Model<String>(explanationText));
        explanation.setEnabled(false);
        wrapper.add(explanation);
        explanationContainer.setVisible(!Strings.isEmpty(explanationText));
        add(explanationContainer);
    }

    private void initAttributeInput(ApartmentCard card, ApartmentCardModification modification, MarkupContainer parent,
            long entityAttributeId) {
        final EntityAttribute entityAttribute = ENTITY.getAttribute(entityAttributeId);

        //label
        parent.add(new Label("label", labelModel(entityAttribute.getNames(), getLocale())));

        //required container
        WebMarkupContainer requiredContainer = new WebMarkupContainer("required");
        requiredContainer.setVisible(entityAttribute.isMandatory());
        parent.add(requiredContainer);

        //input component
        Attribute attribute = card.getAttribute(entityAttributeId);
        Component inputComponent = newInputComponent(apartmentCardStrategy.getEntityName(), null, card, attribute,
                getLocale(), true);
        inputComponent.add(new CssAttributeBehavior(modification.getModificationType(entityAttributeId).getCssClass()));
        parent.add(inputComponent);
    }

    private String getEditedByUserName(ApartmentCardModification modification) {
        Long editedByUserId = modification.getEditedByUserId();
        if (editedByUserId == null) {
            for (RegistrationModification rm : modification.getRegistrationModifications()) {
                if (editedByUserId == null && rm.getEditedByUserId() != null) {
                    editedByUserId = rm.getEditedByUserId();
                    break;
                }
            }
        }

        String editedByUserName = null;
        try {
            editedByUserName = userProfileBean.getFullName(editedByUserId, getLocale());
        } catch (Exception e) {
            log.error("", e);
        }
        return editedByUserName;
    }

    private String getExplanation(ApartmentCardModification modification) {
        if (modification.getExplanation() != null) {
            return modification.getExplanation();
        }
        for (RegistrationModification rm : modification.getRegistrationModifications()) {
            if (rm.getExplanation() != null) {
                return rm.getExplanation();
            }
        }
        return null;
    }
}
