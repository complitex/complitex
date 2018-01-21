package org.complitex.correction.entity;

import com.google.common.base.MoreObjects;
import org.complitex.common.util.DateUtil;

import java.io.Serializable;
import java.util.Date;

/**
 * Объект коррекции
 * @author Anatoly A. Ivanov java@inheaven.ru
 */
public class Correction implements Serializable {
    private String entityName;

    private Long id;
    private Long parentId;
    private Long additionalParentId;
    private Long externalId;
    private Long objectId;
    private String correction;
    private String additionalCorrection;
    private Date beginDate = DateUtil.MIN_BEGIN_DATE;
    private Date endDate = DateUtil.MAX_END_DATE;
    private Long organizationId;
    private Long userOrganizationId;
    private Long moduleId;

    private String organizationName;
    private String userOrganizationName;

    private String internalObject;
    private String displayObject;

    private boolean editable = true;



    protected Correction() {
    }

    public Correction(String entityName, Long parentId, Long additionalParentId, Long externalId, Long objectId,
                      String correction, String additionalCorrection, Long organizationId, Long userOrganizationId) {
        this.entityName = entityName;
        this.parentId = parentId;
        this.additionalParentId = additionalParentId;
        this.externalId = externalId;
        this.objectId = objectId;
        this.correction = correction;
        this.additionalCorrection = additionalCorrection;
        this.organizationId = organizationId;
        this.userOrganizationId = userOrganizationId;
    }

    public Correction(String entityName, Long parentId, Long additionalParentId, Long externalId, Long objectId,
                      String correction, Long organizationId, Long userOrganizationId) {
        this(entityName, parentId, additionalParentId, externalId, objectId, correction, null,
                organizationId, userOrganizationId);
    }

    public Correction(String entityName, Long parentId, Long externalId, Long objectId, String correction,
                      String additionalCorrection, Long organizationId, Long userOrganizationId) {
        this(entityName, parentId, null, externalId, objectId, correction, organizationId, userOrganizationId);
    }

    public Correction(String entityName, Long parentId, Long externalId, Long objectId, String correction,
                      Long organizationId, Long userOrganizationId) {
        this(entityName, parentId, null, externalId, objectId, correction, organizationId, userOrganizationId);
    }

    public Correction(String entityName, Long externalId, Long objectId, String correction,
                      Long organizationId, Long userOrganizationId) {
        this(entityName, null, externalId, objectId, correction, organizationId, userOrganizationId);
    }

    public Correction(String correction, Long organizationId, Long userOrganizationId) {
        this.correction = correction;
        this.organizationId = organizationId;
        this.userOrganizationId = userOrganizationId;
    }

    public Correction(String entityName, Long id) {
        this.entityName = entityName;
        this.id = id;
    }

    public Correction toUpperCase(){
        if (correction != null) {
            correction = correction.toUpperCase();
        }

        return this;
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

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Long getAdditionalParentId() {
        return additionalParentId;
    }

    public void setAdditionalParentId(Long additionalParentId) {
        this.additionalParentId = additionalParentId;
    }

    public Long getExternalId() {
        return externalId;
    }

    public void setExternalId(Long externalId) {
        this.externalId = externalId;
    }

    public Long getObjectId() {
        return objectId;
    }

    public void setObjectId(Long objectId) {
        this.objectId = objectId;
    }

    public String getCorrection() {
        return correction;
    }

    public void setCorrection(String correction) {
        this.correction = correction;
    }

    public String getAdditionalCorrection() {
        return additionalCorrection;
    }

    public void setAdditionalCorrection(String additionalCorrection) {
        this.additionalCorrection = additionalCorrection;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Long getUserOrganizationId() {
        return userOrganizationId;
    }

    public void setUserOrganizationId(Long userOrganizationId) {
        this.userOrganizationId = userOrganizationId;
    }

    public Long getModuleId() {
        return moduleId;
    }

    public void setModuleId(Long moduleId) {
        this.moduleId = moduleId;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getUserOrganizationName() {
        return userOrganizationName;
    }

    public void setUserOrganizationName(String userOrganizationName) {
        this.userOrganizationName = userOrganizationName;
    }

    public String getInternalObject() {
        return internalObject;
    }

    public void setInternalObject(String internalObject) {
        this.internalObject = internalObject;
    }

    public String getDisplayObject() {
        return displayObject;
    }

    public void setDisplayObject(String displayObject) {
        this.displayObject = displayObject;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    protected MoreObjects.ToStringHelper getToStringHelper(){
        return MoreObjects.toStringHelper(this).omitNullValues()
                .add("id", id)
                .add("externalId", externalId)
                .add("parentId", parentId)
                .add("additionalParentId", additionalParentId)
                .add("objectId", objectId)
                .add("correction", correction)
                .add("additionalCorrection", additionalCorrection)
                .add("beginDate", beginDate)
                .add("endDate", endDate)
                .add("organizationId", organizationId)
                .add("userOrganizationId", userOrganizationId)
                .add("moduleId", moduleId)
                .add("organizationName", organizationName)
                .add("userOrganizationName", userOrganizationName)
                .add("internalObject", internalObject)
                .add("displayObject", displayObject);
    }

    @Override
    public String toString() {
        return  getToStringHelper().toString();
    }
}
