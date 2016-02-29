package org.complitex.osznconnection.file.entity;

import java.io.Serializable;

/**
 * Запись в локальной таблице номеров л/c person_account
 */
public class PersonAccount implements Serializable {
    private Long id;

    private String firstName;
    private String middleName;
    private String lastName;

    private String city;
    private String streetType;
    private String street;
    private String buildingNumber;
    private String buildingCorp;
    private String apartment;

    private Long cityId;
    private Long streetTypeId;
    private Long streetId;
    private Long buildingId;
    private Long apartmentId;

    private Long organizationId;
    private Long userOrganizationId;
    private Long calculationCenterId;

    private String accountNumber;
    private String puAccountNumber;

    private String organizationName;
    private String userOrganizationName;
    private String calculationCenterName;

    public PersonAccount() {
    }

    public PersonAccount(AbstractAccountRequest request, String puAccountNumber, Long calculationCenterId, boolean addressNames) {
        firstName = request.getFirstName();
        middleName = request.getMiddleName();
        lastName = request.getLastName();

        if (addressNames) {
            city = request.getCity();
            streetType = request.getStreetType();
            street = request.getStreet();
            buildingNumber = request.getBuildingNumber();
            buildingCorp = request.getBuildingCorp();
        }

        apartment = request.getApartment();

        cityId = request.getCityId();
        streetId = request.getStreetId();
        streetTypeId = request.getStreetTypeId();
        buildingId = request.getBuildingId();
        apartmentId = request.getApartmentId();

        accountNumber = request.getAccountNumber();
        organizationId = request.getOrganizationId();
        userOrganizationId = request.getUserOrganizationId();

        this.puAccountNumber = puAccountNumber;
        this.calculationCenterId = calculationCenterId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreetType() {
        return streetType;
    }

    public void setStreetType(String streetType) {
        this.streetType = streetType;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getBuildingNumber() {
        return buildingNumber;
    }

    public void setBuildingNumber(String buildingNumber) {
        this.buildingNumber = buildingNumber;
    }

    public String getBuildingCorp() {
        return buildingCorp;
    }

    public void setBuildingCorp(String buildingCorp) {
        this.buildingCorp = buildingCorp;
    }

    public Long getCityId() {
        return cityId;
    }

    public void setCityId(Long cityId) {
        this.cityId = cityId;
    }

    public Long getStreetId() {
        return streetId;
    }

    public void setStreetId(Long streetId) {
        this.streetId = streetId;
    }

    public Long getStreetTypeId() {
        return streetTypeId;
    }

    public void setStreetTypeId(Long streetTypeId) {
        this.streetTypeId = streetTypeId;
    }

    public Long getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(Long buildingId) {
        this.buildingId = buildingId;
    }

    public Long getApartmentId() {
        return apartmentId;
    }

    public void setApartmentId(Long apartmentId) {
        this.apartmentId = apartmentId;
    }

    public String getApartment() {
        return apartment;
    }

    public void setApartment(String apartment) {
        this.apartment = apartment;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Long getUserOrganizationId() {
        return userOrganizationId;
    }

    public void setUserOrganizationId(Long userOrganizationId) {
        this.userOrganizationId = userOrganizationId;
    }

    public Long getCalculationCenterId() {
        return calculationCenterId;
    }

    public void setCalculationCenterId(Long calculationCenterId) {
        this.calculationCenterId = calculationCenterId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getPuAccountNumber() {
        return puAccountNumber;
    }

    public void setPuAccountNumber(String puAccountNumber) {
        this.puAccountNumber = puAccountNumber;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getUserOrganizationName() {
        return userOrganizationName;
    }

    public void setUserOrganizationName(String userOrganizationName) {
        this.userOrganizationName = userOrganizationName;
    }

    public String getCalculationCenterName() {
        return calculationCenterName;
    }

    public void setCalculationCenterName(String calculationCenterName) {
        this.calculationCenterName = calculationCenterName;
    }
}
