package ru.complitex.sync.service;

import ru.complitex.catalog.entity.Item;
import ru.complitex.sync.entity.Sync;
import ru.complitex.sync.entity.SyncParameter;

import java.io.Serializable;

/**
 * @author Ivanov Anatoliy
 */
public interface ISyncListener<T extends SyncParameter> extends Serializable {
    void onSync(int catalog, Sync sync);
    void onAdd(int catalog, Item item, Sync sync);
    void onAddCorrection(int catalog, Item correction, Sync sync);
    void onUpdate(int catalog, Item item, Sync sync);
    void onUpdateCorrection(int catalog, Item correction, Sync sync);
    void onDelete(int catalog, Item item, Sync sync);
    void onDeleteCorrection(int catalog, Item item, Sync sync);
    void onError(int catalog, T syncParameter, Item item, Item correction, Sync sync, Exception exception);
    void onSynced(int catalog);
}
