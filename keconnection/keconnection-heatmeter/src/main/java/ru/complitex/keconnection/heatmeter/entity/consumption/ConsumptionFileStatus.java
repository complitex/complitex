package ru.complitex.keconnection.heatmeter.entity.consumption;

import ru.complitex.common.entity.IFixedIdType;

/**
 * @author inheaven on 016 16.03.15 19:06
 */
public enum ConsumptionFileStatus implements IFixedIdType {
    LOADED(11), LOADING(12), LOAD_ERROR(13),
    BOUND(31), BINDING(32), BIND_ERROR(33);

    private Integer id;

    ConsumptionFileStatus(Integer id) {
        this.id = id;
    }

    @Override
    public Integer getId() {
        return id;
    }
}
