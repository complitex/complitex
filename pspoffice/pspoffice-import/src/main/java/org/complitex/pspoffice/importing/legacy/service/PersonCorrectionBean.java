/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.importing.legacy.service;

import com.google.common.collect.ImmutableMap;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.apache.wicket.util.string.Strings;
import org.complitex.dictionary.converter.BooleanConverter;
import org.complitex.dictionary.converter.DateConverter;
import org.complitex.dictionary.converter.GenderConverter;
import org.complitex.dictionary.entity.Attribute;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.entity.Gender;
import org.complitex.dictionary.service.AbstractBean;
import org.complitex.dictionary.util.CloneUtil;
import org.complitex.dictionary.util.DateUtil;
import org.complitex.dictionary.util.StringUtil;
import org.complitex.pspoffice.document.strategy.DocumentStrategy;
import org.complitex.pspoffice.document.strategy.entity.Document;
import org.complitex.pspoffice.importing.legacy.entity.PersonCorrection;
import org.complitex.pspoffice.importing.legacy.service.exception.TooManyResultsException;
import org.complitex.pspoffice.person.strategy.PersonStrategy;
import org.complitex.pspoffice.person.strategy.entity.Person;
import org.complitex.pspoffice.person.strategy.entity.PersonName.PersonNameType;
import org.complitex.pspoffice.person.strategy.service.PersonNameBean;

/**
 *
 * @author Artem
 */
@Stateless
public class PersonCorrectionBean extends AbstractBean {

    private static final String MAPPING_NAMESPACE = PersonCorrectionBean.class.getName();
    /**
     * Gender consts
     */
    private static final String MALE = "Ч";
    private static final String FEMALE = "Ж";
    /**
     * Ukraine citizenship const
     */
    private static final String UKRAINE_CITIZENSHIP_INDICATOR = "УКРАЇНА";
    @EJB
    private PersonNameBean personNameBean;
    @EJB
    private PersonStrategy personStrategy;
    @EJB
    private DocumentStrategy documentStrategy;

    public void insert(PersonCorrection personCorrection) {
        sqlSession().insert(MAPPING_NAMESPACE + ".insert", personCorrection);
    }

    public boolean exists() {
        return (Integer) sqlSession().selectOne(MAPPING_NAMESPACE + ".exists") > 0;
    }

    public void cleanData() {
        sqlSession().delete(MAPPING_NAMESPACE + ".delete");
    }

    public void update(PersonCorrection personCorrection) {
        sqlSession().update(MAPPING_NAMESPACE + ".update", personCorrection);
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

    public List<PersonCorrection> findForProcessing(int size) {
        return sqlSession().selectList(MAPPING_NAMESPACE + ".findForProcessing",
                ImmutableMap.of("size", size, "NONARCHIVE_INDICATOR", Utils.NONARCHIVE_INDICATOR));
    }

    public Person findSystemPerson(PersonCorrection p) throws TooManyResultsException {
        List<Long> ids = sqlSession().selectList(MAPPING_NAMESPACE + ".findSystemPerson",
                ImmutableMap.builder().
                put("personIdAT", PersonStrategy.OLD_SYSTEM_PERSON_ID).
                put("localeId", Utils.UKRAINIAN_LOCALE_ID).
                put("lastName", p.getFam()).put("firstName", p.getIm()).put("middleName", p.getOt()).
                put("personId", p.getId()).
                build());
        if (ids.size() == 1) {
            return personStrategy.findById(ids.get(0), true, false, false, false, false);
        } else if (ids.isEmpty()) {
            return null;
        } else {
            throw new TooManyResultsException();
        }
    }

    public Person newSystemPerson(PersonCorrection pc, Date birthDate, Long documentTypeId, Long systemMilitaryServiceRelationId) {
        Person p = personStrategy.newInstance();

        //ФИО
        setName(PersonNameType.LAST_NAME, PersonStrategy.LAST_NAME, p, pc.getFam());
        setName(PersonNameType.FIRST_NAME, PersonStrategy.FIRST_NAME, p, pc.getIm());
        setName(PersonNameType.MIDDLE_NAME, PersonStrategy.MIDDLE_NAME, p, pc.getOt());

        //Дата рождения
        Utils.setSystemLocaleValue(p.getAttribute(PersonStrategy.BIRTH_DATE), new DateConverter().toString(birthDate));

        //Пол
        Gender gender = null;
        if (MALE.equalsIgnoreCase(pc.getPol())) {
            gender = Gender.MALE;
        } else if (FEMALE.equalsIgnoreCase(pc.getPol())) {
            gender = Gender.FEMALE;
        } else {
            gender = Gender.MALE;
        }
        Utils.setSystemLocaleValue(p.getAttribute(PersonStrategy.GENDER), new GenderConverter().toString(gender));

        //Гражданство Украины
        final boolean isUkraineCitizenship = Strings.isEmpty(pc.getGrajd())
                || UKRAINE_CITIZENSHIP_INDICATOR.equalsIgnoreCase(pc.getGrajd());
        Utils.setSystemLocaleValue(p.getAttribute(PersonStrategy.UKRAINE_CITIZENSHIP),
                new BooleanConverter().toString(isUkraineCitizenship));

        //Место рождения
        Utils.setSystemLocaleValue(p.getAttribute(PersonStrategy.BIRTH_COUNTRY), pc.getNkra());
        Utils.setSystemLocaleValue(p.getAttribute(PersonStrategy.BIRTH_REGION), pc.getNobl());
        Utils.setSystemLocaleValue(p.getAttribute(PersonStrategy.BIRTH_DISTRICT), pc.getNrayon());
        Utils.setSystemLocaleValue(p.getAttribute(PersonStrategy.BIRTH_CITY), pc.getNmisto());

        //Документ
        Document d = documentStrategy.newInstance(documentTypeId);
        Utils.setSystemLocaleValue(d.getAttribute(DocumentStrategy.DOCUMENT_SERIA), pc.getDokseria());
        Utils.setSystemLocaleValue(d.getAttribute(DocumentStrategy.DOCUMENT_NUMBER), pc.getDoknom());
        Utils.setSystemLocaleValue(d.getAttribute(DocumentStrategy.ORGANIZATION_ISSUED), pc.getDokvidan());

        /* Set document date issued only if that date is later person's birth date. */
        final Date dateIssued = DateUtil.asDate(pc.getDokdatvid(), Utils.DATE_PATTERN);
        if (dateIssued != null && birthDate.before(dateIssued)) {
            Utils.setSystemLocaleValue(d.getAttribute(DocumentStrategy.DATE_ISSUED), pc.getDokdatvid());
        }
        p.setDocument(d);

        //отношение к воиской обязанности
        if (systemMilitaryServiceRelationId != null) {
            p.getAttribute(PersonStrategy.MILITARY_SERVICE_RELATION).setValueId(systemMilitaryServiceRelationId);
        }

        //ID в файле импорта
        Utils.setSystemLocaleValue(p.getAttribute(PersonStrategy.OLD_SYSTEM_PERSON_ID), String.valueOf(pc.getId()));

        return p;
    }

    private void setName(PersonNameType personNameType, long personNameAttributeTypeId, DomainObject person, String name) {
        for (Attribute nameAttribute : person.getAttributes(personNameAttributeTypeId)) {
            nameAttribute.setValueId(personNameBean.saveIfNotExists(personNameType, name,
                    nameAttribute.getAttributeId()).getId());
        }
    }

    public static boolean isBirthDateValid(String birthDate) {
        return DateUtil.asDate(birthDate, Utils.DATE_PATTERN) != null;
    }

    public static boolean isSupportedDocumentType(String documentType) {
        return String.valueOf(ReferenceDataCorrectionBean.PASSPORT).equalsIgnoreCase(documentType)
                || String.valueOf(ReferenceDataCorrectionBean.BIRTH_CERTIFICATE).equalsIgnoreCase(documentType);
    }

    public static boolean isDocumentDataValid(String documentType, String seria, String number) {
        return !Strings.isEmpty(documentType) && !Strings.isEmpty(seria) && !Strings.isEmpty(number);
    }

    public static boolean isParentDataValid(String idbud, String kv, String parentnom) {
        return !Strings.isEmpty(idbud) && !Strings.isEmpty(kv) && parentnom != null && StringUtil.isNumeric(parentnom);
    }

    public void addChild(long personId, long childId, String birthDate, Date updateDate) {
        if (DateUtil.isValidDateInterval(updateDate, DateUtil.asDate(birthDate, Utils.DATE_PATTERN),
                PersonStrategy.AGE_THRESHOLD)) {
            return;
        }

        Person person = personStrategy.findById(personId, true, false, false, false, false);
        Person newPerson = CloneUtil.cloneObject(person);

        List<Attribute> childrenAttributes = newPerson.getAttributes(PersonStrategy.CHILDREN);
        for (Attribute childAttribute : childrenAttributes) {
            if (childAttribute.getValueId().equals(childId)) {
                return;
            }
        }
        newPerson.addAttribute(newChildAttribute(childrenAttributes.size() + 1, childId));
        personStrategy.update(person, newPerson, updateDate);
    }

    private Attribute newChildAttribute(long attributeId, long childId) {
        Attribute childAttribute = new Attribute();
        childAttribute.setAttributeId(attributeId);
        childAttribute.setAttributeTypeId(PersonStrategy.CHILDREN);
        childAttribute.setValueTypeId(PersonStrategy.CHILDREN);
        childAttribute.setValueId(childId);
        return childAttribute;
    }

    public List<Long> findSystemParent(String idbud, String kv, String nom) {
        return sqlSession().selectList(MAPPING_NAMESPACE + ".findSystemParent",
                ImmutableMap.of("NONARCHIVE_INDICATOR", Utils.NONARCHIVE_INDICATOR,
                "idbud", idbud, "kv", kv, "nom", nom));
    }

    public List<PersonCorrection> findChildren(int size) {
        return sqlSession().selectList(MAPPING_NAMESPACE + ".findChildren",
                ImmutableMap.of("size", size, "NONARCHIVE_INDICATOR", Utils.NONARCHIVE_INDICATOR));
    }

    public PersonCorrection getOwner(String buildingId, String apartment, String ownerType, String fio) {
        PersonCorrection correction = findOwnerByType(buildingId, apartment, ownerType);
        if (correction == null && !Strings.isEmpty(fio)) {
            correction = findOwnerByName(buildingId, apartment, prepareFio(fio));
        }
        return correction;
    }

    private static String prepareFio(String rawFio) {
        return rawFio.replaceAll("( )+", " ").trim();
    }

    private PersonCorrection findOwnerByName(String buildingId, String apartment, String fio) {
        List<PersonCorrection> corrections = sqlSession().selectList(MAPPING_NAMESPACE + ".getOwnerByName",
                ImmutableMap.of("buildingId", buildingId, "apartment", apartment,
                "fio", fio, "NONARCHIVE_INDICATOR", Utils.NONARCHIVE_INDICATOR));
        if (corrections.size() == 1) {
            return corrections.get(0);
        } else {
            return null;
        }
    }

    private PersonCorrection findOwnerByType(String buildingId, String apartment, String ownerType) {
        List<PersonCorrection> corrections = sqlSession().selectList(MAPPING_NAMESPACE + ".getOwnerByType",
                ImmutableMap.of("buildingId", buildingId, "apartment", apartment,
                "OWNER_TYPE", ownerType, "NONARCHIVE_INDICATOR", Utils.NONARCHIVE_INDICATOR));
        if (corrections.size() == 1) {
            return corrections.get(0);
        } else {
            return null;
        }
    }

    public List<PersonCorrection> findByAddress(String buildingId, String apartment) {
        return sqlSession().selectList(MAPPING_NAMESPACE + ".findPersonByAddress",
                ImmutableMap.of("buildingId", buildingId, "apartment", apartment));
    }
}
