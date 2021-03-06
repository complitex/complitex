package ru.complitex.pspoffice.api.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Anatoly A. Ivanov
 *         25.05.2017 16:55
 */
public class PersonModel implements Serializable {
    private Long id;

    private Map<String, String> lastName;
    private Map<String, String> firstName;
    private Map<String, String> middleName;

    private String identityCode;

    private Date birthDate;

    private String birthCountry;
    private String birthRegion;
    private String birthCity;
    private String birthDistrict;

    private Integer gender;

    private List<DocumentModel> documents;

    private Long citizenshipId;
    private Long militaryServiceRelationId;

    private List<PersonModel> children;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Map<String, String> getLastName() {
        return lastName;
    }

    public void setLastName(Map<String, String> lastName) {
        this.lastName = lastName;
    }

    public Map<String, String> getFirstName() {
        return firstName;
    }

    public void setFirstName(Map<String, String> firstName) {
        this.firstName = firstName;
    }

    public Map<String, String> getMiddleName() {
        return middleName;
    }

    public void setMiddleName(Map<String, String> middleName) {
        this.middleName = middleName;
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

    public List<DocumentModel> getDocuments() {
        return documents;
    }

    public void setDocuments(List<DocumentModel> documents) {
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

    public List<PersonModel> getChildren() {
        return children;
    }

    public void setChildren(List<PersonModel> children) {
        this.children = children;
    }
}
