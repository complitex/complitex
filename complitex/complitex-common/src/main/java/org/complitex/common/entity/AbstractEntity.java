package org.complitex.common.entity;

/**
 * inheaven on 13.11.2014 17:03.
 */
public class AbstractEntity implements ILongId {
    private Long id;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
