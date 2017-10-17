package ru.complitex.pspoffice.api.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author Anatoly A. Ivanov
 * 10.10.2017 13:20
 */
public class DomainModel implements Serializable {
    private Long id;
    private Long parentEntityId;
    private Long parentId;
    private String externalId;
    private Date startDate;
    private Date endDate;
    private Integer statusId;
    private Set<Long> subjectIds;

    private List<DomainAttributeModel> attributes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParentEntityId() {
        return parentEntityId;
    }

    public void setParentEntityId(Long parentEntityId) {
        this.parentEntityId = parentEntityId;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }

    public Set<Long> getSubjectIds() {
        return subjectIds;
    }

    public void setSubjectIds(Set<Long> subjectIds) {
        this.subjectIds = subjectIds;
    }

    public List<DomainAttributeModel> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<DomainAttributeModel> attributes) {
        this.attributes = attributes;
    }
}
