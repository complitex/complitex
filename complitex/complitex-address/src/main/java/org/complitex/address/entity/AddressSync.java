package org.complitex.address.entity;

import com.google.common.base.Objects;
import org.complitex.common.entity.ILongId;

import java.util.Date;

import static org.complitex.address.entity.AddressEntity.BUILDING;

/**
 * @author Anatoly Ivanov
 *         Date: 29.07.2014 22:35
 */
public class AddressSync implements ILongId {
    private Long id;
    private Long objectId;
    private Long parentId;
    private Long additionalParentId;
    private String externalId;
    private String additionalExternalId;
    private String name;
    private String additionalName;
    private String servicingOrganization;
    private String balanceHolder;
    private AddressEntity type;
    private AddressSyncStatus status;
    private Date date;

    public String getUniqueExternalId(){
        return BUILDING.equals(type) ? additionalExternalId + '.' + externalId  : externalId;
    }

    public void setUniqueExternalId(String uniqueExternalId){
        if (BUILDING.equals(type)){
            String[] ids = uniqueExternalId.split("\\.");
            additionalExternalId = ids[0];
            externalId = ids[1];
        }else{
            externalId = uniqueExternalId;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getAdditionalParentId() {
        return additionalParentId;
    }

    public void setAdditionalParentId(Long additionalParentId) {
        this.additionalParentId = additionalParentId;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getAdditionalExternalId() {
        return additionalExternalId;
    }

    public void setAdditionalExternalId(String additionalExternalId) {
        this.additionalExternalId = additionalExternalId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAdditionalName() {
        return additionalName;
    }

    public void setAdditionalName(String additionalName) {
        this.additionalName = additionalName;
    }

    public String getServicingOrganization() {
        return servicingOrganization;
    }

    public void setServicingOrganization(String servicingOrganization) {
        this.servicingOrganization = servicingOrganization;
    }

    public String getBalanceHolder() {
        return balanceHolder;
    }

    public void setBalanceHolder(String balanceHolder) {
        this.balanceHolder = balanceHolder;
    }

    public AddressEntity getType() {
        return type;
    }

    public void setType(AddressEntity type) {
        this.type = type;
    }

    public AddressSyncStatus getStatus() {
        return status;
    }

    public void setStatus(AddressSyncStatus status) {
        this.status = status;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).omitNullValues()
                .add("id", id)
                .add("objectId", objectId)
                .add("parentId", parentId)
                .add("externalId", externalId)
                .add("additionalExternalId", additionalExternalId)
                .add("name", name)
                .add("additionalName", additionalName)
                .add("type", type)
                .add("status", status)
                .add("date", date)
                .toString();
    }
}
