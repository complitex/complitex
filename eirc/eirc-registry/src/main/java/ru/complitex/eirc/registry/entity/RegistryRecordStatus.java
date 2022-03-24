package ru.complitex.eirc.registry.entity;

import ru.complitex.common.entity.IFixedIdType;
import ru.complitex.common.entity.ILocalizedType;
import ru.complitex.common.util.ResourceUtil;
import ru.complitex.correction.entity.AddressLinkStatus;
import ru.complitex.correction.entity.LinkStatus;

import java.util.Locale;

/**
 * @author Pavel Sknar
 */
public enum RegistryRecordStatus implements IFixedIdType, ILocalizedType {

    LOADED_WITH_ERROR(1),
    LOADED(2),
    LINKED_WITH_ERROR(3),
    LINKED(4),
    PROCESSED_WITH_ERROR(5),
    PROCESSED(6),
    ;

    private static final String RESOURCE_BUNDLE = RegistryRecordStatus.class.getName();

    private Integer id;

    private RegistryRecordStatus(Integer id) {
        this.id = id;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public String getLabel(Locale locale) {
        return ResourceUtil.getString(RESOURCE_BUNDLE, String.valueOf(getId()), locale);
    }

    public static RegistryRecordStatus getRegistryRecordStatus(LinkStatus linkStatus) {
        return linkStatus instanceof AddressLinkStatus && linkStatus != AddressLinkStatus.ADDRESS_LINKED ? LINKED_WITH_ERROR : null;
    }
}
