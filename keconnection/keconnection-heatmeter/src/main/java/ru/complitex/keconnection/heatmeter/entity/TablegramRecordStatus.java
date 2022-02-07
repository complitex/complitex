package ru.complitex.keconnection.heatmeter.entity;

import ru.complitex.common.entity.IFixedIdType;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 08.10.12 16:11
 */
public enum TablegramRecordStatus implements IFixedIdType{
    LOADED(1), PROCESSED(2), HEATMETER_NOT_FOUND(3), ALREADY_HAS_PAYLOAD(4), ERROR_PAYLOAD_SUM(5);

    private Integer id;

    private TablegramRecordStatus(Integer id) {
        this.id = id;
    }

    @Override
    public Integer getId() {
        return id;
    }
}
