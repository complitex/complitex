package org.complitex.common.entity;

import org.complitex.common.web.component.ShowMode;

import java.io.Serializable;
import java.util.*;

public class DomainObjectFilter implements Serializable {
    public enum ComparisonType {
        LIKE, EQUALITY
    }

    private Long objectId;
    private Long parentId;
    private Long localeId;

    private String entityName;
    private String parentEntity;

    private long first;
    private long count;

    private Long orderByAttributeTypeId;
    private boolean orderByNumber;
    private boolean asc;

    private Date startDate;
    private String comparisonType = ComparisonType.LIKE.name();
    private String status = ShowMode.ALL.name();

    private String userPermissionString;
    private boolean admin;

    private List<AttributeFilter> attributeFilters = new ArrayList<AttributeFilter>();

    private Map<String, Object> additionalParams = new HashMap<>();

    private String userOrganizationsString;

    private String value;

    private String externalId;

    public DomainObjectFilter() {
    }

    public DomainObjectFilter(String entityName) {
        this.entityName = entityName;
    }

    public DomainObjectFilter(Long objectId) {
        this.objectId = objectId;
    }

    public DomainObjectFilter(Long objectId, String entityName) {
        this.objectId = objectId;
        this.entityName = entityName;
    }

    public DomainObjectFilter(Long objectId, String entityName, Date startDate) {
        this.objectId = objectId;
        this.entityName = entityName;
        this.startDate = startDate;
    }

    public DomainObjectFilter addAttributes(Long... entityAttributeIds){
        for (Long a : entityAttributeIds){
            attributeFilters.add(new AttributeFilter(a));
        }

        return this;
    }

    public DomainObjectFilter setParent(String parentEntity, Long parentId){
        this.parentEntity = parentEntity;
        this.parentId = parentId;

        return this;
    }

    public DomainObjectFilter addAttribute(Long entityAttributeId, String value){
        attributeFilters.add(new AttributeFilter(entityAttributeId, value));

        return this;
    }

    public DomainObjectFilter addAttribute(Long entityAttributeId, Long valueId){
        attributeFilters.add(new AttributeFilter(entityAttributeId, valueId));

        return this;
    }

    public AttributeFilter getAttributeExample(long entityAttributeId) {
        for (AttributeFilter attrExample : attributeFilters) {
            if (attrExample.getEntityAttributeId().equals(entityAttributeId)) {
                return attrExample;
            }
        }

        return null;
    }

    public void addAttributeFilter(AttributeFilter attributeFilter) {
        attributeFilters.add(attributeFilter);
    }

    public DomainObjectFilter addAdditionalParam(String key, Object value) {
        additionalParams.put(key, value);

        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> T getAdditionalParam(String key) {
        return additionalParams != null ? (T) additionalParams.get(key) : null;
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

    public Long getLocaleId() {
        return localeId;
    }

    public void setLocaleId(Long localeId) {
        this.localeId = localeId;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getParentEntity() {
        return parentEntity;
    }

    public void setParentEntity(String parentEntity) {
        this.parentEntity = parentEntity;
    }

    public long getFirst() {
        return first;
    }

    public void setFirst(long first) {
        this.first = first;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public Long getOrderByAttributeTypeId() {
        return orderByAttributeTypeId;
    }

    public void setOrderByAttributeTypeId(Long orderByAttributeTypeId) {
        this.orderByAttributeTypeId = orderByAttributeTypeId;
    }

    public boolean isOrderByNumber() {
        return orderByNumber;
    }

    public void setOrderByNumber(boolean orderByNumber) {
        this.orderByNumber = orderByNumber;
    }

    public boolean isAsc() {
        return asc;
    }

    public void setAsc(boolean asc) {
        this.asc = asc;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getComparisonType() {
        return comparisonType;
    }

    public void setComparisonType(String comparisonType) {
        this.comparisonType = comparisonType;
    }

    public String getStatus() {
        return status;
    }

    public DomainObjectFilter setStatus(String status) {
        this.status = status;

        return this;
    }

    public String getUserPermissionString() {
        return userPermissionString;
    }

    public void setUserPermissionString(String userPermissionString) {
        this.userPermissionString = userPermissionString;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public List<AttributeFilter> getAttributeFilters() {
        return attributeFilters;
    }

    public void setAttributeFilters(List<AttributeFilter> attributeFilters) {
        this.attributeFilters = attributeFilters;
    }

    public Map<String, Object> getAdditionalParams() {
        return additionalParams;
    }

    public void setAdditionalParams(Map<String, Object> additionalParams) {
        this.additionalParams = additionalParams;
    }

    public String getUserOrganizationsString() {
        return userOrganizationsString;
    }

    public void setUserOrganizationsString(String userOrganizationsString) {
        this.userOrganizationsString = userOrganizationsString;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }
}
