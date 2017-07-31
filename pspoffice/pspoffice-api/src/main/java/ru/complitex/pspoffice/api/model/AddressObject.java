package ru.complitex.pspoffice.api.model;

import io.swagger.annotations.ApiModel;

import java.io.Serializable;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 *         04.05.2017 17:59
 */
@ApiModel("Address object")
public class AddressObject implements Serializable{
    private Long objectId;
    private Long parentId;
    private Long typeId;

    private String code;

    private List<Name> names;
    private List<Name> shortNames;

    //date, history

    public AddressObject() {
    }

    public AddressObject(Long objectId, Long parentId) {
        this.objectId = objectId;
        this.parentId = parentId;
    }

    public AddressObject(Long objectId, List<Name> names) {
        this.objectId = objectId;
        this.names = names;
    }

    public AddressObject(Long objectId, List<Name> names, List<Name> shortNames) {
        this.objectId = objectId;
        this.names = names;
        this.shortNames = shortNames;
    }

    public AddressObject(Long objectId, Long parentId, String code, List<Name> names) {
        this.objectId = objectId;
        this.parentId = parentId;
        this.code = code;
        this.names = names;
    }

    public AddressObject(Long objectId, Long parentId, Long typeId, List<Name> names) {
        this.objectId = objectId;
        this.parentId = parentId;
        this.typeId = typeId;
        this.names = names;
    }

    public AddressObject(Long objectId, Long parentId, Long typeId, String code, List<Name> names) {
        this.objectId = objectId;
        this.parentId = parentId;
        this.typeId = typeId;
        this.code = code;
        this.names = names;
    }

    public Long getObjectId() {
        return objectId;
    }

    public void setObjectId(Long objectId) {
        this.objectId = objectId;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<Name> getNames() {
        return names;
    }

    public void setNames(List<Name> names) {
        this.names = names;
    }

    public List<Name> getShortNames() {
        return shortNames;
    }

    public void setShortNames(List<Name> shortNames) {
        this.shortNames = shortNames;
    }
}
