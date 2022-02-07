package ru.complitex.sync.entity;

import ru.complitex.common.entity.AbstractEntity;

/**
 * @author inheaven on 019 19.08.15 19:15
 */
public class DomainSyncFilter extends AbstractEntity{
    private Long count;
    private DomainSyncStatus status;

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public DomainSyncStatus getStatus() {
        return status;
    }

    public void setStatus(DomainSyncStatus status) {
        this.status = status;
    }
}
