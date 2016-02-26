package org.complitex.osznconnection.file.entity;

import org.complitex.common.mybatis.IFixedIdType;

/**
* User: Anatoly A. Ivanov java@inhell.ru
* Date: 19.01.11 23:18
*/
public enum RequestFileStatus implements IFixedIdType {
    SKIPPED(100),
    LOADING(112),   LOAD_ERROR(111),   LOADED(110),
    BINDING(122),   BIND_ERROR(121),   BOUND(120),
    FILLING(132),   FILL_ERROR(131),   FILLED(130),
    SAVING(142),    SAVE_ERROR(141),   SAVED(140),
    EXPORTING(152), EXPORT_ERROR(151), EXPORTED(150);

    private Integer id;

    RequestFileStatus(Integer id) {
        this.id = id;
    }

    @Override
    public Integer getId() {
        return id;
    }

    public boolean isProcessing(){
        return this.equals(LOADING) || this.equals(BINDING) || this.equals(FILLING) || this.equals(SAVING);

    }
}
