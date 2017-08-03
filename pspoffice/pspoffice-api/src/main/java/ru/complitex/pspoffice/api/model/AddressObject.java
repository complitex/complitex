package ru.complitex.pspoffice.api.model;

import io.swagger.annotations.ApiModel;

import java.io.Serializable;
import java.util.Map;

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

    private Map<String, String> name;
    private Map<String, String> shortName;

    //date, history

    public AddressObject() {
    }

    public AddressObject(Long objectId, Long parentId) {
        this.objectId = objectId;
        this.parentId = parentId;
    }

    public AddressObject(Long objectId, Map<String, String> name) {
        this.objectId = objectId;
        this.name = name;
    }

    public AddressObject(Long objectId, Map<String, String> name, Map<String, String> shortName) {
        this.objectId = objectId;
        this.name = name;
        this.shortName = shortName;
    }

    public AddressObject(Long objectId, Long parentId, String code, Map<String, String> name) {
        this.objectId = objectId;
        this.parentId = parentId;
        this.code = code;
        this.name = name;
    }

    public AddressObject(Long objectId, Long parentId, Long typeId, Map<String, String> name) {
        this.objectId = objectId;
        this.parentId = parentId;
        this.typeId = typeId;
        this.name = name;
    }

    public AddressObject(Long objectId, Long parentId, Long typeId, String code, Map<String, String> names) {
        this.objectId = objectId;
        this.parentId = parentId;
        this.typeId = typeId;
        this.code = code;
        this.name = name;
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

    public Map<String, String> getName() {
        return name;
    }

    public void setName(Map<String, String> name) {
        this.name = name;
    }

    public Map<String, String> getShortName() {
        return shortName;
    }

    public void setShortName(Map<String, String> shortName) {
        this.shortName = shortName;
    }
}
