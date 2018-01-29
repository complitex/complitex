package org.complitex.osznconnection.file.entity;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 27.08.2010 13:15:40
 *
 * Абстрактное представление записи файла запроса.
 * Используется <code>Map<String, Object></code> для хранения названий и значений полей.
 */
public abstract class AbstractRequest<E extends Enum> extends AbstractFieldMapEntity<E>{
    private Long id;
    private Long requestFileId;
    private Long groupId;
    private Long organizationId;
    private Long userOrganizationId;

    private RequestStatus status;

    private RequestFileType requestFileType;

    private List<RequestWarning> warnings = Lists.newArrayList();

    private Map<String, Object> updateFieldMap = new TreeMap<>();

    private Date date;

    public AbstractRequest(RequestFileType requestFileType) {
        this.requestFileType = requestFileType;
    }

    public Map<String, Object> getUpdateFieldMap() {
        return updateFieldMap;
    }

    public void setUpdateFieldMap(Map<String, Object> updateFieldMap) {
        this.updateFieldMap = updateFieldMap;
    }

    public void putUpdateField(E e, Object object) {
        updateFieldMap.put(e.name(), object);
    }

    public RequestFileType getRequestFileType(){
        return requestFileType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRequestFileId() {
        return requestFileId;
    }

    public void setRequestFileId(Long requestFileId) {
        this.requestFileId = requestFileId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
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

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public List<RequestWarning> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<RequestWarning> warnings) {
        this.warnings = warnings;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return getToStringHelper().toString();
    }

    protected MoreObjects.ToStringHelper getToStringHelper() {
        return super.getToStringHelper()
                .add("id", id)
                .add("requestFileId", requestFileId)
                .add("groupId", groupId)
                .add("organizationId", organizationId)
                .add("userOrganizationId", userOrganizationId)
                .add("status", status)
                .add("requestFileType", requestFileType)
                .add("warnings", warnings)
                .add("updateFieldMap", updateFieldMap)
                .add("date", date);
    }
}
