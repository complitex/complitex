package ru.complitex.sync.entity;

/**
 * @author inheaven on 15.07.2015 18:40.
 */
public class SyncBeginMessage {
    private SyncEntity syncEntity;
    private String parentName;
    private Long count;

    public SyncEntity getSyncEntity() {
        return syncEntity;
    }

    public void setSyncEntity(SyncEntity syncEntity) {
        this.syncEntity = syncEntity;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
