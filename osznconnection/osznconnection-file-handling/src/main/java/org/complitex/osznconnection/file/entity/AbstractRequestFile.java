package org.complitex.osznconnection.file.entity;

import org.complitex.common.util.DateUtil;

import java.io.File;
import java.util.Date;

/**
 * inheaven on 17.03.2016.
 */
public abstract class AbstractRequestFile extends AbstractExecutorObject{
    private Long id;
    private Long groupId;

    private String name;
    private String directory;
    private String absolutePath;

    private Long length;
    private String checkSum;

    private Integer dbfRecordCount = 0;
    private Integer loadedRecordCount = 0;
    private Integer bindedRecordCount = 0;
    private Integer filledRecordCount = 0;

    private Date beginDate;
    private Date endDate;

    private Date loaded = DateUtil.getCurrentDate();

    private RequestFileType type;
    private RequestFileSubType subType;

    private RequestFileStatus status;

    private Long organizationId;
    private Long userOrganizationId;
    private Long serviceProviderId;

    private Long userId;

    public int getMonth(){
        return DateUtil.getMonth(beginDate) + 1;
    }

    public int getYear(){
        return DateUtil.getYear(beginDate);
    }

    public String getFullName() {
        if (name == null) {
            return null;
        }

        return (directory != null && !directory.isEmpty() ? directory + File.separator : "") + name;
    }

    public String getShortName(){
        return name.substring(0, name.indexOf('.'));
    }

    @Override
    public String getObjectName() {
        return getFullName();
    }

    @Override
    public String getLogObjectName() {
        return getFullName();
    }

    public boolean isProcessing() {
        return status != null && status.isProcessing();
    }

    public String getEdrpou(){
        return null;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public Long getLength() {
        return length;
    }

    public void setLength(Long length) {
        this.length = length;
    }

    public String getCheckSum() {
        return checkSum;
    }

    public void setCheckSum(String checkSum) {
        this.checkSum = checkSum;
    }

    public Integer getDbfRecordCount() {
        return dbfRecordCount;
    }

    public void setDbfRecordCount(Integer dbfRecordCount) {
        this.dbfRecordCount = dbfRecordCount;
    }

    public Integer getLoadedRecordCount() {
        return loadedRecordCount;
    }

    public void setLoadedRecordCount(Integer loadedRecordCount) {
        this.loadedRecordCount = loadedRecordCount;
    }

    public Integer getBindedRecordCount() {
        return bindedRecordCount;
    }

    public void setBindedRecordCount(Integer bindedRecordCount) {
        this.bindedRecordCount = bindedRecordCount;
    }

    public Integer getFilledRecordCount() {
        return filledRecordCount;
    }

    public void setFilledRecordCount(Integer filledRecordCount) {
        this.filledRecordCount = filledRecordCount;
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

    public Date getLoaded() {
        return loaded;
    }

    public void setLoaded(Date loaded) {
        this.loaded = loaded;
    }

    public RequestFileType getType() {
        return type;
    }

    public void setType(RequestFileType type) {
        this.type = type;
    }

    public RequestFileSubType getSubType() {
        return subType;
    }

    public void setSubType(RequestFileSubType subType) {
        this.subType = subType;
    }

    @Override
    public RequestFileStatus getStatus() {
        return status;
    }

    public void setStatus(RequestFileStatus status) {
        this.status = status;
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

    public Long getServiceProviderId() {
        return serviceProviderId;
    }

    public void setServiceProviderId(Long serviceProviderId) {
        this.serviceProviderId = serviceProviderId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "AbstractRequestFile{" +
                "id=" + id +
                ", groupId=" + groupId +
                ", name='" + name + '\'' +
                ", directory='" + directory + '\'' +
                ", absolutePath='" + absolutePath + '\'' +
                ", length=" + length +
                ", checkSum='" + checkSum + '\'' +
                ", dbfRecordCount=" + dbfRecordCount +
                ", loadedRecordCount=" + loadedRecordCount +
                ", bindedRecordCount=" + bindedRecordCount +
                ", filledRecordCount=" + filledRecordCount +
                ", beginDate=" + beginDate +
                ", endDate=" + endDate +
                ", loaded=" + loaded +
                ", type=" + type +
                ", subType=" + subType +
                ", status=" + status +
                ", organizationId=" + organizationId +
                ", userOrganizationId=" + userOrganizationId +
                ", serviceProviderId=" + serviceProviderId +
                ", userId=" + userId +
                "} " + super.toString();
    }
}
