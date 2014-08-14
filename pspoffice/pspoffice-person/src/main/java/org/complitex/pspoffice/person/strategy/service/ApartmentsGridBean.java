/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.service;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.complitex.address.strategy.apartment.ApartmentStrategy;
import org.complitex.address.strategy.room.RoomStrategy;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.entity.example.DomainObjectExample;
import org.complitex.dictionary.service.AbstractBean;
import org.complitex.dictionary.service.LocaleBean;
import org.complitex.dictionary.service.SessionBean;
import org.complitex.dictionary.strategy.organization.IOrganizationStrategy;
import org.complitex.pspoffice.person.strategy.ApartmentCardStrategy;
import org.complitex.pspoffice.person.strategy.entity.ApartmentCard;
import org.complitex.pspoffice.person.strategy.entity.grid.ApartmentsGridEntity;
import org.complitex.pspoffice.person.strategy.entity.grid.ApartmentsGridFilter;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.*;

/**
 *
 * @author Artem
 */
@Stateless
public class ApartmentsGridBean extends AbstractBean {

    private static final String MAPPING = ApartmentsGridBean.class.getName();

    @EJB
    private ApartmentStrategy apartmentStrategy;

    @EJB
    private RoomStrategy roomStrategy;

    @EJB
    private ApartmentCardStrategy apartmentCardStrategy;

    @EJB
    private SessionBean sessionBean;

    @EJB(lookup = IOrganizationStrategy.BEAN_LOOKUP)
    private IOrganizationStrategy organizationStrategy;

    @EJB
    private LocaleBean localeBean;

    public ApartmentsGridFilter newFilter(long buildingId, Locale locale) {
        final boolean isAdmin = sessionBean.isAdmin();
        return new ApartmentsGridFilter(buildingId,
                !isAdmin ? sessionBean.getPermissionString("apartment") : null,
                !isAdmin ? sessionBean.getPermissionString("room") : null, locale);
    }

    private Map<String, Object> newParamsMap(ApartmentsGridFilter filter) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("apartmentNameAT", ApartmentStrategy.NAME);
        params.put("roomNameAT", RoomStrategy.NAME);
        params.put("buildingId", filter.getBuildingId());
        params.put("number", filter.getNumber());
        params.put("apartmentPermissionString", filter.getApartmentPermissionString());
        params.put("roomPermissionString", filter.getRoomPermissionString());
        params.put("start", filter.getStart());
        params.put("size", filter.getSize());
        return params;
    }

    public int count(ApartmentsGridFilter filter) {
        return (Integer) sqlSession().selectOne(MAPPING + ".count", newParamsMap(filter));
    }

    private List<? extends DomainObject> findRooms(long apartmentId) {
        DomainObjectExample example = new DomainObjectExample();
        roomStrategy.configureExample(example, ImmutableMap.of("apartment", apartmentId), null);
        return roomStrategy.find(example);
    }

    public List<ApartmentsGridEntity> find(ApartmentsGridFilter filter) {
        Map<String, Object> params = newParamsMap(filter);
        params.put("sortLocaleId", localeBean.convert(filter.getLocale()).getId());
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> data = sqlSession().selectList(MAPPING + ".find", params);
        final List<ApartmentsGridEntity> result = Lists.newArrayList();
        if (data != null && !data.isEmpty()) {
            for (Map<String, Object> item : data) {
                //apartment/room
                final long objectId = (Long) item.get("objectId");
                final String entity = (String) item.get("entity");

                DomainObject object = null;
                if (entity.equals("apartment")) {
                    object = apartmentStrategy.findById(objectId, true);
                } else {
                    object = roomStrategy.findById(objectId, true);
                }

                //apartment
                final String number = entity.equals("apartment")
                        ? apartmentStrategy.displayDomainObject(object, filter.getLocale())
                        : roomStrategy.displayDomainObject(object, filter.getLocale());

                //rooms
                final List<DomainObject> rooms = Lists.newArrayList();
                if (entity.equals("apartment")) {
                    rooms.addAll(findRooms(objectId));
                }

                //apartment cards
                List<ApartmentCard> apartmentCards = Lists.newArrayList();
                if (entity.equals("apartment")) {
                    final int apartmentCardsCount = apartmentCardStrategy.countByAddress("apartment", objectId);
                    apartmentCards = apartmentCardStrategy.findByAddress("apartment", objectId, 0, apartmentCardsCount);
                }

                //registered
                int registered = 0;
                for (ApartmentCard apartmentCard : apartmentCards) {
                    registered += apartmentCard.getRegisteredCount();
                }

                final List<DomainObject> organizations = Lists.newArrayList();
                final Set<Long> organizationIds = object.getSubjectIds();
                for (long organizationId : organizationIds) {
                    if (organizationId > 0) {
                        final DomainObject organization = organizationStrategy.findById(organizationId, true);
                        organizations.add(organization);
                    }
                }

                result.add(new ApartmentsGridEntity(number, entity, objectId,
                        Collections.unmodifiableList(rooms), Collections.unmodifiableList(apartmentCards),
                        registered, Collections.unmodifiableList(organizations)));
            }
        }
        return result;
    }
}
