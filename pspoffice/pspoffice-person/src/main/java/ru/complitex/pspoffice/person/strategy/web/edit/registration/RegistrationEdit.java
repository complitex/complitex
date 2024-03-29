package ru.complitex.pspoffice.person.strategy.web.edit.registration;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.*;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.util.string.Strings;
import ru.complitex.address.service.AddressRendererBean;
import ru.complitex.common.converter.DateConverter;
import ru.complitex.common.entity.*;
import ru.complitex.common.service.LogBean;
import ru.complitex.common.strategy.StringValueBean;
import ru.complitex.common.util.CloneUtil;
import ru.complitex.common.util.DateUtil;
import ru.complitex.common.util.Numbers;
import ru.complitex.common.util.StringValueUtil;
import ru.complitex.common.web.component.DisableAwareDropDownChoice;
import ru.complitex.common.web.component.DomainObjectDisableAwareRenderer;
import ru.complitex.common.web.component.dateinput.MaskedDateInput;
import ru.complitex.common.web.component.domain.DomainObjectAccessUtil;
import ru.complitex.common.web.component.fieldset.CollapsibleFieldset;
import ru.complitex.common.web.component.scroll.ScrollToElementUtil;
import ru.complitex.common.web.component.type.MaskedDateInputPanel;
import ru.complitex.pspoffice.ownerrelationship.strategy.OwnerRelationshipStrategy;
import ru.complitex.pspoffice.person.Module;
import ru.complitex.pspoffice.person.report.web.F3ReferencePage;
import ru.complitex.pspoffice.person.report.web.RegistrationCardPage;
import ru.complitex.pspoffice.person.report.web.RegistrationStopCouponPage;
import ru.complitex.pspoffice.person.strategy.ApartmentCardStrategy;
import ru.complitex.pspoffice.person.strategy.PersonStrategy;
import ru.complitex.pspoffice.person.strategy.RegistrationStrategy;
import ru.complitex.pspoffice.person.strategy.entity.ApartmentCard;
import ru.complitex.pspoffice.person.strategy.entity.PersonAgeType;
import ru.complitex.pspoffice.person.strategy.entity.Registration;
import ru.complitex.pspoffice.person.strategy.web.component.ExplanationDialog;
import ru.complitex.pspoffice.person.strategy.web.component.PersonPicker;
import ru.complitex.pspoffice.person.strategy.web.edit.apartment_card.ApartmentCardEdit;
import ru.complitex.pspoffice.person.strategy.web.edit.registration.toolbar.F3ReferenceButton;
import ru.complitex.pspoffice.person.strategy.web.edit.registration.toolbar.RegistrationCardButton;
import ru.complitex.pspoffice.person.strategy.web.edit.registration.toolbar.RegistrationStopCouponButton;
import ru.complitex.pspoffice.person.strategy.web.history.registration.RegistrationHistoryPage;
import ru.complitex.pspoffice.registration_type.strategy.RegistrationTypeStrategy;
import ru.complitex.resources.WebCommonResourceInitializer;
import ru.complitex.template.web.component.toolbar.ToolbarButton;
import ru.complitex.template.web.security.SecurityRole;
import ru.complitex.template.web.template.FormTemplatePage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.ImmutableList.of;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static ru.complitex.common.web.component.DomainObjectComponentUtil.labelModel;
import static ru.complitex.common.web.component.DomainObjectComponentUtil.newInputComponent;
import static ru.complitex.pspoffice.person.strategy.RegistrationStrategy.*;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRole.AUTHORIZED)
public class RegistrationEdit extends FormTemplatePage {

    private final Logger log = LoggerFactory.getLogger(RegistrationEdit.class);
    @EJB
    private RegistrationStrategy registrationStrategy;
    @EJB
    private AddressRendererBean addressRendererBean;
    @EJB
    private OwnerRelationshipStrategy ownerRelationshipStrategy;
    @EJB
    private RegistrationTypeStrategy registrationTypeStrategy;
    @EJB
    private ApartmentCardStrategy apartmentCardStrategy;
    @EJB
    private StringValueBean stringBean;
    @EJB
    private LogBean logBean;
    @EJB
    private PersonStrategy personStrategy;
    private final Entity ENTITY = registrationStrategy.getEntity();
    private final ApartmentCard apartmentCard;
    private final Registration newRegistration;
    private final Registration oldRegistration;
    private final String addressEntity;
    private final long addressId;

    public RegistrationEdit(ApartmentCard apartmentCard, String addressEntity, long addressId, Registration registration) {
        this.apartmentCard = apartmentCard;
        this.addressEntity = addressEntity;
        this.addressId = addressId;

        if (registration.getObjectId() == null) {
            newRegistration = registration;
            oldRegistration = null;
        } else {
            newRegistration = registration;
            oldRegistration = CloneUtil.cloneObject(newRegistration);
        }
        init();
    }

    private boolean isNew() {
        return oldRegistration == null;
    }

    private boolean canEdit() {
        return DomainObjectAccessUtil.canEdit(null, registrationStrategy.getEntityName(), newRegistration);
    }

    private boolean isInactive() {
        return newRegistration.getStatus() != Status.ACTIVE;
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(JavaScriptHeaderItem.forReference(WebCommonResourceInitializer.SCROLL_JS));
        response.render(CssHeaderItem.forReference(new PackageResourceReference(
                RegistrationEdit.class, RegistrationEdit.class.getSimpleName() + ".css")));
    }

    private void init() {
        IModel<String> addressModel = new LoadableDetachableModel<String>() {

            @Override
            protected String load() {
                return addressRendererBean.displayAddress(addressEntity, addressId, getLocale());
            }
        };

        IModel<String> labelModel = new StringResourceModel("label", null, Model.of(new Object[]{addressModel.getObject()}));
        Label title = new Label("title", labelModel);
        add(title);
        final Label label = new Label("label", labelModel);
        label.setOutputMarkupId(true);
        add(label);

        final FeedbackPanel messages = new FeedbackPanel("messages");
        messages.setOutputMarkupId(true);
        add(messages);

        Form<Void> form = new Form<Void>("form");

        //address
        form.add(new Label("address", addressModel));

        //person
        WebMarkupContainer personContainer = new WebMarkupContainer("personContainer");
        final EntityAttribute personEntityAttribute = ENTITY.getAttribute(PERSON);
        personContainer.add(new WebMarkupContainer("required").setVisible(personEntityAttribute.isRequired()));

        PersonPicker person = new PersonPicker("person", PersonAgeType.ANY, new PropertyModel<>(newRegistration, "person"),
                true, labelModel(personEntityAttribute.getNames(), getLocale()), isNew() && canEdit());
        personContainer.add(person);
        form.add(personContainer);

        //registration date
        {
            initSystemAttributeInput(form, "registrationDate", REGISTRATION_DATE, true);
            if (!isInactive()) {
                if (newRegistration.getRegistrationDate() == null) {
                    StringValueUtil.getSystemStringValue(newRegistration.getAttribute(REGISTRATION_DATE).getStringValues()).
                            setValue(new DateConverter().toString(DateUtil.getCurrentDate()));
                }
                if (!isNew()) {
                    MaskedDateInput registrationDate = (MaskedDateInput) form.get("registrationDateContainer:input:"
                            + MaskedDateInputPanel.DATE_INPUT_ID);
                    registrationDate.setMinDate(newRegistration.getPerson().getBirthDate());
                }
            }
        }

        form.add(initRegistrationType());

        CollapsibleFieldset arrivalAddressFieldset = new CollapsibleFieldset("arrivalAddressFieldset",
                new ResourceModel("arrival_address"), !isNew());
        form.add(arrivalAddressFieldset);
        initSystemAttributeInput(arrivalAddressFieldset, "arrivalCountry", ARRIVAL_COUNTRY, true);
        initSystemAttributeInput(arrivalAddressFieldset, "arrivalRegion", ARRIVAL_REGION, true);
        initSystemAttributeInput(arrivalAddressFieldset, "arrivalStreet", ARRIVAL_STREET, true);
        initSystemAttributeInput(arrivalAddressFieldset, "arrivalDistrict", ARRIVAL_DISTRICT, true);
        initSystemAttributeInput(arrivalAddressFieldset, "arrivalBuildingNumber", ARRIVAL_BUILDING_NUMBER, true);
        initSystemAttributeInput(arrivalAddressFieldset, "arrivalCity", ARRIVAL_CITY, true);
        initSystemAttributeInput(arrivalAddressFieldset, "arrivalBuildingCorp", ARRIVAL_BUILDING_CORP, true);
        initSystemAttributeInput(arrivalAddressFieldset, "arrivalApartment", ARRIVAL_APARTMENT, true);
        initSystemAttributeInput(arrivalAddressFieldset, "arrivalDate", ARRIVAL_DATE, true);

        CollapsibleFieldset departureAddressFieldset = new CollapsibleFieldset("departureAddressFieldset",
                new ResourceModel("departure_address"), false);
        departureAddressFieldset.setVisible(isInactive());
        form.add(departureAddressFieldset);
        initSystemAttributeInput(departureAddressFieldset, "departureCountry", DEPARTURE_COUNTRY, true);
        initSystemAttributeInput(departureAddressFieldset, "departureRegion", DEPARTURE_REGION, true);
        initSystemAttributeInput(departureAddressFieldset, "departureDistrict", DEPARTURE_DISTRICT, true);
        initSystemAttributeInput(departureAddressFieldset, "departureCity", DEPARTURE_CITY, true);
        initSystemAttributeInput(departureAddressFieldset, "departureStreet", DEPARTURE_STREET, true);
        initSystemAttributeInput(departureAddressFieldset, "departureBuildingNumber", DEPARTURE_BUILDING_NUMBER, true);
        initSystemAttributeInput(departureAddressFieldset, "departureBuildingCorp", DEPARTURE_BUILDING_CORP, true);
        initSystemAttributeInput(departureAddressFieldset, "departureApartment", DEPARTURE_APARTMENT, true);
        initSystemAttributeInput(departureAddressFieldset, "departureDate", DEPARTURE_DATE, true);
        initSystemAttributeInput(departureAddressFieldset, "departureReason", DEPARTURE_REASON, true);

        form.add(initOwnerRelationship());

        //user attributes:
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
            Attribute userAttribute = newRegistration.getAttribute(entityAttributeId);
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
        form.add(userAttributesView);

        //history
        form.add(new Link<Void>("history") {

            @Override
            public void onClick() {
                setResponsePage(new RegistrationHistoryPage(apartmentCard, newRegistration));
            }
        }.setVisible(!isNew()));

        //explanation
        final ExplanationDialog registrationExplanationDialog = new ExplanationDialog("registrationExplanationDialog");
        add(registrationExplanationDialog);

        IndicatingAjaxButton submit = new IndicatingAjaxButton("submit") {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                try {
                    if (RegistrationEdit.this.validate()) {
                        String needExplanationLabel = needExplanationLabel();
                        boolean isNeedExplanation = !Strings.isEmpty(needExplanationLabel);
                        if (!isNeedExplanation) {
                            persist(null);
                        } else {
                            registrationExplanationDialog.open(target, needExplanationLabel, new ExplanationDialog.ISubmitAction() {

                                @Override
                                public void onSubmit(AjaxRequestTarget target, String explanation) {
                                    try {
                                        persist(explanation);
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

            private void persist(String explanation) {
                save(explanation);
                back();
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
        submit.setVisible(canEdit());
        form.add(submit);
        Link<Void> back = new Link<Void>("back") {

            @Override
            public void onClick() {
                back();
            }
        };
        form.add(back);
        add(form);
    }

    private Component initOwnerRelationship() {
        final EntityAttribute ownerRelationshipEntityAttribute = registrationStrategy.getEntity().getAttribute(OWNER_RELATIONSHIP);

        WebMarkupContainer ownerRelationshipContainer = new WebMarkupContainer("ownerRelationshipContainer");

        //label
        IModel<String> labelModel = labelModel(ownerRelationshipEntityAttribute.getNames(), getLocale());
        ownerRelationshipContainer.add(new Label("label", labelModel));

        //required
        ownerRelationshipContainer.add(new WebMarkupContainer("required").setVisible(ownerRelationshipEntityAttribute.isRequired()));

        //owner relationship
        final List<DomainObject> allOwnerRelationships = ownerRelationshipStrategy.getAll(getLocale());
        IModel<DomainObject> ownerRelationshipModel = new Model<DomainObject>() {

            @Override
            public void setObject(DomainObject object) {
                newRegistration.setOwnerRelationship(object);
            }

            @Override
            public DomainObject getObject() {
                return newRegistration.getOwnerRelationship();
            }
        };
        if (newRegistration.getOwnerRelationship() != null) {
            for (DomainObject ownerRelationship : allOwnerRelationships) {
                if (ownerRelationship.getObjectId().equals(newRegistration.getOwnerRelationship().getObjectId())) {
                    ownerRelationshipModel.setObject(ownerRelationship);
                    break;
                }
            }
        }

        DropDownChoice<DomainObject> ownerRelationship = new DropDownChoice<>("input", ownerRelationshipModel,
                allOwnerRelationships, new DomainObjectDisableAwareRenderer() {

            @Override
            public Object getDisplayValue(DomainObject object) {
                return ownerRelationshipStrategy.displayDomainObject(object, getLocale());
            }
        });

        ownerRelationship.setEnabled(canEdit());
        ownerRelationship.setNullValid(true).
                setRequired(ownerRelationshipEntityAttribute.isRequired()).
                setLabel(labelModel);
        ownerRelationshipContainer.add(ownerRelationship);

        return ownerRelationshipContainer;
    }

    private Component initRegistrationType() {
        final EntityAttribute registrationTypeEntityAttribute = registrationStrategy.getEntity().getAttribute(REGISTRATION_TYPE);

        WebMarkupContainer registrationTypeContainer = new WebMarkupContainer("registrationTypeContainer");

        //label
        IModel<String> labelModel = labelModel(registrationTypeEntityAttribute.getNames(), getLocale());
        registrationTypeContainer.add(new Label("label", labelModel));

        //required
        registrationTypeContainer.add(new WebMarkupContainer("required").setVisible(registrationTypeEntityAttribute.isRequired()));

        //registration type
        final List<DomainObject> allRegistrationTypes = registrationTypeStrategy.getAll();
        IModel<DomainObject> registrationTypeModel = new Model<DomainObject>() {

            @Override
            public void setObject(DomainObject object) {
                newRegistration.setRegistrationType(object);
            }

            @Override
            public DomainObject getObject() {
                return newRegistration.getRegistrationType();
            }
        };
        if (newRegistration.getRegistrationType() != null) {
            for (DomainObject registrationType : allRegistrationTypes) {
                if (registrationType.getObjectId().equals(newRegistration.getRegistrationType().getObjectId())) {
                    registrationTypeModel.setObject(registrationType);
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
        registrationType.setRequired(registrationTypeEntityAttribute.isRequired());
        registrationType.setLabel(labelModel);
        registrationType.setEnabled(canEdit());
        registrationTypeContainer.add(registrationType);
        return registrationTypeContainer;
    }

    private void initSystemAttributeInput(MarkupContainer parent, String id, long entityAttributeId, boolean showIfMissing) {
        WebMarkupContainer container = new WebMarkupContainer(id + "Container");
        parent.add(container);
        initAttributeInput(container, entityAttributeId, showIfMissing);
    }

    private void initAttributeInput(MarkupContainer parent, long entityAttributeId, boolean showIfMissing) {
        final EntityAttribute entityAttribute = registrationStrategy.getEntity().getAttribute(entityAttributeId);

        //label
        parent.add(new Label("label", labelModel(entityAttribute.getNames(), getLocale())));

        //required container
        WebMarkupContainer requiredContainer = new WebMarkupContainer("required");
        requiredContainer.setVisible(entityAttribute.isRequired());
        parent.add(requiredContainer);

        //input component
        Attribute attribute = newRegistration.getAttribute(entityAttributeId);
        if (attribute == null) {
            attribute = new Attribute();
            attribute.setStringValues(StringValueUtil.newStringValues());
            attribute.setEntityAttributeId(entityAttributeId);
            parent.setVisible(showIfMissing);
        }
        parent.add(newInputComponent(registrationStrategy.getEntityName(), null, newRegistration, attribute,
                getLocale(), isInactive()));
    }

    public void beforePersist() {
        // person
        newRegistration.getAttribute(PERSON).setValueId(newRegistration.getPerson().getObjectId());

        // owner relationship
        Attribute ownerRelationshipAttribute = newRegistration.getAttribute(OWNER_RELATIONSHIP);
        DomainObject ownerRelationship = newRegistration.getOwnerRelationship();
        Long ownerRelationshipId = ownerRelationship != null ? ownerRelationship.getObjectId() : null;
        ownerRelationshipAttribute.setValueId(ownerRelationshipId);

        // registration type
        Attribute registrationTypeAttribute = newRegistration.getAttribute(REGISTRATION_TYPE);
        DomainObject registrationType = newRegistration.getRegistrationType();
        Long registrationTypeId = registrationType != null ? registrationType.getObjectId() : null;
        registrationTypeAttribute.setValueId(registrationTypeId);
    }

    private boolean validate() {
        //registration date must be greater than person's birth date
        if (newRegistration.getPerson().getBirthDate().after(newRegistration.getRegistrationDate())) {
            error(getString("registration_date_error"));
        }

        //permanent registration type
        Long oldRegistrationTypeId = oldRegistration == null ? null : oldRegistration.getRegistrationType().getObjectId();
        if (newRegistration.getRegistrationType().getObjectId().equals(RegistrationTypeStrategy.PERMANENT)
                && !newRegistration.getRegistrationType().getObjectId().equals(oldRegistrationTypeId)) {
            String address = personStrategy.findPermanentRegistrationAddress(newRegistration.getPerson().getObjectId(), getLocale());
            if (!Strings.isEmpty(address)) {
                error(MessageFormat.format(getString("permanent_registration_error"), address));
            }
        }

        //duplicate person registration check
        if (isNew() && !registrationStrategy.validateDuplicatePerson(apartmentCard.getObjectId(), newRegistration.getPerson().getObjectId())) {
            error(getString("person_already_registered"));
        }

        return getSession().getFeedbackMessages().isEmpty();
    }

    private void save(String explanation) {
        beforePersist();
        if (isNew()) {
            apartmentCardStrategy.addRegistration(apartmentCard, newRegistration, DateUtil.getCurrentDate());
        } else {
            if (!Strings.isEmpty(explanation)) {
                registrationStrategy.setExplanation(newRegistration, explanation);
            }
            registrationStrategy.update(oldRegistration, newRegistration, DateUtil.getCurrentDate());
        }
        logBean.log(Log.STATUS.OK, Module.NAME, RegistrationEdit.class, isNew() ? Log.EVENT.CREATE : Log.EVENT.EDIT,
                registrationStrategy, oldRegistration, newRegistration,
                !Strings.isEmpty(explanation) ? getStringFormat("explanation_log", explanation) : null);
    }

    private String needExplanationLabel() {
        if (isNew()) {
            return null;
        }

        Set<String> modifiedAttributes = newHashSet();
        //registration type
        if (!oldRegistration.getRegistrationType().getObjectId().equals(newRegistration.getRegistrationType().getObjectId())) {
            modifiedAttributes.add(labelModel(ENTITY.getAttribute(REGISTRATION_TYPE).getNames(), getLocale()).getObject());
        }
        //owner relationship
        final Long oldOwnerRelationshipId = oldRegistration.getOwnerRelationship() != null
                ? oldRegistration.getOwnerRelationship().getObjectId() : null;
        final Long newOwnerRelationshipId = newRegistration.getOwnerRelationship() != null
                ? newRegistration.getOwnerRelationship().getObjectId() : null;
        if (!Numbers.isEqual(oldOwnerRelationshipId, newOwnerRelationshipId)) {
            modifiedAttributes.add(labelModel(ENTITY.getAttribute(OWNER_RELATIONSHIP).getNames(), getLocale()).getObject());
        }

        if (modifiedAttributes.isEmpty()) {
            return null;
        }

        StringBuilder attributes = new StringBuilder();
        for (Iterator<String> i = modifiedAttributes.iterator(); i.hasNext();) {
            attributes.append("'").append(i.next()).append("'").append(i.hasNext() ? ", " : "");
        }
        return getStringFormat("need_explanation_label", attributes.toString());
    }

    private void back() {
        setResponsePage(new ApartmentCardEdit(apartmentCard.getObjectId(), null));
    }

    @Override
    protected List<ToolbarButton> getToolbarButtons(String id) {
        return of(new RegistrationStopCouponButton(id) {

            @Override
            protected void onClick() {
                setResponsePage(new RegistrationStopCouponPage(oldRegistration, addressEntity, addressId));
            }

            @Override
            protected void onBeforeRender() {
                super.onBeforeRender();
                setVisible(isInactive());
            }
        }, new RegistrationCardButton(id) {

            @Override
            protected void onClick() {
                setResponsePage(new RegistrationCardPage(oldRegistration, addressEntity, addressId));
            }

            @Override
            protected void onBeforeRender() {
                super.onBeforeRender();
                setVisible(!isNew());
            }
        }, new F3ReferenceButton(id) {

            @Override
            protected void onClick() {
                setResponsePage(new F3ReferencePage(oldRegistration, apartmentCard));
            }

            @Override
            protected void onBeforeRender() {
                super.onBeforeRender();
                setVisible(!isNew() && canEdit());
            }
        });
    }
}
