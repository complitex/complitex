package org.complitex.address.entity;

/**
 * @author inheaven on 15.07.2015 18:40.
 */
public class SyncBeginMessage {
    private String type;
    private String parentName;
    private Long count;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
