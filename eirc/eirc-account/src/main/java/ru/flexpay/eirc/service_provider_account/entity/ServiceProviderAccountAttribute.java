package ru.flexpay.eirc.service_provider_account.entity;

import ru.complitex.common.entity.Attribute;

/**
 * @author Pavel Sknar
 */
public class ServiceProviderAccountAttribute extends Attribute {

    private Long pkId;

    public ServiceProviderAccountAttribute() {
    }

    public ServiceProviderAccountAttribute(Long pkId) {
        this.pkId = pkId;
    }

    public Long getPkId() {
        return pkId;
    }

    public void setPkId(Long pkId) {
        this.pkId = pkId;
    }
}
