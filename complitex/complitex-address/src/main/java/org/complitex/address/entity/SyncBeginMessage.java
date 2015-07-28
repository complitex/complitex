package org.complitex.address.entity;

/**
 * @author inheaven on 15.07.2015 18:40.
 */
public class SyncBeginMessage {
    private AddressEntity addressEntity;
    private String parentName;
    private Long count;

    public AddressEntity getAddressEntity() {
        return addressEntity;
    }

    public void setAddressEntity(AddressEntity addressEntity) {
        this.addressEntity = addressEntity;
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
