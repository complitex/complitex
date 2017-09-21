package org.complitex.osznconnection.file.entity;

import org.complitex.entity.IFixedIdType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
* User: Anatoly A. Ivanov java@inhell.ru
* Date: 19.01.11 23:18
*/
public enum RequestFileStatus implements IFixedIdType {
    SKIPPED(100),
    LOAD_WAIT(113), LOADING(112),   LOAD_ERROR(111),   LOADED(110),
    BIND_WAIT(123), BINDING(122),   BIND_ERROR(121),   BOUND(120),
    FILL_WAIT(133), FILLING(132),   FILL_ERROR(131),   FILLED(130),
    SAVE_WAIT(143), SAVING(142),    SAVE_ERROR(141),   SAVED(140),
    EXPORT_WAIT(153), EXPORTING(152), EXPORT_ERROR(151), EXPORTED(150);

    public static final Set<RequestFileStatus> PROCESSING = new HashSet<>(Arrays.asList(
            LOADING, BINDING, FILLING, SAVING, EXPORTING));

    public static final Set<RequestFileStatus> WAITING = new HashSet<>(Arrays.asList(
            LOAD_WAIT, BIND_WAIT, FILL_WAIT, SAVE_WAIT, EXPORT_WAIT));

    private Integer id;

    RequestFileStatus(Integer id) {
        this.id = id;
    }

    @Override
    public Integer getId() {
        return id;
    }

    public boolean isProcessing(){
        return PROCESSING.contains(this);
    }

    public boolean isWaiting(){
        return WAITING.contains(this);
    }
}
