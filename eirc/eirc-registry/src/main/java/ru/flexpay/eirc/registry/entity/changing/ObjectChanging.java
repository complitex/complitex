package ru.flexpay.eirc.registry.entity.changing;

import ru.flexpay.eirc.dictionary.entity.DictionaryObject;

/**
 * @author Pavel Sknar
 */
public class ObjectChanging extends DictionaryObject {
    private Long oldPkId;
    private Long newPkId;
    private Long registryRecordContainerId;
    private String objectType;

    public ObjectChanging() {
    }

    public ObjectChanging(Long oldPkId) {
        this.oldPkId = oldPkId;
    }

    public ObjectChanging(Long oldPkId, Long newPkId, Long registryRecordContainerId) {
        this.oldPkId = oldPkId;
        this.newPkId = newPkId;
        this.registryRecordContainerId = registryRecordContainerId;
    }

    public Long getOldPkId() {
        return oldPkId;
    }

    public void setOldPkId(Long oldPkId) {
        this.oldPkId = oldPkId;
    }

    public Long getNewPkId() {
        return newPkId;
    }

    public void setNewPkId(Long newPkId) {
        this.newPkId = newPkId;
    }

    public Long getRegistryRecordContainerId() {
        return registryRecordContainerId;
    }

    public void setRegistryRecordContainerId(Long registryRecordContainerId) {
        this.registryRecordContainerId = registryRecordContainerId;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }
}
