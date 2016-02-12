package ru.flexpay.eirc.registry.entity;

import org.complitex.common.entity.ILocalizedType;
import org.complitex.common.mybatis.IFixedIdType;
import org.complitex.common.util.ResourceUtil;

import java.util.Locale;

/**
 * @author Pavel Sknar
 */
public enum  RegistryStatus implements IFixedIdType, ILocalizedType {
    LOADING(0),
    LOADING_WITH_ERROR(1),
    LOADED(2),
    LOADING_CANCELED(3),
    LOADED_WITH_ERROR(4),

    PROCESSING(5),
    PROCESSING_WITH_ERROR(6),
    PROCESSED(7),
    PROCESSED_WITH_ERROR(8),
    PROCESSING_CANCELED(9),

    ROLLBACKING(10),
    ROLLBACKED(11),

    PROCESSED_PARTLY(12),

    LINKING(19),
    LINKING_WITH_ERROR(20),
    LINKED(21),
    LINKED_WITH_ERROR(22),
    LINKING_CANCELED(23);

    private static final String RESOURCE_BUNDLE = RegistryStatus.class.getName();
    private static final Locale DEFAULT_LOCALE = new Locale("ru");

    private Integer id;

    private RegistryStatus(Integer id) {
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


    @Override
    public String toString() {
        return getLabel(DEFAULT_LOCALE);
    }
}
