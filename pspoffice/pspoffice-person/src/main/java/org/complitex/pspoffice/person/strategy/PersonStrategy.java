package org.complitex.pspoffice.person.strategy;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.Strings;
import org.complitex.address.service.AddressRendererBean;
import org.complitex.common.converter.BooleanConverter;
import org.complitex.common.converter.DateConverter;
import org.complitex.common.entity.*;
import org.complitex.common.service.SessionBean;
import org.complitex.common.strategy.StringCultureBean;
import org.complitex.common.strategy.StringLocaleBean;
import org.complitex.common.util.*;
import org.complitex.pspoffice.document.strategy.DocumentStrategy;
import org.complitex.pspoffice.document.strategy.entity.Document;
import org.complitex.pspoffice.document_type.strategy.DocumentTypeStrategy;
import org.complitex.pspoffice.military.strategy.MilitaryServiceRelationStrategy;
import org.complitex.pspoffice.person.strategy.entity.*;
import org.complitex.pspoffice.person.strategy.entity.Person;
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

    private static final String PERSON_NS = PersonStrategy.class.getPackage().getName() + ".Person";
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
    private StringLocaleBean stringLocaleBean;

    @EJB
    private RegistrationStrategy registrationStrategy;

    @EJB
    private AddressRendererBean addressRendererBean;

    @EJB
    private SessionBean sessionBean;

    @EJB
    private ApartmentCardStrategy apartmentCardStrategy;

    @Override
    public String getEntityName() {
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
    public Long getDefaultSortAttributeTypeId() {
        return OrderBy.LAST_NAME.getOrderByAttributeId();
    }

    @Override
    public String displayDomainObject(DomainObject object, Locale locale) {
        Person person = (Person) object;
        Locale systemLocale = stringLocaleBean.getSystemLocale();
        return displayPerson(person.getFirstName(locale, systemLocale), person.getMiddleName(locale, systemLocale),
                person.getLastName(locale, systemLocale));
    }

    public String displayPerson(String firstName, String middleName, String lastName) {
        // return in format 'last_name fisrt_name middle_name'
        return lastName + " " + firstName + " " + middleName;
    }


    @Override
    public List<Person> getList(DomainObjectFilter example) {
        if (example.getObjectId() != null && example.getObjectId() <= 0) {
            return Collections.emptyList();
        }

        example.setEntityName(getEntityName());
        if (!example.isAdmin()) {
            prepareExampleForPermissionCheck(example);
        }

        List<Person> persons = sqlSession().selectList(PERSON_NS + ".selectPersons", example);
        for (Person person : persons) {
            loadAttributes(person);
            loadName(person);
            //load subject ids
            person.setSubjectIds(loadSubjects(person.getPermissionId()));
        }
        return persons;
    }


    @Override
    public Long getCount(DomainObjectFilter example) {
        if (example.getObjectId() != null && example.getObjectId() <= 0) {
            return 0L;
        }

        example.setEntityName(getEntityName());
        prepareExampleForPermissionCheck(example);
        return sqlSession().selectOne(PERSON_NS + ".selectPersonCount", example);
    }

    @Override
    public String getPluralEntityLabel(Locale locale) {
        return ResourceUtil.getString(PersonStrategy.class.getName(), getEntityName(), locale);
    }

    @Override
    public Person newInstance() {
        return new Person(super.newInstance());
    }


    @Override
    public Person getDomainObject(Long id, boolean runAsAdmin) {
        return findById(id, runAsAdmin, true, true, true, true);
    }


    public Person findById(long id, boolean runAsAdmin, boolean loadName, boolean loadChildren,
            boolean loadDocument, boolean loadMilitaryServiceRelation) {
        DomainObject personObject = super.getDomainObject(id, runAsAdmin);
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


    private void loadName(Person person) {
        //first name
        for (Attribute firstNameAttribute : person.getAttributes(FIRST_NAME)) {
            Long nameId = firstNameAttribute.getValueId();
            if (nameId != null) {
                person.addFirstName(stringLocaleBean.getLocale(firstNameAttribute.getAttributeId()),
                        personNameBean.findById(PersonNameType.FIRST_NAME, nameId).getName());
            }
        }

        //last name
        for (Attribute lastNameAttribute : person.getAttributes(LAST_NAME)) {
            Long nameId = lastNameAttribute.getValueId();
            if (nameId != null) {
                person.addLastName(stringLocaleBean.getLocale(lastNameAttribute.getAttributeId()),
                        personNameBean.findById(PersonNameType.LAST_NAME, nameId).getName());
            }
        }

        //middle name
        for (Attribute middleNameAttribute : person.getAttributes(MIDDLE_NAME)) {
            Long nameId = middleNameAttribute.getValueId();
            if (nameId != null) {
                person.addMiddleName(stringLocaleBean.getLocale(middleNameAttribute.getAttributeId()),
                        personNameBean.findById(PersonNameType.MIDDLE_NAME, nameId).getName());
            }
        }
    }


    public void loadDocument(Person person) {
        if (person.getDocument() == null) {
            long documentId = person.getAttribute(DOCUMENT).getValueId();
            Document document = documentStrategy.findById(documentId);
            person.setDocument(document);
        }
    }


    public List<Document> findPreviousDocuments(Long personId) {
        if (personId == null) {
            return null;
        }
        List<Attribute> previousDocumentAttributes = sqlSession().selectList(PERSON_NS + ".findPreviousDocumentAttributes",
                ImmutableMap.of("personId", personId, "personDocumentAT", DOCUMENT));
        List<Document> previousDocuments = newArrayList();
        for (Attribute previousDocumentAttribute : previousDocumentAttributes) {
            Document previousDocument = documentStrategy.findById(previousDocumentAttribute.getValueId());
            long documentTypeId = previousDocument.getDocumentTypeId();
            DomainObject documentType = documentTypeStrategy.getDomainObject(documentTypeId, true);
            previousDocument.setDocumentType(documentType);
            previousDocuments.add(previousDocument);
        }
        return previousDocuments;
    }


    public void loadChildren(Person person) {
        List<Attribute> childrenAttributes = person.getAttributes(CHILDREN);
        if (childrenAttributes != null && !childrenAttributes.isEmpty() && person.getChildren().isEmpty()) {
            for (Attribute childAttribute : childrenAttributes) {
                Long childId = childAttribute.getValueId();
                DomainObjectFilter example = new DomainObjectFilter(childId);
                example.setAdmin(true);
                List<Person> children = getList(example);
                if (children != null && children.size() == 1) {
                    Person child = children.get(0);
                    if (child != null) {
                        person.addChild(child);
                    }
                }
            }
        }
    }


    public void loadMilitaryServiceRelation(Person person) {
        if (person.getMilitaryServiceRelation() == null) {
            Attribute militaryServiceRelationAttribute = person.getAttribute(MILITARY_SERVICE_RELATION);
            if (militaryServiceRelationAttribute != null) {
                final Long militaryServiceRelationId = militaryServiceRelationAttribute.getValueId();
                if (militaryServiceRelationId != null) {
                    person.setMilitaryServiceRelation(militaryServiceRelationStrategy.getDomainObject(militaryServiceRelationId, true));
                }
            }
        }
    }

    @Override
    protected void fillAttributes(DomainObject object) {
        List<Attribute> toAdd = newArrayList();
        for (AttributeType attributeType : getEntity().getAttributeTypes()) {
            if (!attributeType.isObsolete()) {
                if (object.getAttributes(attributeType.getId()).isEmpty()) {
                    if ((attributeType.getAttributeValueTypes().size() == 1)
                            && !attributeType.getId().equals(CHILDREN)
                            && !attributeType.getId().equals(EXPLANATION)
                            && !NAME_ATTRIBUTE_IDS.contains(attributeType.getId())) {
                        Attribute attribute = new Attribute();
                        AttributeValueType attributeValueType = attributeType.getAttributeValueTypes().get(0);
                        attribute.setAttributeTypeId(attributeType.getId());
                        attribute.setValueTypeId(attributeValueType.getId());
                        attribute.setObjectId(object.getObjectId());
                        attribute.setAttributeId(1L);

                        if (isSimpleAttributeType(attributeType)) {
                            attribute.setStringCultures(StringCultures.newStringCultures());
                        }
                        toAdd.add(attribute);

                        //by default UKRAINE_CITIZENSHIP attribute set to TRUE.
                        if (attributeType.getId().equals(UKRAINE_CITIZENSHIP)) {
                            StringCulture systemLocaleStringCulture =
                                    StringCultures.getSystemStringCulture(attribute.getStringCultures());
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
        for (final StringLocale stringLocale : stringLocaleBean.getAllLocales()) {
            boolean found = false;
            for (Attribute nameAttribute : nameAttributes) {
                if (nameAttribute.getAttributeId().equals(stringLocale.getId())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                Attribute attribute = new Attribute();
                attribute.setAttributeTypeId(nameAttributeTypeId);
                attribute.setValueTypeId(nameAttributeTypeId);
                attribute.setObjectId(person.getObjectId());
                attribute.setAttributeId(stringLocale.getId());
                nameAttributes.add(attribute);
            }
        }
        final long systemLocaleId = stringLocaleBean.getSystemStringLocale().getId();
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
    public Long getDefaultOrderByAttributeId() {
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


    public List<Person> findByName(PersonAgeType personAgeType, String lastName, String firstName, String middleName,
            Locale locale) {
        if (Strings.isEmpty(lastName)) {
            throw new IllegalArgumentException("Last name is null or empty.");
        }
        DomainObjectFilter example = new DomainObjectFilter();
        example.setStatus(StatusType.ACTIVE.name());
        example.addAdditionalParam("last_name", lastName);
        example.setLocaleId(stringLocaleBean.convert(locale).getId());

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
        List<Person> persons = sqlSession().selectList(PERSON_NS + ".findByName", example);
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
            if (stringLocaleBean.getSystemStringLocale().getId().equals(nameAttribute.getAttributeId())
                    || nameAttribute.getValueId() != null) {
                person.addAttribute(nameAttribute);
            }
        }
    }


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
        StringCultures.getSystemStringCulture(person.getAttribute(EDITED_BY_USER_ID).getStringCultures()).
                setValue(String.valueOf(userId));
    }

    public void setExplanation(Person person, String explanation) {
        person.removeAttribute(EXPLANATION);

        Attribute explAttribute = new Attribute();
        explAttribute.setStringCultures(StringCultures.newStringCultures());
        StringCultures.getSystemStringCulture(explAttribute.getStringCultures()).setValue(explanation);
        explAttribute.setAttributeTypeId(EXPLANATION);
        explAttribute.setValueTypeId(EXPLANATION);
        explAttribute.setAttributeId(1L);
        person.addAttribute(explAttribute);
    }


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
            removeKidFromParent(newPerson.getObjectId(), updateDate);
        }

        prepareForSaveNameAttributes(oldPerson);
        prepareForSaveNameAttributes(newPerson);

        super.update(oldPerson, newPerson, updateDate);

        //handle explanation attribute: 
        // 2. insert new one
        if (newExplAttribute != null && newExplAttribute.getStartDate() == null) {
            newExplAttribute.setObjectId(newPerson.getObjectId());
            newExplAttribute.setStartDate(updateDate);
            insertAttribute(newExplAttribute);
        }
    }


    private void removeKidFromParent(final long childId, Date updateDate) {
        Map<String, Long> params = of("childId", childId, "personChildrenAT", CHILDREN);
        List<Long> parentIds = sqlSession().selectList(PERSON_NS + ".findParents", params);
        for (long parentId : parentIds) {
            Person oldParent = getDomainObject(parentId, true);
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
            documentId = newPerson.getDocument().getObjectId();
        } else {
            documentId = newPerson.getReplacedDocument() != null ? newPerson.getReplacedDocument().getObjectId()
                    : newPerson.getDocument().getObjectId();
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


    public int countPersonRegistrations(long personId) {
        return (Integer) sqlSession().selectOne(PERSON_NS + ".countPersonRegistrations",
                newFindPersonRegistrationParameters(personId));
    }

    public List<PersonRegistration> findPersonRegistrations(long personId) {
        List<PersonRegistration> personRegistrations = sqlSession().selectList(PERSON_NS + ".findPersonRegistrations",
                newFindPersonRegistrationParameters(personId));
        for (PersonRegistration personRegistration : personRegistrations) {
            personRegistration.setRegistration(
                    registrationStrategy.findById(personRegistration.getRegistrationId(), true, false, true, true));
            personRegistration.setAddressEntity(apartmentCardStrategy.getAddressEntity(personRegistration.getAddressTypeId()));
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


    public List<PersonApartmentCardAddress> findPersonApartmentCardAddresses(long personId) {
        List<PersonApartmentCardAddress> personApartmentCardAddresses = sqlSession().selectList(PERSON_NS
                + ".findPersonApartmentCardAddresses", newFindPersonRegistrationParameters(personId));
        for (PersonApartmentCardAddress personApartmentCardAddress : personApartmentCardAddresses) {
            personApartmentCardAddress.setAddressEntity(apartmentCardStrategy.getAddressEntity(personApartmentCardAddress.getAddressTypeId()));
        }
        return personApartmentCardAddresses;
    }


    public String findPermanentRegistrationAddress(long personId, Locale locale) {
        Map<Object, Object> params = builder().putAll(newFindPersonRegistrationParameters(personId)).
                put("registrationTypeAT", RegistrationStrategy.REGISTRATION_TYPE).
                put("permanentRegistrationTypeId", RegistrationTypeStrategy.PERMANENT).
                build();
        List<PersonRegistration> personRegistrations = sqlSession().selectList(
                PERSON_NS + ".findPermanentRegistrationAddress", params);
        if (!personRegistrations.isEmpty()) {
            return addressRendererBean.displayAddress(
                    apartmentCardStrategy.getAddressEntity(personRegistrations.get(0).getAddressTypeId()),
                    personRegistrations.get(0).getAddressId(), locale);
        }
        return null;
    }


    public void registerPersonDeath(Person person, Date deathDate, List<PersonRegistration> activePersonRegistrations,
            Locale locale) {
        Date updateDate = DateUtil.getCurrentDate();

        if (activePersonRegistrations != null && !activePersonRegistrations.isEmpty()) {
            for (PersonRegistration personRegistration : activePersonRegistrations) {
                final Registration oldRegistration = personRegistration.getRegistration();
                final Registration newRegistration = CloneUtil.cloneObject(oldRegistration);
                //departure reason
                StringCultures.getSystemStringCulture(newRegistration.getAttribute(RegistrationStrategy.DEPARTURE_REASON).getStringCultures()).
                        setValue(ResourceUtil.getString(RESOURCE_BUNDLE, "death_departure_reason", locale));
                //departure date
                StringCultures.getSystemStringCulture(newRegistration.getAttribute(RegistrationStrategy.DEPARTURE_DATE).getStringCultures()).
                        setValue(new DateConverter().toString(deathDate));
                registrationStrategy.update(oldRegistration, newRegistration, updateDate);
                registrationStrategy.disable(newRegistration, updateDate);
            }
        }

        removeKidFromParent(person.getObjectId(), updateDate);

        Person newPerson = CloneUtil.cloneObject(person);
        StringCultures.getSystemStringCulture(newPerson.getAttribute(PersonStrategy.DEATH_DATE).getStringCultures()).
                setValue(new DateConverter().toString(deathDate));
        update(person, newPerson, updateDate);

        documentStrategy.disable(person.getDocument(), updateDate);

        disable(person, updateDate);
    }


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
        return (Date) sqlSession().selectOne(PERSON_NS + ".getPreviousModificationDate",
                newModificationDateParams(personId, date));
    }

    public Date getNextModificationDate(long personId, Date date) {
        if (date == null) {
            return null;
        }
        return (Date) sqlSession().selectOne(PERSON_NS + ".getNextModificationDate",
                newModificationDateParams(personId, date));
    }

    public Person getHistoryPerson(long personId, Date date) {
        DomainObject historyObject = super.getHistoryObject(personId, date);
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
        final Date previousStartDate = getPreviousModificationDate(historyPerson.getObjectId(), startDate);
        Person previousPerson = previousStartDate == null ? null
                : getHistoryPerson(historyPerson.getObjectId(), previousStartDate);
        if (previousPerson == null) {
            for (Attribute current : historyPerson.getAttributes()) {
                if (!current.getAttributeTypeId().equals(CHILDREN)) {
                    m.addAttributeModification(current.getAttributeTypeId(), ModificationType.ADD);
                }
            }
            for (Person child : historyPerson.getChildren()) {
                m.addChildModificationType(child.getObjectId(), ModificationType.ADD);
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
                    if (current.getObjectId().equals(prev.getObjectId())) {
                        added = false;
                        m.addChildModificationType(current.getObjectId(), ModificationType.NONE);
                    }
                }
                if (added) {
                    m.addChildModificationType(current.getObjectId(), ModificationType.ADD);
                }
            }
            for (Person prev : previousPerson.getChildren()) {
                boolean removed = true;
                for (Person current : historyPerson.getChildren()) {
                    if (prev.getObjectId().equals(current.getObjectId())) {
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
                : documentStrategy.getHistoryDocument(historyDocument.getObjectId(), previousStartDate);
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
