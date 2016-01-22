package org.complitex.address.strategy.building.entity;

import org.complitex.common.entity.ILongId;

import java.util.Objects;

public class BuildingCode implements ILongId {

    private Long id;
    private Long organizationId;
    private Long buildingCode;
    private Long buildingId;

    public BuildingCode() {
    }

    public BuildingCode(Long organizationId, Long buildingCode) {
        this.organizationId = organizationId;
        this.buildingCode = buildingCode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBuildingCode() {
        return buildingCode;
    }

    public void setBuildingCode(Long buildingCode) {
        this.buildingCode = buildingCode;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Long getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(Long buildingId) {
        this.buildingId = buildingId;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BuildingCode other = (BuildingCode) obj;
        if (!Objects.equals(this.organizationId, other.organizationId)) {
            return false;
        }
        if (!Objects.equals(this.buildingCode, other.buildingCode)) {
            return false;
        }
        if (!Objects.equals(this.buildingId, other.buildingId)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int hash = 17;
        hash = 31 * hash + Objects.hashCode(this.organizationId);
        hash = 31 * hash + Objects.hashCode(this.buildingCode);
        hash = 31 * hash + Objects.hashCode(this.buildingId);
        return hash;
    }
}
