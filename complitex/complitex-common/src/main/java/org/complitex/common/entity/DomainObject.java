package org.complitex.common.entity;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.complitex.common.util.Locales;

import java.io.Serializable;
import java.util.*;
import java.util.Locale;

/**
 *
 * @author Artem
 */
public class DomainObject implements Serializable {
    private String entityTable;
    private Long objectId;
    private StatusType status = StatusType.ACTIVE;
    private Date startDate;
    private Date endDate;
    private Long parentId;
    private Long parentEntityId;
    private Long permissionId;
    private String externalId;

    private List<Attribute> attributes = new ArrayList<>();
    private Set<Long> subjectIds = new HashSet<>();

    public DomainObject() {
    }

    public DomainObject(Long objectId) {
        this.objectId = objectId;
    }

    protected DomainObject(DomainObject copy) {
        objectId = copy.objectId;
        status = copy.status;
        startDate = copy.startDate;
        endDate = copy.endDate;
        parentId = copy.parentId;
        parentEntityId = copy.parentEntityId;
        permissionId = copy.permissionId;
        externalId = copy.externalId;
        attributes = copy.attributes;
        subjectIds = copy.subjectIds;
    }


    public Attribute getAttribute(Long attributeTypeId) {
        for (Attribute a : attributes) {
            if (a.getAttributeTypeId().equals(attributeTypeId) && status.equals(StatusType.ACTIVE)) {
                return a;
            }
        }

        return null;
    }

    public List<Attribute> getAttributes(final Long attributeTypeId) {
        return Lists.newArrayList(Iterables.filter(attributes, new Predicate<Attribute>() {

            @Override
            public boolean apply(Attribute attr) {
                return attr.getAttributeTypeId().equals(attributeTypeId);
            }
        }));
    }

    public void removeAttribute(long attributeTypeId) {
        for (Iterator<Attribute> i = attributes.iterator(); i.hasNext();) {
            Attribute attribute = i.next();
            if (attribute.getAttributeTypeId().equals(attributeTypeId)) {
                i.remove();
            }
        }
    }

    public String getStringValue(Long attributeTypeId){
        return getAttribute(attributeTypeId).getStringValue();
    }

    public String getStringValue(Long attributeTypeId, Locale locale){
        Attribute attribute = getAttribute(attributeTypeId);

        return attribute != null ? attribute.getStringValue(locale) : null;
    }

    public void setStringValue(Long attributeTypeId, String value, Locale locale){
        getAttribute(attributeTypeId).setStringValue(value, Locales.getLocaleId(locale));
    }

    public void setStringValue(Long attributeTypeId, String value){
        setStringValue(attributeTypeId, value, Locales.getSystemLocale());
    }

    public void setLongValue(Long attributeTypeId, Long value){
        getAttribute(attributeTypeId).setValueId(value);
    }

    public String getEntityTable() {
        return entityTable;
    }

    public void setEntityTable(String entityTable) {
        this.entityTable = entityTable;
    }

    public Long getObjectId() {
        return objectId;
    }

    public void setObjectId(Long objectId) {
        this.objectId = objectId;
    }

    public StatusType getStatus() {
        return status;
    }

    public void setStatus(StatusType status) {
        this.status = status;
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

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Long getParentEntityId() {
        return parentEntityId;
    }

    public void setParentEntityId(Long parentEntityId) {
        this.parentEntityId = parentEntityId;
    }

    public Long getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(Long permissionId) {
        this.permissionId = permissionId;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }

    public Set<Long> getSubjectIds() {
        return subjectIds;
    }

    public void setSubjectIds(Set<Long> subjectIds) {
        this.subjectIds = subjectIds;
    }
}
