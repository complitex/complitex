package org.complitex.osznconnection.file.entity;

/**
 * inheaven on 17.03.2016.
 */
public abstract class AbstractRequestFile extends AbstractExecutorObject{
    private Long id;

    private Long organizationId;
    private Long userOrganizationId;

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
}
