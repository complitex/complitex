/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.report.entity;

import java.io.Serializable;
import static com.google.common.collect.Lists.*;
import java.util.List;

/**
 *
 * @author Artem
 */
public class FamilyAndApartmentInfo implements Serializable {

    private String addressEntity;
    private long addressId;
    private List<FamilyMember> familyMembers = newArrayList();
    private Integer rooms;
    private String roomsArea;
    private String kitchenArea;
    private String bathroomArea;
    private String toiletArea;
    private String hallArea;
    private String verandaArea;
    private String embeddedArea;
    private String balconyArea;
    private String loggiaArea;
    private String fullApartmentArea;
    private String storeroomArea;
    private String barnArea;
    private String anotherBuildingsInfo;
    private String additionalInformation;
    private Integer maintenanceYear;

    public String getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

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

    public String getAnotherBuildingsInfo() {
        return anotherBuildingsInfo;
    }

    public void setAnotherBuildingsInfo(String anotherBuildingsInfo) {
        this.anotherBuildingsInfo = anotherBuildingsInfo;
    }

    public String getBalconyArea() {
        return balconyArea;
    }

    public void setBalconyArea(String balconyArea) {
        this.balconyArea = balconyArea;
    }

    public String getBarnArea() {
        return barnArea;
    }

    public void setBarnArea(String barnArea) {
        this.barnArea = barnArea;
    }

    public String getBathroomArea() {
        return bathroomArea;
    }

    public void setBathroomArea(String bathroomArea) {
        this.bathroomArea = bathroomArea;
    }

    public String getEmbeddedArea() {
        return embeddedArea;
    }

    public void setEmbeddedArea(String embeddedArea) {
        this.embeddedArea = embeddedArea;
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

    public String getFullApartmentArea() {
        return fullApartmentArea;
    }

    public void setFullApartmentArea(String fullApartmentArea) {
        this.fullApartmentArea = fullApartmentArea;
    }

    public String getHallArea() {
        return hallArea;
    }

    public void setHallArea(String hallArea) {
        this.hallArea = hallArea;
    }

    public String getKitchenArea() {
        return kitchenArea;
    }

    public void setKitchenArea(String kitchenArea) {
        this.kitchenArea = kitchenArea;
    }

    public String getLoggiaArea() {
        return loggiaArea;
    }

    public void setLoggiaArea(String loggiaArea) {
        this.loggiaArea = loggiaArea;
    }

    public Integer getMaintenanceYear() {
        return maintenanceYear;
    }

    public void setMaintenanceYear(Integer maintenanceYear) {
        this.maintenanceYear = maintenanceYear;
    }

    public Integer getRooms() {
        return rooms;
    }

    public void setRooms(Integer rooms) {
        this.rooms = rooms;
    }

    public String getRoomsArea() {
        return roomsArea;
    }

    public void setRoomsArea(String roomsArea) {
        this.roomsArea = roomsArea;
    }

    public String getStoreroomArea() {
        return storeroomArea;
    }

    public void setStoreroomArea(String storeroomArea) {
        this.storeroomArea = storeroomArea;
    }

    public String getToiletArea() {
        return toiletArea;
    }

    public void setToiletArea(String toiletArea) {
        this.toiletArea = toiletArea;
    }

    public String getVerandaArea() {
        return verandaArea;
    }

    public void setVerandaArea(String verandaArea) {
        this.verandaArea = verandaArea;
    }
}
