package org.complitex.pspoffice.person.strategy;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.Strings;
import org.complitex.address.service.AddressRendererBean;
import org.complitex.dictionary.converter.BooleanConverter;
import org.complitex.dictionary.converter.DateConverter;
import org.complitex.dictionary.entity.Attribute;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.entity.StatusType;
import org.complitex.dictionary.entity.StringCulture;
import org.complitex.dictionary.entity.description.EntityAttributeType;
import org.complitex.dictionary.entity.description.EntityAttributeValueType;
import org.complitex.dictionary.entity.example.DomainObjectExample;
import org.complitex.dictionary.mybatis.Transactional;
import org.complitex.dictionary.service.LocaleBean;
import org.complitex.dictionary.service.SessionBean;
import org.complitex.dictionary.service.StringCultureBean;
import org.complitex.dictionary.util.CloneUtil;
import org.complitex.dictionary.util.DateUtil;
import org.complitex.dictionary.util.Numbers;
import org.complitex.dictionary.util.ResourceUtil;
import org.complitex.pspoffice.document.strategy.DocumentStrategy;
import org.complitex.pspoffice.document.strategy.entity.Document;
import org.complitex.pspoffice.document_type.strategy.DocumentTypeStrategy;
import org.complitex.pspoffice.military.strategy.MilitaryServiceRelationStrategy;
import org.complitex.pspoffice.person.strategy.entity.*;
import org.complitex.pspoffice.person.strategy.entity.PersonName.PersonNameType;
import org.complitex.pspoffice.person.strategy.service.PersonNameBean;
import org.complitex.pspoffice.person.strategy.web.edit.person.PersonEdit;
import org.complitex.pspoffice.person.strategy.web.list.person.PersonList;
import org.complitex.pspoffice.registration_type.strategy.RegistrationTypeStrategy;
import org.complitex.template.strategy.TemplateStrategy;
import org.complitex.template.web.security.SecurityRole;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.io.Serializable;
import java.util.*;

import static com.google.common.collect.ImmutableMap.builder;
import static com.google.common.collect.ImmutableMap.of;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Lists.newArrayList;

/**
 *
 * @author Artem
 */
@Stateless
public class PersonStrategy extends TemplateStrategy {

    private static final String PERSON_MAPPING = PersonStrategy.class.getPackage().getName() + ".Person";
    public static final String RESOURCE_BUNDLE = PersonStrategy.class.getName();
    /**
     * Person kid-adult age threshold
     */
    public static final int AGE_THRESHOLD = 16;
    /**
     * Attribute type ids
     */
    public static final long LAST_NAME = 2000;
    public static final long FIRST_NAME = 2001;
    public static final long MIDDLE_NAME = 2002;
    public static final long IDENTITY_CODE = 2003;
    public static final long BIRTH_DATE = 2004;
    public static final long BIRTH_COUNTRY = 2005;
    public static final long BIRTH_REGION = 2006;
    public static final long BIRTH_DISTRICT = 2007;
    public static final long BIRTH_CITY = 2008;
    public static final long DOCUMENT = 2009;
    public static final long DEATH_DATE = 2013;
    public static final long MILITARY_SERVICE_RELATION = 2014;
    public static final long CHILDREN = 2015;
    public static final long GENDER = 2016;
    public static final long UKRAINE_CITIZENSHIP = 2020;
    public static final long EXPLANATION = 2021;
    public static final long EDITED_BY_USER_ID = 2022;
    public static final long OLD_SYSTEM_PERSON_ID = 2023;
    public static final Set<Long> NAME_ATTRIBUTE_IDS = ImmutableSet.of(LAST_NAME, FIRST_NAME, MIDDLE_NAME);

    /**
     * Order by related constants
     */
    public static enum OrderBy {

        LAST_NAME(PersonStrategy.LAST_NAME), FIRST_NAME(PersonStrategy.FIRST_NAME), MIDDLE_NAME(PersonStrategy.MIDDLE_NAME);
        private Long orderByAttributeId;

        private OrderBy(Long orderByAttributeId) {
            this.orderByAttributeId = orderByAttributeId;
        }

        public Long getOrderByAttributeId() {
            return orderByAttributeId;
        }
    }
    /**
     * Filter constants
     */
    public static final String LAST_NAME_FILTER = "last_name";
    public static final String FIRST_NAME_FILTER = "first_name";
    public static final String MIDDLE_NAME_FILTER = "middle_name";
    public static final long DEFAULT_ORDER_BY_ID = -1;
    @EJB
    private StringCultureBean stringBean;
    @EJB
    private PersonNameBean personNameBean;
    @EJB
    private DocumentStrategy documentStrategy;
    @EJB
    private DocumentTypeStrategy documentTypeStrategy;
    @EJB
    private MilitaryServiceRelationStrategy militaryServiceRelationStrategy;
    @EJB
    private LocaleBean localeBean;
    @EJB
    private RegistrationStrategy registrationStrategy;
    @EJB
    private AddressRendererBean addressRendererBean;
    @EJB
    private SessionBean sessionBean;

    @Override
    public String getEntityTable() {
        return "person";
    }

    @Override
    public Class<? extends WebPage> getListPage() {
        return PersonList.class;
    }

    @Override
    public PageParameters getListPageParams() {
        return new PageParameters();
    }

    @Override
    public Class<? extends WebPage> getEditPage() {
        return PersonEdit.class;
    }

    @Override
    public PageParameters getEditPageParams(Long objectId, Long parentId, String parentEntity) {
        PageParameters params = new PageParameters();
        params.set(TemplateStrategy.OBJECT_ID, objectId);
        return params;
    }

    @Override
    public String[] getEditRoles() {
        return new String[]{SecurityRole.PERSON_MODULE_EDIT};
    }

    @Override
    public String[] getListRoles() {
        return new String[]{SecurityRole.PERSON_MODULE_VIEW};
    }

    @Override
    public long getDefaultSortAttributeTypeId() {
        return OrderBy.LAST_NAME.getOrderByAttributeId();
    }

    @Override
    public String displayDomainObject(DomainObject object, Locale locale) {
        Person person = (Person) object;
        Locale systemLocale = localeBean.getSystemLocale();
        return displayPerson(person.getFirstName(locale, systemLocale), person.getMiddleName(locale, systemLocale),
                person.getLastName(locale, systemLocale));
    }

    public String displayPerson(String firstName, String middleName, String lastName) {
        // return in format 'last_name fisrt_name middle_name'
        return lastName + " " + firstName + " " + middleName;
    }

    @Transactional
    @Override
    public List<Person> find(DomainObjectExample example) {
        if (example.getId() != null && example.getId() <= 0) {
            return Collections.emptyList();
        }

        example.setTable(getEntityTable());
        if (!example.isAdmin()) {
            prepareExampleForPermissionCheck(example);
        }

        List<Person> persons = sqlSession().selectList(PERSON_MAPPING + "." + FIND_OPERATION, example);
        for (Person person : persons) {
            loadAttributes(person);
            loadName(person);
            //load subject ids
            person.setSubjectIds(loadSubjects(person.getPermissionId()));
        }
        return persons;
    }

    @Transactional
    @Override
    public int count(DomainObjectExample example) {
        if (example.getId() != null && example.getId() <= 0) {
            return 0;
        }

        example.setTable(getEntityTable());
        prepareExampleForPermissionCheck(example);
        return (Integer) sqlSession().selectOne(PERSON_MAPPING + "." + COUNT_OPERATION, example);
    }

    @Override
    public String getPluralEntityLabel(Locale locale) {
        return ResourceUtil.getString(PersonStrategy.class.getName(), getEntityTable(), locale);
    }

    @Override
    public Person newInstance() {
        return new Person(super.newInstance());
    }

    @Transactional
    @Override
    public Person findById(Long id, boolean runAsAdmin) {
        return findById(id, runAsAdmin, true, true, true, true);
    }

    @Transactional
    public Person findById(long id, boolean runAsAdmin, boolean loadName, boolean loadChildren,
            boolean loadDocument, boolean loadMilitaryServiceRelation) {
        DomainObject personObject = super.findById(id, runAsAdmin);
        if (personObject == null) {
            return null;
        }
        Person person = new Person(personObject);
        if (loadName) {
            loadName(person);
        }
        if (loadChildren) {
            loadChildren(person);
        }
        if (loadDocument) {
            loadDocument(person);
        }
        if (loadMilitaryServiceRelation) {
            loadMilitaryServiceRelation(person);
        }
        return person;
    }

    @Transactional
    private void loadName(Person person) {
        //first name
        for (Attribute firstNameAttribute : person.getAttributes(FIRST_NAME)) {
            Long nameId = firstNameAttribute.getValueId();
            if (nameId != null) {
                person.addFirstName(localeBean.getLocale(firstNameAttribute.getAttributeId()),
                        personNameBean.findById(PersonNameType.FIRST_NAME, nameId).getName());
            }
        }

        //last name
        for (Attribute lastNameAttribute : person.getAttributes(LAST_NAME)) {
            Long nameId = lastNameAttribute.getValueId();
            if (nameId != null) {
                person.addLastName(localeBean.getLocale(lastNameAttribute.getAttributeId()),
                        personNameBean.findById(PersonNameType.LAST_NAME, nameId).getName());
            }
        }

        //middle name
        for (Attribute middleNameAttribute : person.getAttributes(MIDDLE_NAME)) {
            Long nameId = middleNameAttribute.getValueId();
            if (nameId != null) {
                person.addMiddleName(localeBean.getLocale(middleNameAttribute.getAttributeId()),
                        personNameBean.findById(PersonNameType.MIDDLE_NAME, nameId).getName());
            }
        }
    }

    @Transactional
    public void loadDocument(Person person) {
        if (person.getDocument() == null) {
            long documentId = person.getAttribute(DOCUMENT).getValueId();
            Document document = documentStrategy.findById(documentId);
            person.setDocument(document);
        }
    }

    @Transactional
    public List<Document> findPreviousDocuments(Long personId) {
        if (personId == null) {
            return null;
        }
        List<Attribute> previousDocumentAttributes = sqlSession().selectList(PERSON_MAPPING + ".findPreviousDocumentAttributes",
                ImmutableMap.of("personId", personId, "personDocumentAT", DOCUMENT));
        List<Document> previousDocuments = newArrayList();
        for (Attribute previousDocumentAttribute : previousDocumentAttributes) {
            Document previousDocument = documentStrategy.findById(previousDocumentAttribute.getValueId());
            long documentTypeId = previousDocument.getDocumentTypeId();
            DomainObject documentType = documentTypeStrategy.findById(documentTypeId, true);
            previousDocument.setDocumentType(documentType);
            previousDocuments.add(previousDocument);
        }
        return previousDocuments;
    }

    @Transactional
    public void loadChildren(Person person) {
        List<Attribute> childrenAttributes = person.getAttributes(CHILDREN);
        if (childrenAttributes != null && !childrenAttributes.isEmpty() && person.getChildren().isEmpty()) {
            for (Attribute childAttribute : childrenAttributes) {
                Long childId = childAttribute.getValueId();
                DomainObjectExample example = new DomainObjectExample(childId);
                example.setAdmin(true);
                List<Person> children = find(example);
                if (children != null && children.size() == 1) {
                    Person child = children.get(0);
                    if (child != null) {
                        person.addChild(child);
                    }
                }
            }
        }
    }

    @Transactional
    public void loadMilitaryServiceRelation(Person person) {
        if (person.getMilitaryServiceRelation() == null) {
            Attribute militaryServiceRelationAttribute = person.getAttribute(MILITARY_SERVICE_RELATION);
            if (militaryServiceRelationAttribute != null) {
                final Long militaryServiceRelationId = militaryServiceRelationAttribute.getValueId();
                if (militaryServiceRelationId != null) {
                    person.setMilitaryServiceRelation(militaryServiceRelationStrategy.findById(militaryServiceRelationId, true));
                }
            }
        }
    }

    @Override
    protected void fillAttributes(DomainObject object) {
        List<Attribute> toAdd = newArrayList();
        for (EntityAttributeType attributeType : getEntity().getEntityAttributeTypes()) {
            if (!attributeType.isObsolete()) {
                if (object.getAttributes(attributeType.getId()).isEmpty()) {
                    if ((attributeType.getEntityAttributeValueTypes().size() == 1)
                            && !attributeType.getId().equals(CHILDREN)
                            && !attributeType.getId().equals(EXPLANATION)
                            && !NAME_ATTRIBUTE_IDS.contains(attributeType.getId())) {
                        Attribute attribute = new Attribute();
                        EntityAttributeValueType attributeValueType = attributeType.getEntityAttributeValueTypes().get(0);
                        attribute.setAttributeTypeId(attributeType.getId());
                        attribute.setValueTypeId(attributeValueType.getId());
                        attribute.setObjectId(object.getId());
                        attribute.setAttributeId(1L);

                        if (isSimpleAttributeType(attributeType)) {
                            attribute.setLocalizedValues(stringBean.newStringCultures());
                        }
                        toAdd.add(attribute);

                        //by default UKRAINE_CITIZENSHIP attribute set to TRUE.
                        if (attributeType.getId().equals(UKRAINE_CITIZENSHIP)) {
                            StringCulture systemLocaleStringCulture =
                                    stringBean.getSystemStringCulture(attribute.getLocalizedValues());
                            systemLocaleStringCulture.setValue(new BooleanConverter().toString(Boolean.TRUE));
                        }
                    }
                }
            }
        }

        updateNameAttributesForNewLocales(object);

        if (!toAdd.isEmpty()) {
            object.getAttributes().addAll(toAdd);
        }
    }

    private void updateNameAttributesForNewLocales(DomainObject person) {
        updateNameAttributeForNewLocales(person, LAST_NAME);
        updateNameAttributeForNewLocales(person, FIRST_NAME);
        updateNameAttributeForNewLocales(person, MIDDLE_NAME);
    }

    private void updateNameAttributeForNewLocales(DomainObject person, final long nameAttributeTypeId) {
        List<Attribute> nameAttributes = person.getAttributes(nameAttributeTypeId);
        person.removeAttribute(nameAttributeTypeId);
        for (final org.complitex.dictionary.entity.Locale locale : localeBean.getAllLocales()) {
            boolean found = false;
            for (Attribute nameAttribute : nameAttributes) {
                if (nameAttribute.getAttributeId().equals(locale.getId())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                Attribute attribute = new Attribute();
                attribute.setAttributeTypeId(nameAttributeTypeId);
                attribute.setValueTypeId(nameAttributeTypeId);
                attribute.setObjectId(person.getId());
                attribute.setAttributeId(locale.getId());
                nameAttributes.add(attribute);
            }
        }
        final long systemLocaleId = localeBean.getSystemLocaleObject().getId();
        Collections.sort(nameAttributes, new Comparator<Attribute>() {

            @Override
            public int compare(Attribute o1, Attribute o2) {
                if (o1.getAttributeId().equals(systemLocaleId)) {
                    return -1;
                }
                if (o2.getAttributeId().equals(systemLocaleId)) {
                    return 1;
                }
                return o1.getAttributeId().compareTo(o2.getAttributeId());
            }
        });
        person.getAttributes().addAll(nameAttributes);
    }

    @Override
    public long getDefaultOrderByAttributeId() {
        return DEFAULT_ORDER_BY_ID;
    }

    @Override
    public int getSearchTextFieldSize() {
        return 40;
    }

    @Override
    public Class<? extends WebPage> getHistoryPage() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String[] getDescriptionRoles() {
        return new String[]{SecurityRole.PERSON_MODULE_DESCRIPTION_EDIT};
    }

    @Transactional
    public List<Person> findByName(PersonAgeType personAgeType, String lastName, String firstName, String middleName,
            Locale locale) {
        if (Strings.isEmpty(lastName)) {
            throw new IllegalArgumentException("Last name is null or empty.");
        }
        DomainObjectExample example = new DomainObjectExample();
        example.setStatus(StatusType.ACTIVE.name());
        example.addAdditionalParam("last_name", lastName);
        example.setLocaleId(localeBean.convert(locale).getId());

        firstName = firstName != null ? firstName.trim() : null;
        if (Strings.isEmpty(firstName)) {
            firstName = null;
        }
        example.addAdditionalParam("first_name", firstName);

        middleName = middleName != null ? middleName.trim() : null;
        if (Strings.isEmpty(middleName)) {
            middleName = null;
        }
        example.addAdditionalParam("middle_name", middleName);

        prepareExampleForPermissionCheck(example);

        List<Person> results = newArrayList();
        List<Person> persons = sqlSession().selectList(PERSON_MAPPING + ".findByName", example);
        for (Person person : persons) {
            loadAttributes(person);
            boolean eligiblePerson = (personAgeType == PersonAgeType.ANY)
                    || (personAgeType == PersonAgeType.KID && person.isKid())
                    || (personAgeType == PersonAgeType.ADULT && !person.isKid());
            if (eligiblePerson) {
                loadName(person);
                loadDocument(person);
                results.add(person);
            }
        }
        return results;
    }

    @Transactional
    @Override
    protected void insertDomainObject(DomainObject object, Date insertDate) {
        Person person = (Person) object;
        person.getDocument().setSubjectIds(person.getSubjectIds());
        documentStrategy.insert(person.getDocument(), insertDate);
        updateDocumentAttribute(null, person);
        prepareForSaveNameAttributes(person);
        super.insertDomainObject(object, insertDate);
    }

    private void prepareForSaveNameAttributes(Person person) {
        prepareForSaveNameAttribute(person, LAST_NAME);
        prepareForSaveNameAttribute(person, FIRST_NAME);
        prepareForSaveNameAttribute(person, MIDDLE_NAME);
    }

    private void prepareForSaveNameAttribute(Person person, long nameAttributeTypeId) {
        List<Attribute> nameAttributes = person.getAttributes(nameAttributeTypeId);
        person.removeAttribute(nameAttributeTypeId);
        for (Attribute nameAttribute : nameAttributes) {
            if (localeBean.getSystemLocaleObject().getId().equals(nameAttribute.getAttributeId())
                    || nameAttribute.getValueId() != null) {
                person.addAttribute(nameAttribute);
            }
        }
    }

    @Transactional
    @Override
    protected void insertUpdatedDomainObject(DomainObject person, Date updateDate) {
        super.insertDomainObject(person, updateDate);
    }

    @Override
    public void insert(DomainObject person, Date insertDate) {
        setEditedByUserId(person);
        super.insert(person, insertDate);
    }

    private void setEditedByUserId(DomainObject person) {
        long userId = sessionBean.getCurrentUserId();
        stringBean.getSystemStringCulture(person.getAttribute(EDITED_BY_USER_ID).getLocalizedValues()).
                setValue(String.valueOf(userId));
    }

    public void setExplanation(Person person, String explanation) {
        person.removeAttribute(EXPLANATION);

        Attribute explAttribute = new Attribute();
        explAttribute.setLocalizedValues(stringBean.newStringCultures());
        stringBean.getSystemStringCulture(explAttribute.getLocalizedValues()).setValue(explanation);
        explAttribute.setAttributeTypeId(EXPLANATION);
        explAttribute.setValueTypeId(EXPLANATION);
        explAttribute.setAttributeId(1L);
        person.addAttribute(explAttribute);
    }

    @Transactional
    @Override
    public void update(DomainObject oldObject, DomainObject newObject, Date updateDate) {
        Person oldPerson = (Person) oldObject;
        Person newPerson = (Person) newObject;

        setEditedByUserId(newPerson);

        //handle explanation attribute: 
        // 1. archive old explanation if it is existed.

        final Attribute newExplAttribute = newPerson.getAttribute(EXPLANATION);
        newPerson.removeAttribute(EXPLANATION);
        final Attribute oldExplAttribute = oldPerson.getAttribute(EXPLANATION);
        oldPerson.removeAttribute(EXPLANATION);
        if (oldExplAttribute != null) {
            archiveAttribute(oldExplAttribute, updateDate);
        }

        //document altering
        if (newPerson.getDocument() != null) {
            newPerson.getDocument().setSubjectIds(newPerson.getSubjectIds());
            documentStrategy.update(oldPerson.getDocument(), newPerson.getDocument(), updateDate);

            if (newPerson.getReplacedDocument() != null) {
                documentStrategy.disable(newPerson.getDocument(), updateDate);
                newPerson.getReplacedDocument().setSubjectIds(newPerson.getSubjectIds());
                documentStrategy.insert(newPerson.getReplacedDocument(), updateDate);
            }

            updateDocumentAttribute(oldPerson, newPerson);
        }

        // if person was a kid but birth date has changed or time gone then it is need to update parent
        if (oldPerson.isKid() && !newPerson.isKid()) {
            removeKidFromParent(newPerson.getId(), updateDate);
        }

        prepareForSaveNameAttributes(oldPerson);
        prepareForSaveNameAttributes(newPerson);

        super.update(oldPerson, newPerson, updateDate);

        //handle explanation attribute: 
        // 2. insert new one
        if (newExplAttribute != null && newExplAttribute.getStartDate() == null) {
            newExplAttribute.setObjectId(newPerson.getId());
            newExplAttribute.setStartDate(updateDate);
            insertAttribute(newExplAttribute);
        }
    }

    @Transactional
    private void removeKidFromParent(final long childId, Date updateDate) {
        Map<String, Long> params = of("childId", childId, "personChildrenAT", CHILDREN);
        List<Long> parentIds = sqlSession().selectList(PERSON_MAPPING + ".findParents", params);
        for (long parentId : parentIds) {
            Person oldParent = findById(parentId, true);
            Person newParent = CloneUtil.cloneObject(oldParent);
            List<Attribute> children = newParent.getAttributes(CHILDREN);
            newParent.removeAttribute(CHILDREN);
            newParent.getAttributes().addAll(newArrayList(filter(children, new Predicate<Attribute>() {

                @Override
                public boolean apply(Attribute childrenAttribute) {
                    return !new Long(childId).equals(childrenAttribute.getValueId());
                }
            })));

            update(oldParent, newParent, updateDate);
        }
    }

    private void updateDocumentAttribute(Person oldPerson, Person newPerson) {
        Long documentId = null;
        if (oldPerson == null) {
            documentId = newPerson.getDocument().getId();
        } else {
            documentId = newPerson.getReplacedDocument() != null ? newPerson.getReplacedDocument().getId()
                    : newPerson.getDocument().getId();
        }
        newPerson.getAttribute(DOCUMENT).setValueId(documentId);
    }

    public static class PersonRegistration implements Serializable {

        private long registrationId;
        private Registration registration;
        private String addressEntity;
        private long addressTypeId;
        private long addressId;

        public long getAddressId() {
            return addressId;
        }

        public void setAddressId(long addressId) {
            this.addressId = addressId;
        }

        public long getAddressTypeId() {
            return addressTypeId;
        }

        public void setAddressTypeId(long addressTypeId) {
            this.addressTypeId = addressTypeId;
        }

        public long getRegistrationId() {
            return registrationId;
        }

        public void setRegistrationId(long registrationId) {
            this.registrationId = registrationId;
        }

        public String getAddressEntity() {
            return addressEntity;
        }

        public Registration getRegistration() {
            return registration;
        }

        private void setAddressEntity(String addressEntity) {
            this.addressEntity = addressEntity;
        }

        private void setRegistration(Registration registration) {
            this.registration = registration;
        }
    }

    @Transactional
    public int countPersonRegistrations(long personId) {
        return (Integer) sqlSession().selectOne(PERSON_MAPPING + ".countPersonRegistrations",
                newFindPersonRegistrationParameters(personId));
    }

    @Transactional
    public List<PersonRegistration> findPersonRegistrations(long personId) {
        List<PersonRegistration> personRegistrations = sqlSession().selectList(PERSON_MAPPING + ".findPersonRegistrations",
                newFindPersonRegistrationParameters(personId));
        for (PersonRegistration personRegistration : personRegistrations) {
            personRegistration.setRegistration(
                    registrationStrategy.findById(personRegistration.getRegistrationId(), true, false, true, true));
            personRegistration.setAddressEntity(ApartmentCardStrategy.getAddressEntity(personRegistration.getAddressTypeId()));
        }
        return personRegistrations;
    }

    private Map<String, Long> newFindPersonRegistrationParameters(long personId) {
        return of("registrationPersonAT", RegistrationStrategy.PERSON,
                "apartmentCardRegistrationAT", ApartmentCardStrategy.REGISTRATIONS,
                "apartmentCardAddressAT", ApartmentCardStrategy.ADDRESS,
                "personId", personId);
    }

    public static class PersonApartmentCardAddress implements Serializable {

        private long apartmentCardId;
        private String addressEntity;
        private long addressTypeId;
        private long addressId;

        public String getAddressEntity() {
            return addressEntity;
        }

        private void setAddressEntity(String addressEntity) {
            this.addressEntity = addressEntity;
        }

        public long getAddressId() {
            return addressId;
        }

        public void setAddressId(long addressId) {
            this.addressId = addressId;
        }

        public long getAddressTypeId() {
            return addressTypeId;
        }

        public void setAddressTypeId(long addressTypeId) {
            this.addressTypeId = addressTypeId;
        }

        public long getApartmentCardId() {
            return apartmentCardId;
        }

        public void setApartmentCardId(long apartmentCardId) {
            this.apartmentCardId = apartmentCardId;
        }
    }

    @Transactional
    public List<PersonApartmentCardAddress> findPersonApartmentCardAddresses(long personId) {
        List<PersonApartmentCardAddress> personApartmentCardAddresses = sqlSession().selectList(PERSON_MAPPING
                + ".findPersonApartmentCardAddresses", newFindPersonRegistrationParameters(personId));
        for (PersonApartmentCardAddress personApartmentCardAddress : personApartmentCardAddresses) {
            personApartmentCardAddress.setAddressEntity(ApartmentCardStrategy.getAddressEntity(personApartmentCardAddress.getAddressTypeId()));
        }
        return personApartmentCardAddresses;
    }

    @Transactional
    public String findPermanentRegistrationAddress(long personId, Locale locale) {
        Map<Object, Object> params = builder().putAll(newFindPersonRegistrationParameters(personId)).
                put("registrationTypeAT", RegistrationStrategy.REGISTRATION_TYPE).
                put("permanentRegistrationTypeId", RegistrationTypeStrategy.PERMANENT).
                build();
        List<PersonRegistration> personRegistrations = sqlSession().selectList(
                PERSON_MAPPING + ".findPermanentRegistrationAddress", params);
        if (!personRegistrations.isEmpty()) {
            return addressRendererBean.displayAddress(
                    ApartmentCardStrategy.getAddressEntity(personRegistrations.get(0).getAddressTypeId()),
                    personRegistrations.get(0).getAddressId(), locale);
        }
        return null;
    }

    @Transactional
    public void registerPersonDeath(Person person, Date deathDate, List<PersonRegistration> activePersonRegistrations,
            Locale locale) {
        Date updateDate = DateUtil.getCurrentDate();

        if (activePersonRegistrations != null && !activePersonRegistrations.isEmpty()) {
            for (PersonRegistration personRegistration : activePersonRegistrations) {
                final Registration oldRegistration = personRegistration.getRegistration();
                final Registration newRegistration = CloneUtil.cloneObject(oldRegistration);
                //departure reason
                stringBean.getSystemStringCulture(newRegistration.getAttribute(RegistrationStrategy.DEPARTURE_REASON).getLocalizedValues()).
                        setValue(ResourceUtil.getString(RESOURCE_BUNDLE, "death_departure_reason", locale));
                //departure date
                stringBean.getSystemStringCulture(newRegistration.getAttribute(RegistrationStrategy.DEPARTURE_DATE).getLocalizedValues()).
                        setValue(new DateConverter().toString(deathDate));
                registrationStrategy.update(oldRegistration, newRegistration, updateDate);
                registrationStrategy.disable(newRegistration, updateDate);
            }
        }

        removeKidFromParent(person.getId(), updateDate);

        Person newPerson = CloneUtil.cloneObject(person);
        stringBean.getSystemStringCulture(newPerson.getAttribute(PersonStrategy.DEATH_DATE).getLocalizedValues()).
                setValue(new DateConverter().toString(deathDate));
        update(person, newPerson, updateDate);

        documentStrategy.disable(person.getDocument(), updateDate);

        disable(person, updateDate);
    }

    @Transactional
    private void disable(Person person, Date endDate) {
        person.setEndDate(endDate);
        changeActivity(person, false);
    }

    /* History */
    private Map<String, Object> newModificationDateParams(long personId, Date date) {
        return ImmutableMap.<String, Object>of("personId", personId, "date", date, "personDocumentAT", DOCUMENT,
                "nontraceableAttributes", newArrayList(EXPLANATION, EDITED_BY_USER_ID));
    }

    public Date getPreviousModificationDate(long personId, Date date) {
        if (date == null) {
            date = DateUtil.getCurrentDate();
        }
        return (Date) sqlSession().selectOne(PERSON_MAPPING + ".getPreviousModificationDate",
                newModificationDateParams(personId, date));
    }

    public Date getNextModificationDate(long personId, Date date) {
        if (date == null) {
            return null;
        }
        return (Date) sqlSession().selectOne(PERSON_MAPPING + ".getNextModificationDate",
                newModificationDateParams(personId, date));
    }

    public Person getHistoryPerson(long personId, Date date) {
        DomainObject historyObject = super.findHistoryObject(personId, date);
        if (historyObject == null) {
            return null;
        }
        Person person = new Person(historyObject);

        //explanation
        Attribute explAttribute = person.getAttribute(EXPLANATION);
        if (explAttribute != null && !explAttribute.getStartDate().equals(date)) {
            person.removeAttribute(EXPLANATION);
        }

        updateNameAttributesForNewLocales(person);
        loadChildren(person);
        loadHistoryDocument(person, date);
        loadMilitaryServiceRelation(person);
        return person;
    }

    private void loadHistoryDocument(Person person, Date date) {
        long documentId = person.getAttribute(DOCUMENT).getValueId();
        Document document = documentStrategy.getHistoryDocument(documentId, date);
        person.setDocument(document);
    }

    public PersonModification getDistinctions(Person historyPerson, Date startDate) {
        PersonModification m = new PersonModification();
        final Date previousStartDate = getPreviousModificationDate(historyPerson.getId(), startDate);
        Person previousPerson = previousStartDate == null ? null
                : getHistoryPerson(historyPerson.getId(), previousStartDate);
        if (previousPerson == null) {
            for (Attribute current : historyPerson.getAttributes()) {
                if (!current.getAttributeTypeId().equals(CHILDREN)) {
                    m.addAttributeModification(current.getAttributeTypeId(), ModificationType.ADD);
                }
            }
            for (Person child : historyPerson.getChildren()) {
                m.addChildModificationType(child.getId(), ModificationType.ADD);
            }
            m.setDocumentModification(getDocumentDistinctions(historyPerson.getDocument(), previousStartDate));
        } else {
            //changes
            for (Attribute current : historyPerson.getAttributes()) {
                for (Attribute prev : previousPerson.getAttributes()) {
                    if (current.getAttributeTypeId().equals(prev.getAttributeTypeId())
                            && !current.getAttributeTypeId().equals(CHILDREN)
                            && !current.getAttributeTypeId().equals(EXPLANATION)
                            && !NAME_ATTRIBUTE_IDS.contains(current.getAttributeTypeId())) {

                        m.addAttributeModification(current.getAttributeTypeId(),
                                !current.getValueId().equals(prev.getValueId()) ? ModificationType.CHANGE
                                : ModificationType.NONE);
                    }
                }
            }

            //added
            for (Attribute current : historyPerson.getAttributes()) {
                if (!current.getAttributeTypeId().equals(CHILDREN)
                        && !current.getAttributeTypeId().equals(EXPLANATION)
                        && !NAME_ATTRIBUTE_IDS.contains(current.getAttributeTypeId())) {
                    boolean added = true;
                    for (Attribute prev : previousPerson.getAttributes()) {
                        if (current.getAttributeTypeId().equals(prev.getAttributeTypeId())) {
                            added = false;
                            break;
                        }
                    }
                    if (added) {
                        m.addAttributeModification(current.getAttributeTypeId(), ModificationType.ADD);
                    }
                }
            }

            //removed
            for (Attribute prev : previousPerson.getAttributes()) {
                if (!prev.getAttributeTypeId().equals(CHILDREN)
                        && !prev.getAttributeTypeId().equals(EXPLANATION)
                        && !NAME_ATTRIBUTE_IDS.contains(prev.getAttributeTypeId())) {
                    boolean removed = true;
                    for (Attribute current : historyPerson.getAttributes()) {
                        if (current.getAttributeTypeId().equals(prev.getAttributeTypeId())) {
                            removed = false;
                            break;
                        }
                    }
                    if (removed) {
                        m.addAttributeModification(prev.getAttributeTypeId(), ModificationType.REMOVE);
                    }
                }
            }

            //children
            for (Person current : historyPerson.getChildren()) {
                boolean added = true;
                for (Person prev : previousPerson.getChildren()) {
                    if (current.getId().equals(prev.getId())) {
                        added = false;
                        m.addChildModificationType(current.getId(), ModificationType.NONE);
                    }
                }
                if (added) {
                    m.addChildModificationType(current.getId(), ModificationType.ADD);
                }
            }
            for (Person prev : previousPerson.getChildren()) {
                boolean removed = true;
                for (Person current : historyPerson.getChildren()) {
                    if (prev.getId().equals(current.getId())) {
                        removed = false;
                        break;
                    }
                }
                if (removed) {
                    m.setChildRemoved(true);
                    break;
                }
            }


            //names
            for (final long nameAttributeTypeId : NAME_ATTRIBUTE_IDS) {
                boolean nameChanged = isNameAttributeModified(previousPerson, historyPerson, nameAttributeTypeId);
                m.addAttributeModification(nameAttributeTypeId, nameChanged ? ModificationType.CHANGE : ModificationType.NONE);
            }

            //document
            m.setDocumentModification(getDocumentDistinctions(historyPerson.getDocument(), previousStartDate));
        }
        m.setEditedByUserId(historyPerson.getEditedByUserId());
        m.setExplanation(historyPerson.getExplanation());
        return m;
    }

    public boolean isNameAttributeModified(Person oldPerson, Person newPerson, long nameAttributeTypeId) {
        boolean nameChanged = false;
        for (Attribute newAttr : newPerson.getAttributes(nameAttributeTypeId)) {
            if (!nameChanged) {
                boolean added = true;
                for (Attribute oldAttr : oldPerson.getAttributes(nameAttributeTypeId)) {
                    if (newAttr.getAttributeId().equals(oldAttr.getAttributeId())) {
                        added = false;
                        nameChanged = !Numbers.isEqual(newAttr.getValueId(), oldAttr.getValueId());
                        break;
                    }
                }
                if (added) {
                    nameChanged = true;
                    break;
                }
            }
        }
        if (!nameChanged) {
            for (Attribute oldAttr : oldPerson.getAttributes(nameAttributeTypeId)) {
                boolean removed = true;
                for (Attribute newAttr : newPerson.getAttributes(nameAttributeTypeId)) {
                    if (oldAttr.getAttributeId().equals(newAttr.getAttributeId())) {
                        removed = false;
                        break;
                    }
                }
                if (removed) {
                    nameChanged = true;
                    break;
                }
            }
        }
        return nameChanged;
    }

    private DocumentModification getDocumentDistinctions(Document historyDocument, Date previousStartDate) {
        DocumentModification m = new DocumentModification();
        Document previousDocument = previousStartDate == null ? null
                : documentStrategy.getHistoryDocument(historyDocument.getId(), previousStartDate);
        if (previousDocument == null) {
            m = new DocumentModification(true);
            for (Attribute current : historyDocument.getAttributes()) {
                m.addAttributeModification(current.getAttributeTypeId(), ModificationType.NONE);
            }
        } else {
            //changes
            for (Attribute current : historyDocument.getAttributes()) {
                for (Attribute prev : previousDocument.getAttributes()) {
                    if (current.getAttributeTypeId().equals(prev.getAttributeTypeId())) {
                        if (!current.getValueId().equals(prev.getValueId())) {
                            m.addAttributeModification(current.getAttributeTypeId(), ModificationType.CHANGE);
                        } else {
                            m.addAttributeModification(current.getAttributeTypeId(), ModificationType.NONE);
                        }
                        break;
                    }
                }
            }

            //added
            for (Attribute current : historyDocument.getAttributes()) {
                boolean added = true;
                for (Attribute prev : previousDocument.getAttributes()) {
                    if (current.getAttributeTypeId().equals(prev.getAttributeTypeId())) {
                        added = false;
                        break;
                    }
                }
                if (added) {
                    m.addAttributeModification(current.getAttributeTypeId(), ModificationType.ADD);
                }
            }

            //removed
            for (Attribute prev : previousDocument.getAttributes()) {
                boolean removed = true;
                for (Attribute current : historyDocument.getAttributes()) {
                    if (current.getAttributeTypeId().equals(prev.getAttributeTypeId())) {
                        removed = false;
                        break;
                    }
                }
                if (removed) {
                    m.addAttributeModification(prev.getAttributeTypeId(), ModificationType.REMOVE);

                }
            }
        }

        for (Attribute current : historyDocument.getAttributes()) {
            if (m.getAttributeModificationType(current.getAttributeTypeId()) == null) {
                m.addAttributeModification(current.getAttributeTypeId(), ModificationType.NONE);
            }
        }
        return m;
    }
}
