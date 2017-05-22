package org.complitex.common.entity;

import org.complitex.common.util.Locales;

import java.util.*;
import java.util.stream.Collectors;

public class DomainObject implements ILongId {
    private Long pkId;
    private String entityName;
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

    public void addAttribute(Attribute attribute) {
        if (attribute != null) {
            attributes.add(attribute);
        }
    }

    public void addAttributePair(Long attributeTypeId1, Long attributeTypeId2){
        Long attributeId = attributes.stream()
                .filter(a -> a.getAttributeTypeId().equals(attributeTypeId1))
                .mapToLong(Attribute::getAttributeId)
                .max()
                .orElse(0) + 1;

        attributes.add(new Attribute(attributeTypeId1, attributeId));
        attributes.add(new Attribute(attributeTypeId2, attributeId));
    }

    public void addAttribute(Long attributeTypeId, Long valueId){
        Long attributeId = attributes.stream()
                .filter(a -> a.getAttributeTypeId().equals(attributeTypeId))
                .mapToLong(Attribute::getAttributeId)
                .max()
                .orElse(0) + 1;

        Attribute attribute = new Attribute();
        attribute.setAttributeId(attributeId);
        attribute.setAttributeTypeId(attributeTypeId);
        attribute.setValueTypeId(attributeTypeId);
        attribute.setValueId(valueId);
    }

    public Attribute getAttribute(Long attributeTypeId) {
        return attributes.stream()
                .filter(a -> a.getAttributeTypeId().equals(attributeTypeId))
                .filter(a -> a.getEndDate() == null)
                .findAny()
                .orElse(null);
    }

    public Attribute getAttribute(Long attributeTypeId, Long attributeId){
        return attributes.stream()
                .filter(a -> a.getEndDate() == null)
                .filter(a -> a.getAttributeTypeId().equals(attributeTypeId))
                .filter(a -> a.getAttributeId().equals(attributeId))
                .findAny()
                .orElse(null);
    }

    public Attribute getAttribute(Attribute attribute){
        return getAttribute(attribute.getAttributeTypeId(), attribute.getAttributeId());
    }


    public List<Attribute> getAttributes(Long attributeTypeId) {
        return attributes.stream()
                .filter(a -> a.getAttributeTypeId().equals(attributeTypeId))
                .filter(a -> a.getEndDate() == null)
                .collect(Collectors.toList());
    }

    public List<Long> getValueIds(Long attributeTypeId){
        return attributes.stream()
                .filter(a -> a.getAttributeTypeId().equals(attributeTypeId))
                .filter(a -> a.getEndDate() == null)
                .map(Attribute::getValueId)
                .collect(Collectors.toList());
    }

    public void removeAttribute(Long attributeTypeId) {
        attributes.removeIf(attribute -> attribute.getAttributeTypeId().equals(attributeTypeId));
    }

    public void removeAttribute(Long attributeTypeId, Long attributeId){
        attributes.removeIf(a -> a.getAttributeTypeId().equals(attributeTypeId) && a.getAttributeId().equals(attributeId));
    }

    public String getStringValue(Long attributeTypeId){
        Attribute attribute = getAttribute(attributeTypeId);

        return attribute != null? attribute.getStringValue() : null;
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

    public Long getValue(Long attributeTypeId){
        Attribute attribute =  getAttribute(attributeTypeId);

        return attribute != null ? attribute.getValueId() : null;
    }

    public void setValue(Long attributeTypeId, Long value){
        getAttribute(attributeTypeId).setValueId(value);
    }

    @Override
    public Long getId() {
        return objectId;
    }

    public Long getPkId() {
        return pkId;
    }

    public void setPkId(Long pkId) {
        this.pkId = pkId;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
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
