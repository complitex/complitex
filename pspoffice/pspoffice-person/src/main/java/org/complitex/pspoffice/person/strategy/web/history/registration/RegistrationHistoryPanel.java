package org.complitex.pspoffice.person.strategy.web.history.registration;

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
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.util.string.Strings;
import org.complitex.address.service.AddressRendererBean;
import org.complitex.common.entity.Attribute;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.entity.Entity;
import org.complitex.common.entity.EntityAttribute;
import org.complitex.common.service.IUserProfileBean;
import org.complitex.common.strategy.StringValueBean;
import org.complitex.common.util.StringValueUtil;
import org.complitex.common.web.component.DisableAwareDropDownChoice;
import org.complitex.common.web.component.DomainObjectDisableAwareRenderer;
import org.complitex.common.web.component.css.CssAttributeBehavior;
import org.complitex.pspoffice.ownerrelationship.strategy.OwnerRelationshipStrategy;
import org.complitex.pspoffice.person.strategy.PersonStrategy;
import org.complitex.pspoffice.person.strategy.RegistrationStrategy;
import org.complitex.pspoffice.person.strategy.entity.ModificationType;
import org.complitex.pspoffice.person.strategy.entity.Registration;
import org.complitex.pspoffice.person.strategy.entity.RegistrationModification;
import org.complitex.pspoffice.person.strategy.web.history.HistoryDateFormatter;
import org.complitex.pspoffice.registration_type.strategy.RegistrationTypeStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.util.Date;
import java.util.List;

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newArrayList;
import static org.complitex.common.web.component.DomainObjectComponentUtil.labelModel;
import static org.complitex.common.web.component.DomainObjectComponentUtil.newInputComponent;
import static org.complitex.pspoffice.person.strategy.RegistrationStrategy.*;

/**
 *
 * @author Artem
 */
final class RegistrationHistoryPanel extends Panel {

    private final Logger log = LoggerFactory.getLogger(RegistrationHistoryPanel.class);
    @EJB
    private RegistrationStrategy registrationStrategy;
    @EJB
    private AddressRendererBean addressRendererBean;
    @EJB
    private PersonStrategy personStrategy;
    @EJB
    private StringValueBean stringBean;
    @EJB
    private RegistrationTypeStrategy registrationTypeStrategy;
    @EJB
    private OwnerRelationshipStrategy ownerRelationshipStrategy;
    @EJB(name = "UserProfileBean")
    private IUserProfileBean userProfileBean;
    private final Entity ENTITY = registrationStrategy.getEntity();

    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(CssHeaderItem.forReference(new PackageResourceReference(RegistrationHistoryPanel.class,
                RegistrationHistoryPanel.class.getSimpleName() + ".css")));
    }

    RegistrationHistoryPanel(String id, long registrationId, final String addressEntity, final long addressId,
            Date endDate) {
        super(id);

        final Date startDate = registrationStrategy.getPreviousModificationDate(registrationId, endDate);
        final Registration registration = registrationStrategy.getHistoryRegistration(registrationId, startDate);
        if (registration == null) {
            throw new NullPointerException("History registration is null. Id: " + registrationId
                    + ", startDate:" + startDate + ", endDate: " + endDate);
        }

        final RegistrationModification modification = registrationStrategy.getDistinctions(registration, startDate);

        add(new Label("label", endDate != null ? new StringResourceModel("label", null,
                Model.of(new Object[]{registrationId, HistoryDateFormatter.format(startDate),
                    HistoryDateFormatter.format(endDate)}))
                : new StringResourceModel("label_current", null, Model.of(new Object[]{registrationId,
                    HistoryDateFormatter.format(startDate)}))));

        final Long editedByUserId = modification.getEditedByUserId();
        String editedByUserName = null;
        try {
            editedByUserName = userProfileBean.getFullName(editedByUserId, getLocale());
        } catch (Exception e) {
            log.error("", e);
        }
        add(new Label("editedByUser", !Strings.isEmpty(editedByUserName) ? editedByUserName : "[N/A]"));

        final WebMarkupContainer content = new WebMarkupContainer("content");
        add(content);

        //address
        content.add(new Label("address", addressRendererBean.displayAddress(addressEntity, addressId, getLocale())));

        //person
        WebMarkupContainer personContainer = new WebMarkupContainer("personContainer");
        final EntityAttribute personEntityAttribute = ENTITY.getAttribute(PERSON);
        personContainer.add(new WebMarkupContainer("required").setVisible(personEntityAttribute.isMandatory()));
        Component person = new Label("person", personStrategy.displayDomainObject(registration.getPerson(), getLocale()));
        person.add(new CssAttributeBehavior(modification.getAttributeModificationType(PERSON).getCssClass()));
        personContainer.add(person);
        content.add(personContainer);

        //registration date
        initSystemAttributeInput(registration, modification, content, "registrationDate", REGISTRATION_DATE, true);

        //registration type
        {
            final EntityAttribute registrationTypeEntityAttribute = ENTITY.getAttribute(REGISTRATION_TYPE);
            WebMarkupContainer registrationTypeContainer = new WebMarkupContainer("registrationTypeContainer");
            registrationTypeContainer.add(new Label("label", labelModel(registrationTypeEntityAttribute.getNames(), getLocale())));
            registrationTypeContainer.add(new WebMarkupContainer("required").setVisible(registrationTypeEntityAttribute.isMandatory()));
            final List<DomainObject> allRegistrationTypes = registrationTypeStrategy.getAll();
            IModel<DomainObject> registrationTypeModel = new Model<DomainObject>();
            if (registration.getRegistrationType() != null) {
                for (DomainObject regType : allRegistrationTypes) {
                    if (regType.getObjectId().equals(registration.getRegistrationType().getObjectId())) {
                        registrationTypeModel.setObject(regType);
                        break;
                    }
                }
            }
            DisableAwareDropDownChoice<DomainObject> registrationType = new DisableAwareDropDownChoice<DomainObject>("input",
                    registrationTypeModel, allRegistrationTypes, new DomainObjectDisableAwareRenderer() {

                @Override
                public Object getDisplayValue(DomainObject object) {
                    return registrationTypeStrategy.displayDomainObject(object, getLocale());
                }
            });
            registrationType.setEnabled(false);
            registrationType.add(new CssAttributeBehavior(modification.getAttributeModificationType(REGISTRATION_TYPE).getCssClass()));
            registrationTypeContainer.add(registrationType);
            content.add(registrationTypeContainer);
        }

        //owner relationship
        final EntityAttribute ownerRelationshipEntityAttribute = ENTITY.getAttribute(OWNER_RELATIONSHIP);
        WebMarkupContainer ownerRelationshipContainer = new WebMarkupContainer("ownerRelationshipContainer");
        final DomainObject ownerRelationshipObject = registration.getOwnerRelationship();
        ownerRelationshipContainer.add(new Label("label", labelModel(ownerRelationshipEntityAttribute.getNames(), getLocale())));
        ownerRelationshipContainer.add(new WebMarkupContainer("required").setVisible(ownerRelationshipEntityAttribute.isMandatory()));
        final String ownerRelationshipValue = ownerRelationshipObject != null
                ? ownerRelationshipStrategy.displayDomainObject(ownerRelationshipObject, getLocale())
                : null;
        TextField<String> ownerRelationship = new TextField<String>("input", new Model<String>(ownerRelationshipValue));
        ownerRelationship.setEnabled(false);

        final ModificationType ownerRelationModificationType = modification.getAttributeModificationType(OWNER_RELATIONSHIP);
        if (ownerRelationModificationType != null) {
            ownerRelationship.add(new CssAttributeBehavior(ownerRelationModificationType.getCssClass()));
        }
        ownerRelationshipContainer.add(ownerRelationship);
        content.add(ownerRelationshipContainer);

        //arrival address
        WebMarkupContainer arrivalAddressContainer = new WebMarkupContainer("arrivalAddressContainer");
        arrivalAddressContainer.setVisible(isArrivalAddressContainerVisible(registration));
        content.add(arrivalAddressContainer);
        initSystemAttributeInput(registration, modification, arrivalAddressContainer, "arrivalCountry", ARRIVAL_COUNTRY, true);
        initSystemAttributeInput(registration, modification, arrivalAddressContainer, "arrivalRegion", ARRIVAL_REGION, true);
        initSystemAttributeInput(registration, modification, arrivalAddressContainer, "arrivalStreet", ARRIVAL_STREET, true);
        initSystemAttributeInput(registration, modification, arrivalAddressContainer, "arrivalDistrict", ARRIVAL_DISTRICT, true);
        initSystemAttributeInput(registration, modification, arrivalAddressContainer, "arrivalBuildingNumber", ARRIVAL_BUILDING_NUMBER, true);
        initSystemAttributeInput(registration, modification, arrivalAddressContainer, "arrivalCity", ARRIVAL_CITY, true);
        initSystemAttributeInput(registration, modification, arrivalAddressContainer, "arrivalBuildingCorp", ARRIVAL_BUILDING_CORP, true);
        initSystemAttributeInput(registration, modification, arrivalAddressContainer, "arrivalApartment", ARRIVAL_APARTMENT, true);
        initSystemAttributeInput(registration, modification, arrivalAddressContainer, "arrivalDate", ARRIVAL_DATE, true);

        //departure address
        WebMarkupContainer departureAddressContainer = new WebMarkupContainer("departureAddressContainer");
        departureAddressContainer.setVisible(isDepartureAddressContainerVisible(registration));
        content.add(departureAddressContainer);
        initSystemAttributeInput(registration, modification, departureAddressContainer, "departureCountry", DEPARTURE_COUNTRY, true);
        initSystemAttributeInput(registration, modification, departureAddressContainer, "departureRegion", DEPARTURE_REGION, true);
        initSystemAttributeInput(registration, modification, departureAddressContainer, "departureDistrict", DEPARTURE_DISTRICT, true);
        initSystemAttributeInput(registration, modification, departureAddressContainer, "departureCity", DEPARTURE_CITY, true);
        initSystemAttributeInput(registration, modification, departureAddressContainer, "departureStreet", DEPARTURE_STREET, true);
        initSystemAttributeInput(registration, modification, departureAddressContainer, "departureBuildingNumber", DEPARTURE_BUILDING_NUMBER, true);
        initSystemAttributeInput(registration, modification, departureAddressContainer, "departureBuildingCorp", DEPARTURE_BUILDING_CORP, true);
        initSystemAttributeInput(registration, modification, departureAddressContainer, "departureApartment", DEPARTURE_APARTMENT, true);
        initSystemAttributeInput(registration, modification, departureAddressContainer, "departureDate", DEPARTURE_DATE, true);
        initSystemAttributeInput(registration, modification, departureAddressContainer, "departureReason", DEPARTURE_REASON, true);

        //user attributes
        List<Long> userAttributeTypeIds = newArrayList(transform(filter(ENTITY.getAttributes(),
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
            Attribute userAttribute = registration.getAttribute(entityAttributeId);
            if (userAttribute != null) {
                userAttributes.add(userAttribute);
            }
        }

        ListView<Attribute> userAttributesView = new ListView<Attribute>("userAttributesView", userAttributes) {

            @Override
            protected void populateItem(ListItem<Attribute> item) {
                long userAttributeTypeId = item.getModelObject().getEntityAttributeId();
                initAttributeInput(registration, modification, item, userAttributeTypeId, false);
            }
        };
        content.add(userAttributesView);

        //explanation
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
        content.add(explanationContainer);
    }

    private boolean isArrivalAddressContainerVisible(Registration registration) {
        return registration.getArrivalCountry() != null || registration.getArrivalRegion() != null
                || registration.getArrivalDistrict() != null || registration.getArrivalCity() != null
                || registration.getArrivalStreet() != null || registration.getArrivalBuildingNumber() != null
                || registration.getArrivalBuildingCorp() != null || registration.getArrivalApartment() != null
                || registration.getArrivalDate() != null;
    }

    private boolean isDepartureAddressContainerVisible(Registration registration) {
        return registration.getDepartureCountry() != null || registration.getDepartureRegion() != null
                || registration.getDepartureDistrict() != null || registration.getDepartureCity() != null
                || registration.getDepartureStreet() != null || registration.getDepartureBuildingNumber() != null
                || registration.getDepartureBuildingCorp() != null || registration.getDepartureApartment() != null
                || registration.getDepartureDate() != null || registration.getDepartureReason() != null;
    }

    private void initSystemAttributeInput(Registration registration, RegistrationModification modification,
            MarkupContainer parent, String id, long entityAttributeId, boolean showIfMissing) {
        WebMarkupContainer container = new WebMarkupContainer(id + "Container");
        parent.add(container);
        initAttributeInput(registration, modification, container, entityAttributeId, showIfMissing);
    }

    private void initAttributeInput(Registration registration, RegistrationModification modification,
            MarkupContainer parent, long entityAttributeId, boolean showIfMissing) {
        final EntityAttribute entityAttribute = ENTITY.getAttribute(entityAttributeId);

        //label
        parent.add(new Label("label", labelModel(entityAttribute.getNames(), getLocale())));

        //required container
        WebMarkupContainer requiredContainer = new WebMarkupContainer("required");
        requiredContainer.setVisible(entityAttribute.isMandatory());
        parent.add(requiredContainer);

        //input component
        Attribute attribute = registration.getAttribute(entityAttributeId);
        if (attribute == null) {
            attribute = new Attribute();
            attribute.setStringValues(StringValueUtil.newStringValues());
            attribute.setEntityAttributeId(entityAttributeId);
            parent.setVisible(showIfMissing);
        }
        Component inputComponent = newInputComponent(registrationStrategy.getEntityName(), null, registration,
                attribute, getLocale(), true);
        ModificationType modificationType = modification.getAttributeModificationType(entityAttributeId);
        if (modificationType == null) {
            modificationType = ModificationType.NONE;
        }
        inputComponent.add(new CssAttributeBehavior(modificationType.getCssClass()));
        parent.add(inputComponent);
    }
}
