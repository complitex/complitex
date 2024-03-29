/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.complitex.pspoffice.person.strategy;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.apache.wicket.util.string.Strings;
import ru.complitex.common.converter.DateConverter;
import ru.complitex.common.converter.StringConverter;
import ru.complitex.common.entity.*;
import ru.complitex.common.service.SessionBean;
import ru.complitex.common.strategy.IStrategy;
import ru.complitex.common.strategy.StrategyFactory;
import ru.complitex.common.strategy.StringValueBean;
import ru.complitex.common.util.CloneUtil;
import ru.complitex.common.util.DateUtil;
import ru.complitex.common.util.StringValueUtil;
import ru.complitex.common.web.DictionaryFwSession;
import ru.complitex.common.web.component.ShowMode;
import ru.complitex.common.web.component.search.SearchComponentState;
import ru.complitex.pspoffice.ownerrelationship.strategy.OwnerRelationshipStrategy;
import ru.complitex.pspoffice.ownership.strategy.OwnershipFormStrategy;
import ru.complitex.pspoffice.person.strategy.entity.*;
import ru.complitex.pspoffice.person.strategy.entity.Person;
import ru.complitex.template.strategy.TemplateStrategy;
import ru.complitex.template.web.security.SecurityRole;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.*;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

/**
 *
 * @author Artem
 */
@Stateless
public class ApartmentCardStrategy extends TemplateStrategy {

    private static final String APARTMENT_CARD_MAPPING = ApartmentCardStrategy.class.getPackage().getName() + ".ApartmentCard";
    public static final String RESOURCE_BUNDLE = ApartmentCardStrategy.class.getName();
    /**
     * Attribute type ids
     */
    public static final long PERSONAL_ACCOUNT = 2400;
    public static final long OWNER = 2401;
    public static final long FORM_OF_OWNERSHIP = 2403;
    public static final long HOUSING_RIGHTS = 2404;
    public static final long REGISTRATIONS = 2405;
    public static final long EXPLANATION = 2406;
    public static final long EDITED_BY_USER_ID = 2407;
    public static final long OLD_SYSTEM_APARTMENT_CARD_ID = 2408;

    public static final long ADDRESS_ROOM = 2410;
    public static final long ADDRESS_APARTMENT = 2411;
    public static final long ADDRESS_BUILDING = 2412;

    /**
     * Set of persistable search state entities
     */
    private static final Set<String> SEARCH_STATE_ENTITES = ImmutableSet.of("country", "region", "city", "street", "building");

    private static class RegistrationsComparator implements Comparator<Registration> {

        long ownerId;

        RegistrationsComparator(long ownerId) {
            this.ownerId = ownerId;
        }

        private int compareByRegistrationDates(Registration o1, Registration o2) {
            // Персоны недавно выписавшиеся должны быть выше в списке чем персоны, выписавшиеся давно.
            {
                if (o1.isFinished() && o2.isFinished()) {
                    Date d1 = o1.getDepartureDate();
                    if (d1 == null) {
                        d1 = o1.getEndDate();
                    }
                    Date d2 = o2.getDepartureDate();
                    if (d2 == null) {
                        d2 = o2.getEndDate();
                    }
                    return d2.compareTo(d1);
                }
                if (o1.isFinished() || o2.isFinished()) {
                    return o1.isFinished() ? 1 : -1;
                }
            }

            // Персоны недавно прописанные должны быть выше в списке, чем персоны, прописанные давно.
            {
                Date d1 = o1.getRegistrationDate();
                Date d2 = o2.getRegistrationDate();
                return d2.compareTo(d1);
            }
        }

        @Override
        public int compare(Registration o1, Registration o2) {
            // Владелец всегда первый в списке.
            {
                long p1 = o1.getAttribute(RegistrationStrategy.PERSON).getValueId();
                long p2 = o2.getAttribute(RegistrationStrategy.PERSON).getValueId();

                // Если оба владельцы, значит один из них - выписан. Т.е. можно сравнивать по датам прописки и выписки.
                if (p1 == ownerId && p2 == ownerId) {
                    return compareByRegistrationDates(o1, o2);
                }

                if (p1 == ownerId || p2 == ownerId) {
                    return p1 == ownerId ? -1 : 1;
                }
            }

            return compareByRegistrationDates(o1, o2);
        }
    }
    @EJB
    private PersonStrategy personStrategy;
    @EJB
    private RegistrationStrategy registrationStrategy;
    @EJB
    private OwnershipFormStrategy ownershipFormStrategy;
    @EJB
    private StringValueBean stringBean;
    @EJB
    private StrategyFactory strategyFactory;
    @EJB
    private SessionBean sessionBean;

    @Override
    public String displayDomainObject(DomainObject object, Locale locale) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getEntityName() {
        return "apartment_card";
    }

    @Override
    public String[] getEditRoles() {
        return new String[]{SecurityRole.AUTHORIZED};
    }

    @Override
    public ApartmentCard newInstance() {
        ApartmentCard apartmentCard = new ApartmentCard(super.newInstance());
        apartmentCard.getSubjectIds().clear();
        return apartmentCard;
    }


    @Override
    public ApartmentCard getDomainObject(Long id, boolean runAsAdmin) {
        return findById(id, runAsAdmin, true, true, true);
    }

    public ApartmentCard findById(long id, boolean runAsAdmin, boolean loadOwner, boolean loadRegistrations,
            boolean loadOwnershipForm) {
        DomainObject apartmentCardObject = super.getDomainObject(id, runAsAdmin);
        if (apartmentCardObject == null) {
            return null;
        }
        ApartmentCard apartmentCard = new ApartmentCard(apartmentCardObject);
        if (loadOwner) {
            loadOwner(apartmentCard);
        }
        if (loadRegistrations) {
            loadAllRegistrations(apartmentCard);
        }
        if (loadOwnershipForm) {
            loadOwnershipForm(apartmentCard);
        }
        return apartmentCard;
    }


    private void loadOwnershipForm(ApartmentCard apartmentCard) {
        long ownershipFormId = apartmentCard.getAttribute(FORM_OF_OWNERSHIP).getValueId();
        DomainObject ownershipForm = ownershipFormStrategy.getDomainObject(ownershipFormId, true);
        apartmentCard.setOwnershipForm(ownershipForm);
    }


    private void loadOwner(ApartmentCard apartmentCard) {
        long ownerId = apartmentCard.getAttribute(OWNER).getValueId();
        Person owner = personStrategy.findById(ownerId, true, true, false, false, false);
        apartmentCard.setOwner(owner);
    }

    public String getAddressEntity(long addressValueTypeId) {
        if (ADDRESS_ROOM == addressValueTypeId) {
            return "room";
        } else if (ADDRESS_APARTMENT == addressValueTypeId) {
            return "apartment";
        } else if (ADDRESS_BUILDING == addressValueTypeId) {
            return "building";
        } else {
            throw new IllegalStateException("Address attribute expected to be of " + ADDRESS_ROOM + " or "
                    + ADDRESS_APARTMENT + " or " + ADDRESS_BUILDING + ". But was: " + addressValueTypeId);
        }
    }

    public String getAddressEntity(ApartmentCard apartmentCard) {
//        long valueTypeId = apartmentCard.getAttribute(ADDRESS).getValueTypeId();
        return getAddressEntity(ADDRESS_APARTMENT); //todo add attribute
    }

    @Override
    protected void fillAttributes(String dataSource, DomainObject object) {
        List<Attribute> toAdd = newArrayList();
        for (EntityAttribute entityAttribute : getEntity().getAttributes()) {
            if (!entityAttribute.isObsolete()) {
                if (object.getAttributes(entityAttribute.getId()).isEmpty()) {
                    if (!entityAttribute.getId().equals(REGISTRATIONS)
                            && !entityAttribute.getId().equals(EXPLANATION)) {
                        Attribute attribute = new Attribute();
                        attribute.setEntityAttributeId(entityAttribute.getId());
                        attribute.setObjectId(object.getObjectId());
                        attribute.setAttributeId(1L);

                        if (entityAttribute.getValueType().isSimple()) {
                            attribute.setStringValues(StringValueUtil.newStringValues());
                        }
                        toAdd.add(attribute);
                    }
                }
            }
        }
        if (!toAdd.isEmpty()) {
            object.getAttributes().addAll(toAdd);
        }
    }

    @Override
    protected Attribute fillManyValueTypesAttribute(EntityAttribute entityAttribute, Long objectId) {
        Attribute attribute = new Attribute();
        attribute.setEntityAttributeId(entityAttribute.getId());
        attribute.setObjectId(objectId);
        attribute.setAttributeId(1L);

        return attribute;
    }

    private void setEditedByUserId(DomainObject apartmentCard) {
        long userId = sessionBean.getCurrentUserId();
//        StringValueUtil.getSystemStringValue(apartmentCard.getAttribute(EDITED_BY_USER_ID).getStringValues()).
//                setValueId(String.valueOf(userId));
    }


    @Override
    public void insert(DomainObject domainObject, Date insertDate) {
        setEditedByUserId(domainObject);
        super.insert(domainObject, insertDate);
    }


    @Override
    public void update(DomainObject oldApartmentCard, DomainObject newApartmentCard, Date updateDate) {
        setEditedByUserId(newApartmentCard);

        //handle explanation attribute: 
        // 1. archive old explanation if it is existed.

        final Attribute newExplAttribute = newApartmentCard.getAttribute(EXPLANATION);
        newApartmentCard.removeAttribute(EXPLANATION);
        final Attribute oldExplAttribute = oldApartmentCard.getAttribute(EXPLANATION);
        oldApartmentCard.removeAttribute(EXPLANATION);
        if (oldExplAttribute != null) {
            archiveAttribute(oldExplAttribute, updateDate);
        }

        super.update(oldApartmentCard, newApartmentCard, updateDate);

        // 2. insert new one
        if (newExplAttribute != null && newExplAttribute.getStartDate() == null) {
            newExplAttribute.setObjectId(newApartmentCard.getObjectId());
            newExplAttribute.setStartDate(updateDate);
            insertAttribute(newExplAttribute);
        }
    }


    public boolean isLeafAddress(long addressId, String addressEntity) {
        boolean isLeaf = true;
        IStrategy addressStrategy = strategyFactory.getStrategy(addressEntity);
        String[] children = addressStrategy.getLogicalChildren();
        if (children != null && children.length > 0) {
            DomainObjectFilter example = new DomainObjectFilter();
            example.setParentId(addressId);
            example.setParentEntity(addressEntity);
            example.setAdmin(true);
            example.setStatus(ShowMode.ACTIVE.name());
            for (String child : children) {
                IStrategy childStrategy = strategyFactory.getStrategy(child);
                Long count = childStrategy.getCount(example);

                if (count > 0) {
                    isLeaf = false;
                    break;
                }
            }
        }
        return isLeaf;
    }

    private Map<String, Object> newSearchByAddressParams(Long addressId) {
        Map<String, Object> params = newHashMap();
        params.put("apartmentCardAddressAT", ApartmentCardStrategy.ADDRESS_APARTMENT);
        params.put("addressId", addressId);
        if (!sessionBean.isAdmin()) {
            params.put("userPermissionString", sessionBean.getPermissionString(getEntityName()));
        }
        return params;
    }


    public int countByAddress(String addressEntity, Long addressId) {
        checkEntity(addressEntity);
        Map<String, Object> params = newSearchByAddressParams(addressId);
        return (Integer) sqlSession().selectOne(APARTMENT_CARD_MAPPING + ".countBy" + Strings.capitalize(addressEntity), params);
    }

    private void checkEntity(String addressEntity) {
        if (!"apartment".equals(addressEntity) && !"building".equals(addressEntity)) {
            throw new IllegalArgumentException("Address entity expected to be of 'apartment' or 'building'. But was: " + addressEntity);
        }
    }


    public ApartmentCard findOneByAddress(String addressEntity, long addressId) {
        return findByAddress(addressEntity, addressId, 0, 1).get(0);
    }


    public List<ApartmentCard> findByAddress(String addressEntity, Long addressId, int start, int size) {
        checkEntity(addressEntity);
        Map<String, Object> params = newSearchByAddressParams(addressId);
        params.put("start", start);
        params.put("size", size);
        List<Long> apartmentCardIds = sqlSession().selectList(APARTMENT_CARD_MAPPING + ".findBy" + Strings.capitalize(addressEntity), params);
        List<ApartmentCard> apartmentCards = newArrayList();
        for (Long apartmentCardId : apartmentCardIds) {
            apartmentCards.add(findById(apartmentCardId, false, true, true, true));
        }
        return apartmentCards;
    }

    /**
     * Util method to get apartment for apartment and room based apartment cards.
     * @param apartmentCard
     * @return
     */

    public Long getApartmentId(ApartmentCard apartmentCard) {
        Long apartmentId = null;

        String addressEntity = getAddressEntity(apartmentCard);
        long addressId = apartmentCard.getAddressId();
        if (addressEntity.equals("room")) {
            DomainObject room = strategyFactory.getStrategy("room").getDomainObject(addressId, true);
            if (room.getParentEntityId().equals(100L)) { //parent is apartment
                apartmentId = room.getParentId();
            }
        } else if (addressEntity.equals("apartment")) {
            apartmentId = addressId;
        }

        return apartmentId;
    }


    public List<ApartmentCard> getNeighbourApartmentCards(ApartmentCard apartmentCard) {
        Long apartmentId = getApartmentId(apartmentCard);

        if (apartmentId == null) {
            return null;
        }

        List<ApartmentCard> neighbourApartmentCards = newArrayList();
        int count = countByAddress("apartment", apartmentId);
        for (ApartmentCard neighbourCard : findByAddress("apartment", apartmentId, 0, count)) {
            if (!neighbourCard.getObjectId().equals(apartmentCard.getObjectId())) {
                neighbourApartmentCards.add(neighbourCard);
            }
        }
        return neighbourApartmentCards;
    }


    private void loadAllRegistrations(ApartmentCard apartmentCard) {
        List<Registration> registrations = newArrayList();
        List<Attribute> registrationAttributes = apartmentCard.getAttributes(REGISTRATIONS);
        if (registrationAttributes != null && !registrationAttributes.isEmpty()) {
            for (Attribute registrationAttribute : registrationAttributes) {
                long registrationId = registrationAttribute.getValueId();
                registrations.add(registrationStrategy.findById(registrationId, true, true, true, true));
            }
        }
        if (!registrations.isEmpty()) {
            final long ownerId = apartmentCard.getAttribute(OWNER).getValueId();
            Collections.sort(registrations, new RegistrationsComparator(ownerId));
        }
        apartmentCard.setRegistrations(registrations);
    }


    private void addRegistration(ApartmentCard apartmentCard, Registration registration, long attributeId, Date insertDate) {
        registration.setSubjectIds(apartmentCard.getSubjectIds());
        registrationStrategy.insert(registration, insertDate);
        insertRegistrationAttribute(apartmentCard, registration.getObjectId(), attributeId, insertDate);
    }


    public void addRegistration(ApartmentCard apartmentCard, Registration registration, Date insertDate) {
        long attributeId = apartmentCard.getRegistrations().size() + 1;
        addRegistration(apartmentCard, registration, attributeId, insertDate);
    }


    private void insertRegistrationAttribute(ApartmentCard apartmentCard, long registrationId, long attributeId, Date insertDate) {
        Attribute registrationAttribute = new Attribute();
        registrationAttribute.setObjectId(apartmentCard.getObjectId());
        registrationAttribute.setAttributeId(attributeId);
        registrationAttribute.setEntityAttributeId(REGISTRATIONS);
        registrationAttribute.setValueId(registrationId);
        registrationAttribute.setStartDate(insertDate);
        insertAttribute(registrationAttribute);
    }


    public void removeRegistrations(List<Registration> removeRegistrations, RemoveRegistrationCard removeRegistrationCard) {
        Date updateDate = DateUtil.getCurrentDate();

        for (Registration registration : removeRegistrations) {
            Registration newRegistration = CloneUtil.cloneObject(registration);
            //departure reason
            StringValueUtil.getSystemStringValue(newRegistration.getAttribute(RegistrationStrategy.DEPARTURE_REASON).getStringValues()).
                    setValue(removeRegistrationCard.getReason());
            //departure date
            StringValueUtil.getSystemStringValue(newRegistration.getAttribute(RegistrationStrategy.DEPARTURE_DATE).getStringValues()).
                    setValue(new DateConverter().toString(removeRegistrationCard.getDate()));
            //departure address
            StringValueUtil.getSystemStringValue(newRegistration.getAttribute(RegistrationStrategy.DEPARTURE_COUNTRY).getStringValues()).
                    setValue(removeRegistrationCard.getCountry());
            StringValueUtil.getSystemStringValue(newRegistration.getAttribute(RegistrationStrategy.DEPARTURE_REGION).getStringValues()).
                    setValue(removeRegistrationCard.getRegion());
            StringValueUtil.getSystemStringValue(newRegistration.getAttribute(RegistrationStrategy.DEPARTURE_DISTRICT).getStringValues()).
                    setValue(removeRegistrationCard.getDistrict());
            StringValueUtil.getSystemStringValue(newRegistration.getAttribute(RegistrationStrategy.DEPARTURE_CITY).getStringValues()).
                    setValue(removeRegistrationCard.getCity());
            StringValueUtil.getSystemStringValue(newRegistration.getAttribute(RegistrationStrategy.DEPARTURE_STREET).getStringValues()).
                    setValue(removeRegistrationCard.getStreet());
            StringValueUtil.getSystemStringValue(newRegistration.getAttribute(RegistrationStrategy.DEPARTURE_BUILDING_NUMBER).getStringValues()).
                    setValue(removeRegistrationCard.getBuildingNumber());
            StringValueUtil.getSystemStringValue(newRegistration.getAttribute(RegistrationStrategy.DEPARTURE_BUILDING_CORP).getStringValues()).
                    setValue(removeRegistrationCard.getBuildingCorp());
            StringValueUtil.getSystemStringValue(newRegistration.getAttribute(RegistrationStrategy.DEPARTURE_APARTMENT).getStringValues()).
                    setValue(removeRegistrationCard.getApartment());
            //explanation
            registrationStrategy.setExplanation(newRegistration, removeRegistrationCard.getExplanation());

            registrationStrategy.update(registration, newRegistration, updateDate);
            registrationStrategy.disable(registration, updateDate);
        }
    }


    public void storeSearchState(DictionaryFwSession session, SearchComponentState searchComponentState) {
        SearchComponentState globalSearchComponentState = session.getGlobalSearchComponentState();
        globalSearchComponentState.updateState(searchComponentState);
        session.storeGlobalSearchComponentState();
    }


    public SearchComponentState restoreSearchState(DictionaryFwSession session) {
        SearchComponentState searchComponentState = new SearchComponentState();
        for (Map.Entry<String, DomainObject> searchFilterEntry : session.getGlobalSearchComponentState().entrySet()) {
            final String searchFilter = searchFilterEntry.getKey();
            final DomainObject filterObject = searchFilterEntry.getValue();
            if (SEARCH_STATE_ENTITES.contains(searchFilter)) {
                if (filterObject != null && filterObject.getObjectId() != null && filterObject.getObjectId() > 0) {
                    searchComponentState.put(searchFilter, filterObject);
                }
            }
        }
        return searchComponentState;
    }


    public void changeRegistrationType(List<Registration> registrationsToChangeType,
            ChangeRegistrationTypeCard changeRegistrationTypeCard) {
        Date updateDate = DateUtil.getCurrentDate();
        Long newRegistrationTypeId = changeRegistrationTypeCard.getRegistrationType().getObjectId();
        for (Registration registration : registrationsToChangeType) {
            if (!registration.getRegistrationType().getObjectId().equals(newRegistrationTypeId)) {
                Registration newRegistration = CloneUtil.cloneObject(registration);
                newRegistration.getAttribute(RegistrationStrategy.REGISTRATION_TYPE).setValueId(newRegistrationTypeId);
                registrationStrategy.setExplanation(newRegistration, changeRegistrationTypeCard.getExplanation());
                registrationStrategy.update(registration, newRegistration, updateDate);
            }
        }
    }


    public void registerOwner(ApartmentCard apartmentCard, RegisterOwnerCard registerOwnerCard, List<Person> children,
            Date insertDate) {
        long attributeId = apartmentCard.getAttributes(REGISTRATIONS).size() + 1;
        Person owner = apartmentCard.getOwner();
        //owner registration
        addRegistration(apartmentCard,
                newRegistration(owner.getObjectId(), registerOwnerCard, null), attributeId++, insertDate);

        //children registration
        if (registerOwnerCard.isRegisterChildren()) {
            for (Person child : children) {
                addRegistration(apartmentCard,
                        newRegistration(child.getObjectId(), registerOwnerCard,
                        child.getGender() == Gender.MALE ? OwnerRelationshipStrategy.SON : OwnerRelationshipStrategy.DAUGHTER),
                        attributeId++, insertDate);
            }
        }
    }

    private Registration newRegistration(long personId, RegisterOwnerCard registerOwnerCard, Long ownerRelationshipId) {
        Registration registration = registrationStrategy.newInstance();
        registration.getAttribute(RegistrationStrategy.PERSON).setValueId(personId);

        if (ownerRelationshipId != null) {
            registration.getAttribute(RegistrationStrategy.OWNER_RELATIONSHIP).setValueId(ownerRelationshipId);
        }

        StringValueUtil.getSystemStringValue(registration.getAttribute(RegistrationStrategy.REGISTRATION_DATE).getStringValues()).
                setValue(new DateConverter().toString(registerOwnerCard.getRegistrationDate()));
        registration.getAttribute(RegistrationStrategy.REGISTRATION_TYPE).setValueId(registerOwnerCard.getRegistrationType().getObjectId());
        StringValueUtil.getSystemStringValue(registration.getAttribute(RegistrationStrategy.ARRIVAL_COUNTRY).getStringValues()).
                setValue(new StringConverter().toString(registerOwnerCard.getCountry()));
        StringValueUtil.getSystemStringValue(registration.getAttribute(RegistrationStrategy.ARRIVAL_REGION).getStringValues()).
                setValue(new StringConverter().toString(registerOwnerCard.getRegion()));
        StringValueUtil.getSystemStringValue(registration.getAttribute(RegistrationStrategy.ARRIVAL_DISTRICT).getStringValues()).
                setValue(new StringConverter().toString(registerOwnerCard.getDistrict()));
        StringValueUtil.getSystemStringValue(registration.getAttribute(RegistrationStrategy.ARRIVAL_CITY).getStringValues()).
                setValue(new StringConverter().toString(registerOwnerCard.getCity()));
        StringValueUtil.getSystemStringValue(registration.getAttribute(RegistrationStrategy.ARRIVAL_STREET).getStringValues()).
                setValue(new StringConverter().toString(registerOwnerCard.getStreet()));
        StringValueUtil.getSystemStringValue(registration.getAttribute(RegistrationStrategy.ARRIVAL_BUILDING_NUMBER).getStringValues()).
                setValue(new StringConverter().toString(registerOwnerCard.getBuildingNumber()));
        StringValueUtil.getSystemStringValue(registration.getAttribute(RegistrationStrategy.ARRIVAL_BUILDING_CORP).getStringValues()).
                setValue(new StringConverter().toString(registerOwnerCard.getBuildingCorp()));
        StringValueUtil.getSystemStringValue(registration.getAttribute(RegistrationStrategy.ARRIVAL_APARTMENT).getStringValues()).
                setValue(new StringConverter().toString(registerOwnerCard.getApartment()));
        StringValueUtil.getSystemStringValue(registration.getAttribute(RegistrationStrategy.ARRIVAL_DATE).getStringValues()).
                setValue(new DateConverter().toString(registerOwnerCard.getArrivalDate()));
        return registration;
    }


    public boolean validateOwnerAddressUniqueness(long addressId, long addressTypeId, long ownerId, Long apartmentCardId) {
        Map<String, Long> params = newHashMap();
        params.put("apartmentCardAddressAT", ADDRESS_APARTMENT);
        params.put("addressId", addressId);
        params.put("addressTypeId", addressTypeId);
        params.put("apartmentCardOwnerAT", OWNER);
        params.put("ownerId", ownerId);
        params.put("apartmentCardId", apartmentCardId);
        return sqlSession().selectOne(APARTMENT_CARD_MAPPING + ".validateOwnerAddressUniqueness", params) == null;
    }


    public void registerChildren(RegisterChildrenCard registerChildrenCard, List<Person> children) {
        Date insertDate = DateUtil.getCurrentDate();
        ApartmentCard apartmentCard = findById(registerChildrenCard.getApartmentCardId(), true, false, false, false);
        long attributeId = apartmentCard.getAttributes(REGISTRATIONS).size() + 1;
        for (Person child : children) {
            addRegistration(apartmentCard, newChildRegistration(child, registerChildrenCard), attributeId++, insertDate);
        }
    }

    private Registration newChildRegistration(Person child, RegisterChildrenCard registerChildrenCard) {
        Registration registration = registrationStrategy.newInstance();
        registration.getAttribute(RegistrationStrategy.PERSON).setValueId(child.getObjectId());
        registration.getAttribute(RegistrationStrategy.OWNER_RELATIONSHIP).setValueId(
                child.getGender() == Gender.MALE ? OwnerRelationshipStrategy.SON : OwnerRelationshipStrategy.DAUGHTER);
        StringValueUtil.getSystemStringValue(registration.getAttribute(RegistrationStrategy.REGISTRATION_DATE).getStringValues()).
                setValue(new DateConverter().toString(registerChildrenCard.getRegistrationDate()));
        registration.getAttribute(RegistrationStrategy.REGISTRATION_TYPE).setValueId(registerChildrenCard.getRegistrationType().getObjectId());
        return registration;
    }


    public void disable(ApartmentCard apartmentCard, Date endDate) {
        apartmentCard.setEndDate(endDate);
        changeActivity(apartmentCard, false);
    }

    public SearchComponentState initAddressSearchComponentState(String addressEntity, long addressId) {
        SearchComponentState searchComponentState = new SearchComponentState();
        IStrategy addressStrategy = strategyFactory.getStrategy(addressEntity);
        DomainObject addressObject = addressStrategy.getDomainObject(addressId, true);
        EntityObjectInfo info = addressStrategy.findParentInSearchComponent(addressId, null);
        if (info != null) {
            searchComponentState = addressStrategy.getSearchComponentStateForParent(info.getId(), info.getEntityName(), null);
            searchComponentState.put(addressEntity, addressObject);
        }
        if (addressEntity.equals("apartment")) {
            DomainObject room = new DomainObject();
            room.setObjectId(SearchComponentState.NOT_SPECIFIED_ID);
            searchComponentState.put("room", room);
        } else if (addressEntity.equals("building")) {
            DomainObject room = new DomainObject();
            room.setObjectId(SearchComponentState.NOT_SPECIFIED_ID);
            searchComponentState.put("room", room);
            DomainObject apartment = new DomainObject();
            apartment.setObjectId(SearchComponentState.NOT_SPECIFIED_ID);
            searchComponentState.put("apartment", apartment);
        }
        return searchComponentState;
    }

    public void setExplanation(ApartmentCard apartmentCard, String explanation) {
        apartmentCard.removeAttribute(EXPLANATION);

        Attribute explAttribute = new Attribute();
        explAttribute.setStringValues(StringValueUtil.newStringValues());
        StringValueUtil.getSystemStringValue(explAttribute.getStringValues()).setValue(explanation);
        explAttribute.setEntityAttributeId(EXPLANATION);
        explAttribute.setAttributeId(1L);
        apartmentCard.addAttribute(explAttribute);
    }

    /* History */
    private Map<String, Object> newModificationDateParams(long apartmentCardId, Date date) {
        return ImmutableMap.<String, Object>of("apartmentCardId", apartmentCardId, "date", date,
                "apartmentCardRegisrationAT", REGISTRATIONS,
                "nontraceableAttributes", newArrayList(EXPLANATION, EDITED_BY_USER_ID),
                "registrationNontraceableAttributes", newArrayList(RegistrationStrategy.EXPLANATION,
                RegistrationStrategy.EDITED_BY_USER_ID));
    }

    public Date getPreviousModificationDate(long apartmentCardId, Date date) {
        if (date == null) {
            date = DateUtil.getCurrentDate();
        }
        return (Date) sqlSession().selectOne(APARTMENT_CARD_MAPPING + ".getPreviousModificationDate",
                newModificationDateParams(apartmentCardId, date));
    }

    public Date getNextModificationDate(long apartmentCardId, Date date) {
        if (date == null) {
            return null;
        }
        return (Date) sqlSession().selectOne(APARTMENT_CARD_MAPPING + ".getNextModificationDate",
                newModificationDateParams(apartmentCardId, date));
    }

    public ApartmentCardModification getDistinctions(ApartmentCard historyCard, Date startDate) {
        ApartmentCardModification m = new ApartmentCardModification();
        final Date previousStartDate = getPreviousModificationDate(historyCard.getObjectId(), startDate);
        ApartmentCard previousCard = previousStartDate == null ? null
                : getHistoryApartmentCard(historyCard.getObjectId(), previousStartDate);
        if (previousCard == null) {
            for (Attribute current : historyCard.getAttributes()) {
                if (!current.getEntityAttributeId().equals(REGISTRATIONS)) {
                    m.addAttributeModification(current.getEntityAttributeId(), ModificationType.ADD);
                }
            }
            for (Registration reg : historyCard.getRegistrations()) {
                m.addRegistrationModification(reg.getObjectId(),
                        new RegistrationModification().setModificationType(ModificationType.ADD));
            }
            m.setEditedByUserId(historyCard.getEditedByUserId());
        } else {
            //changes
            for (Attribute current : historyCard.getAttributes()) {
                for (Attribute prev : previousCard.getAttributes()) {
                    if (current.getEntityAttributeId().equals(prev.getEntityAttributeId())
                            && !current.getEntityAttributeId().equals(REGISTRATIONS)
                            && !current.getEntityAttributeId().equals(EXPLANATION)) {

                        ModificationType modificationType = ModificationType.NONE;
                        if (!current.getValueId().equals(prev.getValueId())) {
                            modificationType = ModificationType.CHANGE;
                            m.setEditedByUserId(historyCard.getEditedByUserId());
                            m.setExplanation(historyCard.getExplanation());
                        }
                        m.addAttributeModification(current.getEntityAttributeId(), modificationType);
                    }
                }
            }

            //added
            for (Attribute current : historyCard.getAttributes()) {
                if (!current.getEntityAttributeId().equals(REGISTRATIONS)
                        && !current.getEntityAttributeId().equals(EXPLANATION)) {
                    boolean added = true;
                    for (Attribute prev : previousCard.getAttributes()) {
                        if (current.getEntityAttributeId().equals(prev.getEntityAttributeId())) {
                            added = false;
                            break;
                        }
                    }
                    if (added) {
                        m.addAttributeModification(current.getEntityAttributeId(), ModificationType.ADD);
                        m.setEditedByUserId(historyCard.getEditedByUserId());
                        m.setExplanation(historyCard.getExplanation());
                    }
                }
            }

            //removed
            for (Attribute prev : previousCard.getAttributes()) {
                if (!prev.getEntityAttributeId().equals(REGISTRATIONS)
                        && !prev.getEntityAttributeId().equals(EXPLANATION)) {
                    boolean removed = true;
                    for (Attribute current : historyCard.getAttributes()) {
                        if (current.getEntityAttributeId().equals(prev.getEntityAttributeId())) {
                            removed = false;
                            break;
                        }
                    }
                    if (removed) {
                        m.addAttributeModification(prev.getEntityAttributeId(), ModificationType.REMOVE);
                        m.setEditedByUserId(historyCard.getEditedByUserId());
                        m.setExplanation(historyCard.getExplanation());
                    }
                }
            }

            //registrations
            for (Registration current : historyCard.getRegistrations()) {
                boolean added = true;
                for (Registration prev : previousCard.getRegistrations()) {
                    if (current.getObjectId().equals(prev.getObjectId())) {
                        added = false;
                        //removed
                        if (current.isFinished() && !prev.isFinished()) {
                            m.addRegistrationModification(current.getObjectId(),
                                    new RegistrationModification().setModificationType(ModificationType.REMOVE).
//                                    setEditedByUserId(current.getEditedByUserId()).
                                    setExplanation(current.getExplanation()));
                            break;
                        }
                        //changed
                        if (!current.isFinished() && !prev.isFinished()) {
                            m.addRegistrationModification(current.getObjectId(),
                                    registrationStrategy.getDistinctionsForApartmentCardHistory(current, previousStartDate));
                            break;
                        }
                        m.addRegistrationModification(current.getObjectId(),
                                new RegistrationModification().setModificationType(ModificationType.NONE));
                    }
                }
                if (added) {
                    m.addRegistrationModification(current.getObjectId(),
                            registrationStrategy.getDistinctionsForApartmentCardHistory(current, previousStartDate));
                }
            }
        }
        return m;
    }

    public ApartmentCard getHistoryApartmentCard(long apartmentCardId, Date date) {
        DomainObject historyObject = super.getHistoryObject(apartmentCardId, date);
        if (historyObject == null) {
            return null;
        }
        ApartmentCard card = new ApartmentCard(historyObject);

        //explanation
        Attribute explAttribute = card.getAttribute(EXPLANATION);
        if (explAttribute != null && !explAttribute.getStartDate().equals(date)) {
            card.removeAttribute(EXPLANATION);
        }

        loadOwner(card);
        loadHistoryRegistrations(card, date);
        loadOwnershipForm(card);
        return card;
    }

    private void loadHistoryRegistrations(ApartmentCard apartmentCard, Date date) {
        List<Registration> registrations = newArrayList();
        List<Attribute> registrationAttributes = apartmentCard.getAttributes(REGISTRATIONS);
        if (registrationAttributes != null && !registrationAttributes.isEmpty()) {
            for (Attribute registrationAttribute : registrationAttributes) {
                long registrationId = registrationAttribute.getValueId();
                Registration registration = registrationStrategy.getHistoryRegistration(registrationId, date);
                if (registration.isFinished() && registration.getEndDate().after(date)) {
                    registration.setStatus(Status.ACTIVE);
                    registration.removeAttribute(RegistrationStrategy.DEPARTURE_DATE);
                }
                registrations.add(registration);
            }
        }

        if (!registrations.isEmpty()) {
            final long ownerId = apartmentCard.getAttribute(OWNER).getValueId();
            Collections.sort(registrations, new RegistrationsComparator(ownerId));
        }
        apartmentCard.setRegistrations(registrations);
    }
}
