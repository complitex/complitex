/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.report.entity;

import java.io.Serializable;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.pspoffice.person.strategy.entity.Person;

/**
 *
 * @author Artem
 */
public class NeighbourFamily implements Serializable {

    private Person person;
    private Integer amount;
    private Integer takeRooms;
    private String takeArea;
    private DomainObject apartment;
    private String roomsAndAreaInfo;
    private String otherBuildingsAndAreaInfo;
    private String loggiaAndAreaInfo;

    public DomainObject getApartment() {
        return apartment;
    }

    public void setApartment(DomainObject apartment) {
        this.apartment = apartment;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public String getTakeArea() {
        return takeArea;
    }

    public void setTakeArea(String takeArea) {
        this.takeArea = takeArea;
    }

    public Integer getTakeRooms() {
        return takeRooms;
    }

    public void setTakeRooms(Integer takeRooms) {
        this.takeRooms = takeRooms;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getRoomsAndAreaInfo() {
        return roomsAndAreaInfo;
    }

    public void setRoomsAndAreaInfo(String roomsAndAreaInfo) {
        this.roomsAndAreaInfo = roomsAndAreaInfo;
    }

    public String getLoggiaAndAreaInfo() {
        return loggiaAndAreaInfo;
    }

    public void setLoggiaAndAreaInfo(String loggiaAndAreaInfo) {
        this.loggiaAndAreaInfo = loggiaAndAreaInfo;
    }

    public String getOtherBuildingsAndAreaInfo() {
        return otherBuildingsAndAreaInfo;
    }

    public void setOtherBuildingsAndAreaInfo(String otherBuildingsAndAreaInfo) {
        this.otherBuildingsAndAreaInfo = otherBuildingsAndAreaInfo;
    }
}
