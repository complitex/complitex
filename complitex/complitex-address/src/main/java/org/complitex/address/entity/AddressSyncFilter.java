package org.complitex.address.entity;

import org.complitex.common.entity.AbstractEntity;

/**
 * @author inheaven on 019 19.08.15 19:15
 */
public class AddressSyncFilter extends AbstractEntity{
    private Long count;
    private AddressSyncStatus status;

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public AddressSyncStatus getStatus() {
        return status;
    }

    public void setStatus(AddressSyncStatus status) {
        this.status = status;
    }
}
