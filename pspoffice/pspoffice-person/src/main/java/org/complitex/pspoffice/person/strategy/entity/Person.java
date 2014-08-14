/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.entity;

import static com.google.common.collect.Maps.*;
import java.util.Date;
import static com.google.common.collect.Lists.*;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.wicket.util.string.Strings;
import org.complitex.dictionary.converter.GenderConverter;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.entity.Gender;
import org.complitex.dictionary.entity.StatusType;
import static org.complitex.dictionary.util.DateUtil.*;
import org.complitex.pspoffice.document.strategy.entity.Document;
import static org.complitex.dictionary.util.AttributeUtil.*;
import static org.complitex.pspoffice.person.strategy.PersonStrategy.*;

/**
 *
 * @author Artem
 */
public class Person extends DomainObject {

    private Map<Locale, String> firstNames = newHashMap();
    private Map<Locale, String> lastNames = newHashMap();
    private Map<Locale, String> middleNames = newHashMap();
    private List<Person> children = newArrayList();
    private Document document;
    private Document replacedDocument;
    private DomainObject militaryServiceRelation;

    public Person(DomainObject copy) {
        super(copy);
    }

    public Person() {
    }

    public DomainObject getMilitaryServiceRelation() {
        return militaryServiceRelation;
    }

    public void setMilitaryServiceRelation(DomainObject militaryServiceRelation) {
        this.militaryServiceRelation = militaryServiceRelation;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public Document getReplacedDocument() {
        return replacedDocument;
    }

    public void setReplacedDocument(Document replacedDocument) {
        this.replacedDocument = replacedDocument;
    }

    public List<Person> getChildren() {
        return children;
    }

    public void setChildren(List<Person> children) {
        this.children = children;
    }

    public void addChild(Person child) {
        children.add(child);
    }

    public void setChild(int index, Person child) {
        children.set(index, child);
    }

    public String getIdentityCode() {
        return getStringValue(this, IDENTITY_CODE);
    }

    public Date getBirthDate() {
        return getDateValue(this, BIRTH_DATE);
    }

    public String getBirthCountry() {
        return getStringValue(this, BIRTH_COUNTRY);
    }

    public String getBirthRegion() {
        return getStringValue(this, BIRTH_REGION);
    }

    public String getBirthDistrict() {
        return getStringValue(this, BIRTH_DISTRICT);
    }

    public String getBirthCity() {
        return getStringValue(this, BIRTH_CITY);
    }

    public Date getDeathDate() {
        return getDateValue(this, DEATH_DATE);
    }

    public Gender getGender() {
        return getAttributeValue(this, GENDER, new GenderConverter());
    }

    public boolean isUkraineCitizen() {
        return getBooleanValue(this, UKRAINE_CITIZENSHIP);
    }

    public String getFirstName(Locale locale, Locale systemLocale) {
        String name = firstNames.get(locale);
        return !Strings.isEmpty(name) ? name : firstNames.get(systemLocale);
    }

    public String getLastName(Locale locale, Locale systemLocale) {
        String name = lastNames.get(locale);
        return !Strings.isEmpty(name) ? name : lastNames.get(systemLocale);
    }

    public String getMiddleName(Locale locale, Locale systemLocale) {
        String name = middleNames.get(locale);
        return !Strings.isEmpty(name) ? name : middleNames.get(systemLocale);
    }

    public void addFirstName(Locale locale, String name) {
        firstNames.put(locale, name);
    }

    public void addLastName(Locale locale, String name) {
        lastNames.put(locale, name);
    }

    public void addMiddleName(Locale locale, String name) {
        middleNames.put(locale, name);
    }

    public boolean isKid() {
        Date birthDate = getBirthDate();
        Date currentDate = getCurrentDate();
        return birthDate != null && !isValidDateInterval(currentDate, birthDate, AGE_THRESHOLD);
    }

    public boolean hasChildren() {
        return children.size() > 0;
    }

    public boolean isDead() {
        return getStatus() == StatusType.INACTIVE && getDeathDate() != null;
    }

    public long getEditedByUserId() {
        return getIntegerValue(this, EDITED_BY_USER_ID);
    }

    public String getExplanation() {
        return getStringValue(this, EXPLANATION);
    }
}
