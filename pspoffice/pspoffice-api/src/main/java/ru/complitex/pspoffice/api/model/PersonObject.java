package ru.complitex.pspoffice.api.model;

import java.io.Serializable;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 *         25.05.2017 16:55
 */
public class PersonObject implements Serializable{
    private Long objectId;

    private List<Name> lastName;
    private List<Name> firstName;
    private List<Name> middleName;

    private String identityCode;

    private String birthDate;

    private String birthCountry;
    private String birthRegion;
    private String birthCity;
    private String birthDistrict;

    private Integer gender;

    private List<DocumentObject> documents;

    private Integer ukraineCitizenship;
    private Long militaryServiceRelationId;

    private List<PersonObject> children;

    public Long getObjectId() {
        return objectId;
    }

    public void setObjectId(Long objectId) {
        this.objectId = objectId;
    }

    public List<Name> getLastName() {
        return lastName;
    }

    public void setLastName(List<Name> lastName) {
        this.lastName = lastName;
    }

    public List<Name> getFirstName() {
        return firstName;
    }

    public void setFirstName(List<Name> firstName) {
        this.firstName = firstName;
    }

    public List<Name> getMiddleName() {
        return middleName;
    }

    public void setMiddleName(List<Name> middleName) {
        this.middleName = middleName;
    }

    public String getIdentityCode() {
        return identityCode;
    }

    public void setIdentityCode(String identityCode) {
        this.identityCode = identityCode;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getBirthCountry() {
        return birthCountry;
    }

    public void setBirthCountry(String birthCountry) {
        this.birthCountry = birthCountry;
    }

    public String getBirthRegion() {
        return birthRegion;
    }

    public void setBirthRegion(String birthRegion) {
        this.birthRegion = birthRegion;
    }

    public String getBirthCity() {
        return birthCity;
    }

    public void setBirthCity(String birthCity) {
        this.birthCity = birthCity;
    }

    public String getBirthDistrict() {
        return birthDistrict;
    }

    public void setBirthDistrict(String birthDistrict) {
        this.birthDistrict = birthDistrict;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public List<DocumentObject> getDocuments() {
        return documents;
    }

    public void setDocuments(List<DocumentObject> documents) {
        this.documents = documents;
    }

    public Integer getUkraineCitizenship() {
        return ukraineCitizenship;
    }

    public void setUkraineCitizenship(Integer ukraineCitizenship) {
        this.ukraineCitizenship = ukraineCitizenship;
    }

    public Long getMilitaryServiceRelationId() {
        return militaryServiceRelationId;
    }

    public void setMilitaryServiceRelationId(Long militaryServiceRelationId) {
        this.militaryServiceRelationId = militaryServiceRelationId;
    }

    public List<PersonObject> getChildren() {
        return children;
    }

    public void setChildren(List<PersonObject> children) {
        this.children = children;
    }
}
