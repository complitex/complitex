package ru.complitex.sync.entity;

import com.google.common.base.MoreObjects;
import ru.complitex.common.entity.ILongId;

import java.util.Date;

/**
 * @author Anatoly Ivanov
 *         Date: 29.07.2014 22:35
 */
public class DomainSync implements ILongId {
    private Long id;
    private Long parentId;
    private String additionalParentId;
    private Long externalId;
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

    public DomainSync() {
    }

    public DomainSync(SyncEntity type, Long externalId) {
        this.type = type;
        this.externalId = externalId;
    }

    public DomainSync(SyncEntity type, DomainSyncStatus status) {
        this.type = type;
        this.status = status;
    }

    public DomainSync(SyncEntity type, DomainSyncStatus status, Long externalId) {
        this.type = type;
        this.status = status;
        this.externalId = externalId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParentId() {
        return parentId;
    }

    public DomainSync setParentId(Long parentId) {
        this.parentId = parentId;

        return this;
    }

    public String getAdditionalParentId() {
        return additionalParentId;
    }

    public void setAdditionalParentId(String additionalParentId) {
        this.additionalParentId = additionalParentId;
    }

    public Long getExternalId() {
        return externalId;
    }

    public void setExternalId(Long externalId) {
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
        return MoreObjects.toStringHelper(this).omitNullValues()
                .add("id", id)
                .add("parentId", parentId)
                .add("additionalParentId", additionalParentId)
                .add("externalId", externalId)
                .add("additionalExternalId", additionalExternalId)
                .add("name", name)
                .add("additionalName", additionalName)
                .add("altName", altName)
                .add("altAdditionalName", altAdditionalName)
                .add("servicingOrganization", servicingOrganization)
                .add("balanceHolder", balanceHolder)
                .add("type", type)
                .add("status", status)
                .add("statusDetail", statusDetail)
                .add("date", date)
                .toString();
    }
}
