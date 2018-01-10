package org.complitex.sync.entity;

import org.complitex.common.entity.ILongId;

import java.util.Date;

import static org.complitex.sync.entity.SyncEntity.BUILDING;

/**
 * @author Anatoly Ivanov
 *         Date: 29.07.2014 22:35
 */
public class DomainSync implements ILongId {
    private Long id;
    private Long objectId;
    private Long parentId;
    private Long additionalParentId;
    private String externalId;
    private String additionalExternalId;
    private String name;
    private String additionalName;
    private String altName;
    private String altAdditionalName;
    private String servicingOrganization;
    private String balanceHolder;
    private SyncEntity type;
    private DomainSyncStatus status;
    private DomainSyncStatusDetail statusDetail;

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

    public String getAltName() {
        return altName;
    }

    public void setAltName(String altName) {
        this.altName = altName;
    }

    public String getAltAdditionalName() {
        return altAdditionalName;
    }

    public void setAltAdditionalName(String altAdditionalName) {
        this.altAdditionalName = altAdditionalName;
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

    public SyncEntity getType() {
        return type;
    }

    public void setType(SyncEntity type) {
        this.type = type;
    }

    public DomainSyncStatus getStatus() {
        return status;
    }

    public void setStatus(DomainSyncStatus status) {
        this.status = status;
    }

    public DomainSyncStatusDetail getStatusDetail() {
        return statusDetail;
    }

    public void setStatusDetail(DomainSyncStatusDetail statusDetail) {
        this.statusDetail = statusDetail;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "DomainSync{" +
                "id=" + id +
                ", objectId=" + objectId +
                ", parentId=" + parentId +
                ", additionalParentId=" + additionalParentId +
                ", externalId='" + externalId + '\'' +
                ", additionalExternalId='" + additionalExternalId + '\'' +
                ", name='" + name + '\'' +
                ", additionalName='" + additionalName + '\'' +
                ", altName='" + altName + '\'' +
                ", altAdditionalName='" + altAdditionalName + '\'' +
                ", servicingOrganization='" + servicingOrganization + '\'' +
                ", balanceHolder='" + balanceHolder + '\'' +
                ", type=" + type +
                ", status=" + status +
                ", statusDetail=" + statusDetail +
                ", date=" + date +
                '}';
    }
}
