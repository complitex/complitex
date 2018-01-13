package org.complitex.common.entity;

import com.google.common.base.MoreObjects;
import org.complitex.common.converter.BooleanConverter;
import org.complitex.common.converter.DateConverter;
import org.complitex.common.converter.IConverter;
import org.complitex.common.util.Locales;

import java.util.*;
import java.util.stream.Collectors;

import static org.complitex.common.util.Locales.getLanguage;

public class DomainObject implements ILongId {
    private Long pkId;
    private String entityName;
    private Long objectId;
    private Status status = Status.ACTIVE;
    private Date startDate;
    private Date endDate;
    private Long parentId;
    private Long parentEntityId;
    private Long permissionId;

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
        attributes = copy.attributes;
        subjectIds = copy.subjectIds;
    }

    public void addAttribute(Attribute attribute) {
        if (attribute != null) {
            attributes.add(attribute);
        }
    }

    public void addAttributePair(Long entityAttributeId1, Long entityAttributeId2){
        Long attributeId = attributes.stream()
                .filter(a -> a.getEntityAttributeId().equals(entityAttributeId1))
                .mapToLong(Attribute::getAttributeId)
                .max()
                .orElse(0) + 1;

        attributes.add(new Attribute(entityAttributeId1, attributeId));
        attributes.add(new Attribute(entityAttributeId2, attributeId));
    }

    public void addAttribute(Long entityAttributeId, Long valueId){
        Long attributeId = attributes.stream()
                .filter(a -> a.getEntityAttributeId().equals(entityAttributeId))
                .mapToLong(Attribute::getAttributeId)
                .max()
                .orElse(0) + 1;

        Attribute attribute = new Attribute();
        attribute.setAttributeId(attributeId);
        attribute.setEntityAttributeId(entityAttributeId);
        attribute.setValueId(valueId);
    }

    public Attribute getAttribute(Long entityAttributeId) {
        return attributes.stream()
                .filter(a -> a.getEntityAttributeId().equals(entityAttributeId))
                .filter(a -> a.getEndDate() == null)
                .findAny()
                .orElse(null);
    }

    public Attribute getAttribute(Long entityAttributeId, Long attributeId){
        return attributes.stream()
                .filter(a -> a.getEndDate() == null)
                .filter(a -> a.getEntityAttributeId().equals(entityAttributeId))
                .filter(a -> a.getAttributeId().equals(attributeId))
                .findAny()
                .orElse(null);
    }

    public Attribute getAttribute(Attribute attribute){
        return getAttribute(attribute.getEntityAttributeId(), attribute.getAttributeId());
    }


    public List<Attribute> getAttributes(Long entityAttributeId) {
        return attributes.stream()
                .filter(a -> a.getEntityAttributeId().equals(entityAttributeId))
                .filter(a -> a.getEndDate() == null)
                .collect(Collectors.toList());
    }

    public List<Long> getValueIds(Long entityAttributeId){
        return attributes.stream()
                .filter(a -> a.getEntityAttributeId().equals(entityAttributeId))
                .filter(a -> a.getEndDate() == null)
                .map(Attribute::getValueId)
                .collect(Collectors.toList());
    }

    public void removeAttribute(Long entityAttributeId) {
        attributes.removeIf(attribute -> attribute.getEntityAttributeId().equals(entityAttributeId));
    }

    public void removeAttribute(Long entityAttributeId, Long attributeId){
        attributes.removeIf(a -> a.getEntityAttributeId().equals(entityAttributeId) && a.getAttributeId().equals(attributeId));
    }

    public String getStringValue(Long entityAttributeId){
        Attribute attribute = getAttribute(entityAttributeId);

        return attribute != null? attribute.getStringValue() : null;
    }

    public String getStringValue(Long entityAttributeId, Locale locale){
        Attribute attribute = getAttribute(entityAttributeId);

        return attribute != null ? attribute.getStringValue(locale) : null;
    }

    public void setStringValue(Long entityAttributeId, String value, Locale locale){
        Attribute attribute = getAttribute(entityAttributeId);

        if (attribute == null){
            addAttribute(attribute = new Attribute(entityAttributeId, 1L));
        }

        attribute.setStringValue(value, Locales.getLocaleId(locale));
    }

    public void setStringValue(Long entityAttributeId, String value){
        setStringValue(entityAttributeId, value, Locales.getSystemLocale());
    }

    public Map<String, String> getStringMap(Long entityAttributeId){
        if (getAttribute(entityAttributeId) == null || getAttribute(entityAttributeId).getStringValues().isEmpty()){
            return null;
        }

        return getAttribute(entityAttributeId).getStringValues().stream()
                .filter(s -> s.getValue() != null)
                .collect(Collectors.toMap(s -> getLanguage(s.getLocaleId()), StringValue::getValue));
    }

    public Long getValueId(Long entityAttributeId){
        Attribute attribute =  getAttribute(entityAttributeId);

        return attribute != null ? attribute.getValueId() : null;
    }

    public void setValueId(Long entityAttributeId, Long value){
        getAttribute(entityAttributeId).setValueId(value);
    }

    public <T> void setValueId(Long entityAttributeId, T value, IConverter<T> converter){
        setStringValue(entityAttributeId, converter.toString(value));
    }

    public void setDateValue(Long entityAttributeId, Date value){
        setValueId(entityAttributeId, value, new DateConverter());
    }

    public void setBooleanValue(Long entityAttributeId, Boolean value){
        setValueId(entityAttributeId, value, new BooleanConverter());
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
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

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).omitNullValues()
                .add("pkId", pkId)
                .add("entityName", entityName)
                .add("objectId", objectId)
                .add("status", status)
                .add("startDate", startDate)
                .add("endDate", endDate)
                .add("parentId", parentId)
                .add("parentEntityId", parentEntityId)
                .add("permissionId", permissionId)
                .add("attributes", attributes)
                .add("subjectIds", subjectIds)
                .toString();
    }
}
