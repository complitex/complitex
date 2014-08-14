/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.report.entity;

import static com.google.common.collect.Lists.*;
import java.io.Serializable;
import java.util.List;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.pspoffice.person.strategy.entity.Person;

/**
 *
 * @author Artem
 */
public class HousingPayments implements Serializable {

    private Person owner;
    private String personalAccount;
    private long addressId;
    private String addressEntity;
    private DomainObject ownershipForm;
    private String floors;
    private String lift;
    private String hostel;
    private Integer floor;
    private String stoveType;
    private String apartmentArea;
    private String balconyArea;
    private Integer benefitPersons;
    private String normativeArea;
    private Integer rooms;
    private String benefits;
    private String paymentsAdjustedForBenefits;
    private String normativePayments;
    private String apartmentPayments;
    private String apartmentTariff;
    private String heatPayments;
    private String heatTariff;
    private String gasPayments;
    private String gasTariff;
    private String coldWaterPayments;
    private String coldWaterTariff;
    private String hotWaterPayments;
    private String hotWaterTariff;
    private String outletPayments;
    private String outletTariff;
    private String debt;
    private Integer debtMonth;
    private List<FamilyMember> familyMembers = newArrayList();

    public String getAddressEntity() {
        return addressEntity;
    }

    public void setAddressEntity(String addressEntity) {
        this.addressEntity = addressEntity;
    }

    public long getAddressId() {
        return addressId;
    }

    public void setAddressId(long addressId) {
        this.addressId = addressId;
    }

    public Person getOwner() {
        return owner;
    }

    public void setOwner(Person owner) {
        this.owner = owner;
    }

    public DomainObject getOwnershipForm() {
        return ownershipForm;
    }

    public void setOwnershipForm(DomainObject ownershipForm) {
        this.ownershipForm = ownershipForm;
    }

    public String getApartmentArea() {
        return apartmentArea;
    }

    public void setApartmentArea(String apartmentArea) {
        this.apartmentArea = apartmentArea;
    }

    public String getApartmentPayments() {
        return apartmentPayments;
    }

    public void setApartmentPayments(String apartmentPayments) {
        this.apartmentPayments = apartmentPayments;
    }

    public String getApartmentTariff() {
        return apartmentTariff;
    }

    public void setApartmentTariff(String apartmentTariff) {
        this.apartmentTariff = apartmentTariff;
    }

    public String getBalconyArea() {
        return balconyArea;
    }

    public void setBalconyArea(String balconyArea) {
        this.balconyArea = balconyArea;
    }

    public Integer getBenefitPersons() {
        return benefitPersons;
    }

    public void setBenefitPersons(Integer benefitPersons) {
        this.benefitPersons = benefitPersons;
    }

    public String getBenefits() {
        return benefits;
    }

    public void setBenefits(String benefits) {
        this.benefits = benefits;
    }

    public String getColdWaterPayments() {
        return coldWaterPayments;
    }

    public void setColdWaterPayments(String coldWaterPayments) {
        this.coldWaterPayments = coldWaterPayments;
    }

    public String getColdWaterTariff() {
        return coldWaterTariff;
    }

    public void setColdWaterTariff(String coldWaterTariff) {
        this.coldWaterTariff = coldWaterTariff;
    }

    public String getDebt() {
        return debt;
    }

    public void setDebt(String debt) {
        this.debt = debt;
    }

    public Integer getDebtMonth() {
        return debtMonth;
    }

    public void setDebtMonth(Integer debtMonth) {
        this.debtMonth = debtMonth;
    }

    public List<FamilyMember> getFamilyMembers() {
        return familyMembers;
    }

    public void setFamilyMembers(List<FamilyMember> familyMembers) {
        this.familyMembers = familyMembers;
    }

    public void addFamilyMember(FamilyMember familyMember) {
        familyMembers.add(familyMember);
    }

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    public String getFloors() {
        return floors;
    }

    public void setFloors(String floors) {
        this.floors = floors;
    }

    public String getGasPayments() {
        return gasPayments;
    }

    public void setGasPayments(String gasPayments) {
        this.gasPayments = gasPayments;
    }

    public String getGasTariff() {
        return gasTariff;
    }

    public void setGasTariff(String gasTariff) {
        this.gasTariff = gasTariff;
    }

    public String getHeatPayments() {
        return heatPayments;
    }

    public void setHeatPayments(String heatPayments) {
        this.heatPayments = heatPayments;
    }

    public String getHeatTariff() {
        return heatTariff;
    }

    public void setHeatTariff(String heatTariff) {
        this.heatTariff = heatTariff;
    }

    public String getHostel() {
        return hostel;
    }

    public void setHostel(String hostel) {
        this.hostel = hostel;
    }

    public String getHotWaterPayments() {
        return hotWaterPayments;
    }

    public void setHotWaterPayments(String hotWaterPayments) {
        this.hotWaterPayments = hotWaterPayments;
    }

    public String getHotWaterTariff() {
        return hotWaterTariff;
    }

    public void setHotWaterTariff(String hotWaterTariff) {
        this.hotWaterTariff = hotWaterTariff;
    }

    public String getLift() {
        return lift;
    }

    public void setLift(String lift) {
        this.lift = lift;
    }

    public String getNormativeArea() {
        return normativeArea;
    }

    public void setNormativeArea(String normativeArea) {
        this.normativeArea = normativeArea;
    }

    public String getNormativePayments() {
        return normativePayments;
    }

    public void setNormativePayments(String normativePayments) {
        this.normativePayments = normativePayments;
    }

    public String getOutletPayments() {
        return outletPayments;
    }

    public void setOutletPayments(String outletPayments) {
        this.outletPayments = outletPayments;
    }

    public String getOutletTariff() {
        return outletTariff;
    }

    public void setOutletTariff(String outletTariff) {
        this.outletTariff = outletTariff;
    }

    public String getPaymentsAdjustedForBenefits() {
        return paymentsAdjustedForBenefits;
    }

    public void setPaymentsAdjustedForBenefits(String paymentsAdjustedForBenefits) {
        this.paymentsAdjustedForBenefits = paymentsAdjustedForBenefits;
    }

    public String getPersonalAccount() {
        return personalAccount;
    }

    public void setPersonalAccount(String personalAccount) {
        this.personalAccount = personalAccount;
    }

    public Integer getRooms() {
        return rooms;
    }

    public void setRooms(Integer rooms) {
        this.rooms = rooms;
    }

    public String getStoveType() {
        return stoveType;
    }

    public void setStoveType(String stoveType) {
        this.stoveType = stoveType;
    }
}
