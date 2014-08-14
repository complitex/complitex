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
public class F3Reference implements Serializable {

    private Person person;
    private long addressId;
    private String addressEntity;
    private String livingArea;
    private String apartmentArea;
    private Integer takesRooms;
    private Integer rooms;
    private Integer floor;
    private Integer floors;
    private Person personalAccountOwner;
    private DomainObject ownershipForm;
    private String facilities;
    private String technicalState;
    private List<FamilyMember> familyMembers = newArrayList();
    private List<NeighbourFamily> neighbourFamilies = newArrayList();

    public String getApartmentArea() {
        return apartmentArea;
    }

    public void setApartmentArea(String apartmentArea) {
        this.apartmentArea = apartmentArea;
    }

    public String getFacilities() {
        return facilities;
    }

    public void setFacilities(String facilities) {
        this.facilities = facilities;
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

    public Integer getFloors() {
        return floors;
    }

    public void setFloors(Integer floors) {
        this.floors = floors;
    }

    public String getLivingArea() {
        return livingArea;
    }

    public void setLivingArea(String livingArea) {
        this.livingArea = livingArea;
    }

    public List<NeighbourFamily> getNeighbourFamilies() {
        return neighbourFamilies;
    }

    public void setNeighbourFamilies(List<NeighbourFamily> neighbourFamilies) {
        this.neighbourFamilies = neighbourFamilies;
    }

    public void addNeighbourFamily(NeighbourFamily neighbourFamily) {
        neighbourFamilies.add(neighbourFamily);
    }

    public Integer getRooms() {
        return rooms;
    }

    public void setRooms(Integer rooms) {
        this.rooms = rooms;
    }

    public Integer getTakesRooms() {
        return takesRooms;
    }

    public void setTakesRooms(Integer takesRooms) {
        this.takesRooms = takesRooms;
    }

    public String getTechnicalState() {
        return technicalState;
    }

    public void setTechnicalState(String technicalState) {
        this.technicalState = technicalState;
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

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Person getPersonalAccountOwner() {
        return personalAccountOwner;
    }

    public void setPersonalAccountOwner(Person personalAccountOwner) {
        this.personalAccountOwner = personalAccountOwner;
    }

    public DomainObject getOwnershipForm() {
        return ownershipForm;
    }

    public void setOwnershipForm(DomainObject ownershipForm) {
        this.ownershipForm = ownershipForm;
    }
}
