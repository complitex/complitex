/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.importing.legacy.service;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.complitex.address.strategy.apartment.ApartmentStrategy;
import org.complitex.dictionary.entity.Attribute;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.service.AbstractBean;
import org.complitex.pspoffice.importing.legacy.entity.ApartmentCardCorrection;
import org.complitex.pspoffice.importing.legacy.service.exception.TooManyResultsException;
import org.complitex.pspoffice.person.strategy.ApartmentCardStrategy;
import org.complitex.pspoffice.person.strategy.entity.ApartmentCard;

/**
 *
 * @author Artem
 */
@Stateless
public class ApartmentCardCorrectionBean extends AbstractBean {

    private static final String MAPPING_NAMESPACE = ApartmentCardCorrectionBean.class.getName();
    @EJB
    private ApartmentCardStrategy apartmentCardStrategy;
    @EJB
    private ApartmentStrategy apartmentStrategy;

    public void insert(ApartmentCardCorrection apartmentCardCorrection) {
        sqlSession().insert(MAPPING_NAMESPACE + ".insert", apartmentCardCorrection);
    }

    public boolean exists() {
        return (Integer) sqlSession().selectOne(MAPPING_NAMESPACE + ".exists") > 0;
    }

    public void cleanData() {
        sqlSession().delete(MAPPING_NAMESPACE + ".delete");
    }

    public void update(ApartmentCardCorrection apartmentCardCorrection) {
        sqlSession().update(MAPPING_NAMESPACE + ".update", apartmentCardCorrection);
    }

    public void clearProcessingStatus() {
        sqlSession().update(MAPPING_NAMESPACE + ".clearProcessingStatus");
    }

    public int countForProcessing() {
        return (Integer) sqlSession().selectOne(MAPPING_NAMESPACE + ".countForProcessing", Utils.NONARCHIVE_INDICATOR);
    }

    public int archiveCount() {
        return (Integer) sqlSession().selectOne(MAPPING_NAMESPACE + ".archiveCount", Utils.NONARCHIVE_INDICATOR);
    }

    public List<ApartmentCardCorrection> findForProcessing(int size) {
        return sqlSession().selectList(MAPPING_NAMESPACE + ".findForProcessing",
                ImmutableMap.of("size", size, "NONARCHIVE_INDICATOR", Utils.NONARCHIVE_INDICATOR));
    }

    public Long findSystemApartment(long systemBuildingId, String apartment) throws TooManyResultsException {
        List<Long> ids = sqlSession().selectList(MAPPING_NAMESPACE + ".findSystemApartment",
                ImmutableMap.<String, Object>of("apartmentNameAT", ApartmentStrategy.NAME, "name", apartment,
                "buildingId", systemBuildingId, "localeId", Utils.RUSSIAN_LOCALE_ID));

        if (ids.size() == 1) {
            return ids.get(0);
        } else if (ids.isEmpty()) {
            return null;
        } else {
            throw new TooManyResultsException();
        }
    }

    public DomainObject newApartment(long systemBuildingId, String apartment, long jekId) {
        DomainObject a = apartmentStrategy.newInstance();
        Utils.setValue(a.getAttribute(ApartmentStrategy.NAME), apartment);
        a.setParentId(systemBuildingId);
        a.setParentEntityId(ApartmentStrategy.PARENT_ENTITY_ID);
        a.setSubjectIds(Sets.newHashSet(jekId));
        return a;
    }

    public Long findSystemApartmentCard(long apartmentCardCorrectionId, long apartmentId) throws TooManyResultsException {
        List<Long> ids = sqlSession().selectList(MAPPING_NAMESPACE + ".findSystemApartmentCard",
                ImmutableMap.of("apartmentCardAddressAT", ApartmentCardStrategy.ADDRESS,
                "apartmentCardApartmentATID", ApartmentCardStrategy.ADDRESS_APARTMENT,
                "apartmentCardIdAT", ApartmentCardStrategy.OLD_SYSTEM_APARTMENT_CARD_ID,
                "apartmentId", apartmentId, "apartmentCardId", apartmentCardCorrectionId));
        if (ids.size() == 1) {
            return ids.get(0);
        } else if (ids.isEmpty()) {
            return null;
        } else {
            throw new TooManyResultsException();
        }
    }

    public ApartmentCard newApartmentCard(long apartmentCardId, long apartmentId, long ownerId, long ownershipFromId, long jekId) {
        ApartmentCard c = apartmentCardStrategy.newInstance();

        //address
        Attribute addressAttribute = c.getAttribute(ApartmentCardStrategy.ADDRESS);
        addressAttribute.setValueTypeId(ApartmentCardStrategy.ADDRESS_APARTMENT);
        addressAttribute.setValueId(apartmentId);

        //owner
        Attribute ownerAttribute = c.getAttribute(ApartmentCardStrategy.OWNER);
        ownerAttribute.setValueId(ownerId);
        ownerAttribute.setValueTypeId(ApartmentCardStrategy.OWNER_TYPE);

        //ownership form
        Attribute ownershipFormAttribute = c.getAttribute(ApartmentCardStrategy.FORM_OF_OWNERSHIP);
        ownershipFormAttribute.setValueId(ownershipFromId);
        ownershipFormAttribute.setValueTypeId(ApartmentCardStrategy.FORM_OF_OWNERSHIP_TYPE);

        //permissions
        c.setSubjectIds(Sets.newHashSet(jekId));

        //ID в файле импорта
        Utils.setSystemLocaleValue(c.getAttribute(ApartmentCardStrategy.OLD_SYSTEM_APARTMENT_CARD_ID), String.valueOf(apartmentCardId));

        return c;
    }
}
