package org.complitex.common.entity.example;

import org.complitex.common.web.component.ShowMode;

import java.io.Serializable;
import java.util.*;

public class DomainObjectExample implements Serializable {
    private Long objectId;
    private Long parentId;
    private Long localeId;

    private String entityTable;
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

    private List<AttributeExample> attributeExamples = new ArrayList<AttributeExample>();

    private Map<String, Object> additionalParams = new HashMap<>();

    public DomainObjectExample() {
    }

    public DomainObjectExample(Long objectId) {
        this.objectId = objectId;
    }

    public DomainObjectExample(Long objectId, String entityTable) {
        this.objectId = objectId;
        this.entityTable = entityTable;
    }

    public DomainObjectExample(Long objectId, String entityTable, Date startDate) {
        this.objectId = objectId;
        this.entityTable = entityTable;
        this.startDate = startDate;
    }

    public DomainObjectExample addAttributes(Long... attributeTypeIds){
        for (Long a : attributeTypeIds){
            attributeExamples.add(new AttributeExample(a));
        }

        return this;
    }

    public DomainObjectExample setParent(String parentEntity, Long parentId){
        this.parentEntity = parentEntity;
        this.parentId = parentId;

        return this;
    }

    public DomainObjectExample addAttribute(Long attributeTypeId, String value){
        attributeExamples.add(new AttributeExample(attributeTypeId, value));

        return this;
    }

    public DomainObjectExample addAttribute(Long attributeTypeId, Long valueId){
        attributeExamples.add(new AttributeExample(attributeTypeId, valueId));

        return this;
    }

    public AttributeExample getAttributeExample(long attributeTypeId) {
        for (AttributeExample attrExample : attributeExamples) {
            if (attrExample.getAttributeTypeId().equals(attributeTypeId)) {
                return attrExample;
            }
        }
        return null;
    }

    public void addAttributeExample(AttributeExample attributeExample) {
        attributeExamples.add(attributeExample);
    }

    public DomainObjectExample addAdditionalParam(String key, Object value) {
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

    public String getEntityTable() {
        return entityTable;
    }

    public void setEntityTable(String entityTable) {
        this.entityTable = entityTable;
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

    public void setStatus(String status) {
        this.status = status;
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

    public List<AttributeExample> getAttributeExamples() {
        return attributeExamples;
    }

    public void setAttributeExamples(List<AttributeExample> attributeExamples) {
        this.attributeExamples = attributeExamples;
    }

    public Map<String, Object> getAdditionalParams() {
        return additionalParams;
    }

    public void setAdditionalParams(Map<String, Object> additionalParams) {
        this.additionalParams = additionalParams;
    }
}
