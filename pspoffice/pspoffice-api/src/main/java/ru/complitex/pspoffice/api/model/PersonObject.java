package ru.complitex.pspoffice.api.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 *         25.05.2017 16:55
 */
public class PersonObject implements Serializable{
    private Long objectId;

    private List<Name> lastNames;
    private List<Name> firstNames;
    private List<Name> middleNames;

    private String identityCode;

    private Date birthDate;

    private String birthCountry;
    private String birthRegion;
    private String birthCity;
    private String birthDistrict;

    private Integer gender;

    private List<DocumentObject> documents;

    private Long citizenshipId;
    private Long militaryServiceRelationId;

    private List<PersonObject> children;

    public Long getObjectId() {
        return objectId;
    }

    public void setObjectId(Long objectId) {
        this.objectId = objectId;
    }

    public List<Name> getLastNames() {
        return lastNames;
    }

    public void setLastNames(List<Name> lastNames) {
        this.lastNames = lastNames;
    }

    public List<Name> getFirstNames() {
        return firstNames;
    }

    public void setFirstNames(List<Name> firstNames) {
        this.firstNames = firstNames;
    }

    public List<Name> getMiddleNames() {
        return middleNames;
    }

    public void setMiddleNames(List<Name> middleNames) {
        this.middleNames = middleNames;
    }

    public String getIdentityCode() {
        return identityCode;
    }

    public void setIdentityCode(String identityCode) {
        this.identityCode = identityCode;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
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

    public Long getCitizenshipId() {
        return citizenshipId;
    }

    public void setCitizenshipId(Long citizenshipId) {
        this.citizenshipId = citizenshipId;
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
