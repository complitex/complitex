package ru.complitex.sync.entity;

import ru.complitex.catalog.util.Strings;

/**
 * @author Ivanov Anatoliy
 */
public class Sync {
    private Long parentId;
    private String additionalParentId;
    private Long externalId;
    private String additionalExternalId;
    private String name;
    private String additionalName;
    private String altName;
    private String altAdditionalName;
    private Long servicingOrganization;
    private Long balanceHolder;

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
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

    public Long getServicingOrganization() {
        return servicingOrganization;
    }

    public void setServicingOrganization(Long servicingOrganization) {
        this.servicingOrganization = servicingOrganization;
    }

    public Long getBalanceHolder() {
        return balanceHolder;
    }

    public void setBalanceHolder(Long balanceHolder) {
        this.balanceHolder = balanceHolder;
    }

    @Override
    public String toString() {
        return Strings.toString(this);
    }
}
